package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import pt.unl.fct.di.apdc.geo5.data.ActivateAccountData;
import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.UserData;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();
	
	public UserResource() {
		
	}
	
	@POST
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUser(UserData userData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		LOG.info("token: " + headers.getHeaderString("token"));
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get user: " + userData.username + " by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_GET, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(userData.username);
			Entity u = datastore.get(userKey);
			if (u == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} else {
		        JsonObject result = new JsonObject();
		        result.addProperty("user_username", u.getKey().getName());
		        result.addProperty("user_name", u.getString("user_name"));
		        result.addProperty("user_email", u.getString("user_email"));
		        result.addProperty("user_role", u.getString("user_role"));
		        result.addProperty("user_creation_time", u.getTimestamp("user_creation_time").toString());
		        result.addProperty("user_last_update_time", u.getTimestamp("user_last_update_time").toString());
		        result.addProperty("user_street", u.getString("user_street"));
		        result.addProperty("user_place", u.getString("user_place"));
		        result.addProperty("user_country", u.getString("user_country"));
		        result.addProperty("active_account", u.getBoolean("active_account"));
		        result.addProperty("user_birthday", u.getString("user_birthday"));
		        result.addProperty("user_zip_code", u.getString("user_zip_code"));
				LOG.info("Got user: " + userData.username + " for user: " + data.username);
				return Response.ok(g.toJson(result)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/activateAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response activateAccount(ActivateAccountData activateData) {
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(activateData.username);
			if (txn.get(userKey) == null) {
				LOG.warning("Failed activate attempt for username: " + activateData.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			Entity u = datastore.get(userKey);
			if (!u.getString("activation_code").equals(activateData.activationCode) && !u.getString("activation_code").equals("NULL")) {
				LOG.warning("Invalid activation code for username: " + activateData.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			else {
				u = Entity.newBuilder(userKey)
						.set("user_name", u.getString("user_name"))
						.set("user_pwd", u.getString("user_pwd"))
						.set("user_email", u.getString("user_email"))
						.set("user_role", u.getString("user_role"))
						.set("user_creation_time", u.getTimestamp("user_creation_time"))
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", u.getString("user_street"))
						.set("user_place", u.getString("user_place"))
						.set("user_country", u.getString("user_country"))
						.set("active_account", true)
						.set("activation_code", u.getString("activation_code"))
						.set("user_birthday", u.getString("user_birthday"))
						.set("user_zip_code", u.getString("user_zip_code"))
						.build();
				txn.update(u);
				LOG.info("Activated user: " + activateData.username + " with activation code: " + activateData.activationCode);
				txn.commit();
				return Response.ok("{}").build();
			}
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/refreshToken")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response refreshToken(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to refresh token for user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		AuthToken t = new AuthToken(data.username, data.role);
		String token = j.generateJwtToken(t);
		return Response.ok(g.toJson(token)).build();
	}
	
	@POST
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveUsers(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active users");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_LIST_ACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("active_account", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeUsersList = new ArrayList<Entity>();
		logs.forEachRemaining(activeUsersLog -> {
			activeUsersList.add(activeUsersLog);
		});
		LOG.info("Got list of active users");
		return Response.ok(g.toJson(activeUsersList)).build();
	}
	
	@POST
	@Path("/listInactive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listInactiveUsers(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list inactive users");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_LIST_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("active_account", false))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeUsersList = new ArrayList<Entity>();
		logs.forEachRemaining(activeUsersLog -> {
			activeUsersList.add(activeUsersLog);
		});
		LOG.info("Got list of inactive users");
		return Response.ok(g.toJson(activeUsersList)).build();
	}
	
	@POST
	@Path("/last24hlogins")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response last24hlogins(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get last 24h logins");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_LAST_24H_LOGINS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Timestamp yesterday = Timestamp.of(cal.getTime());
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("UserLog")
				.setFilter(
						CompositeFilter.and(
								PropertyFilter.hasAncestor(
										datastore.newKeyFactory().setKind("User").newKey(data.username)),
								PropertyFilter.ge("user_login_time", yesterday)
								)
						)
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Date> loginDates = new ArrayList<Date>();
		logs.forEachRemaining(userLog -> {
			loginDates.add(userLog.getTimestamp("user_login_time").toDate());
		});
		return Response.ok(g.toJson(loginDates)).build();
	}
	
	@POST
	@Path("/{username}/picture")
	public Response getUserPicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, 
			@PathParam("username") String username, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to get user picture: " + username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_GET_USER_PICTURE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		try {
			Key picturerKey = datastore.newKeyFactory().setKind("UserProfilePicture").newKey(username);
			Entity p = datastore.get(picturerKey);
			if (p == null) {
				return Response.status(Status.BAD_REQUEST).entity("Profile picture does not exist.").build();
			} else {
		        JsonObject result = new JsonObject();
		        result.addProperty("user_name", p.getKey().getName());
		        result.addProperty("file_name", p.getString("file_name"));
		        result.addProperty("file_type", p.getString("file_type"));
		        result.addProperty("file_upload_date", p.getTimestamp("file_upload_date").toString());
				LOG.info("User: " + data.username + " Got user profile picture for user: " + username);
				return Response.ok(g.toJson(result)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/makeAccountInactive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeAccountInactive(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to make user inactive");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_MAKE_ACCOUNT_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity u = datastore.get(userKey);		
			if (u == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			}
			else {
				u = Entity.newBuilder(userKey)
						.set("user_name", u.getString("user_name"))
						.set("user_pwd", u.getString("user_pwd"))
						.set("user_email", u.getString("user_email"))
						.set("user_role", u.getString("user_role"))
						.set("user_creation_time", u.getTimestamp("user_creation_time"))
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", u.getString("user_street"))
						.set("user_place", u.getString("user_place"))
						.set("user_country", u.getString("user_country"))
						.set("active_account", false)
						.set("activation_code", "NULL")
						.set("user_birthday", u.getString("user_birthday"))
						.set("user_zip_code", u.getString("user_zip_code"))
						.build();
				txn.update(u);
				LOG.info("User made inactive: " + data.username);
				txn.commit();
				return Response.ok("{}").build();				
			} 
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/listAllUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllUsers(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list all users");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_LIST_ALL_USERS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("user_role", "User"))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeUsersList = new ArrayList<Entity>();
		logs.forEachRemaining(activeUsersLog -> {
			activeUsersList.add(activeUsersLog);
		});
		LOG.info("Got list of all users");
		return Response.ok(g.toJson(activeUsersList)).build();
	}
	
	/**
	@POST
	@Path("/listAllAdmins")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAdmins(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list admin users");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_USER_LIST_ALL_ADMINS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(CompositeFilter.and(
						PropertyFilter.eq("user_role", "SU"),
						PropertyFilter.eq("user_role", "BOM"),
						PropertyFilter.eq("user_role", "BOP")
						))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeUsersList = new ArrayList<Entity>();
		logs.forEachRemaining(activeUsersLog -> {
			activeUsersList.add(activeUsersLog);
		});
		LOG.info("Got list of admin users");
		return Response.ok(g.toJson(activeUsersList)).build();
	}
	**/
	
}
