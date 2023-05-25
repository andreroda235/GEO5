package pt.unl.fct.di.apdc.geo5.backoffice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.RegisterBackOfficeData;
import pt.unl.fct.di.apdc.geo5.data.UpdateRoleData;
import pt.unl.fct.di.apdc.geo5.resources.LoginResource;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/backOffice")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BackOfficeResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	public BackOfficeResource() { }
	
	@POST
	@Path("/addBO")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAdmin(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BO: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BO, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(boData.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "SU")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "SU")
						.set("user_birthday", boData.birthday)
						.set("user_zip_code", boData.zipCode)
						.build();
				txn.add(user);
				LOG.info("BO registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addBOM")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBOM(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BOM: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BOM, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(boData.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "BOM")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "BOM")
						.set("user_birthday", boData.birthday)
						.set("user_zip_code", boData.zipCode)
						.build();
				txn.add(user);
				LOG.info("BOM registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addBOP")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBOP(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BOP: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BOP, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(boData.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "BOP")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "BOP")
						.set("user_birthday", boData.birthday)
						.set("user_zip_code", boData.zipCode)
						.build();
				txn.add(user);
				LOG.info("BOP registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/listActiveAdmins")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveAdmins(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active admins");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_LIST_ACTIVE_ADMINS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("active_account", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeAdminsList = new ArrayList<Entity>();
		logs.forEachRemaining(activeAdminsLog -> {
			if(!activeAdminsLog.getString("user_role").equals("User"))
				activeAdminsList.add(activeAdminsLog);
		});
		LOG.info("Got list of active admins");
		return Response.ok(g.toJson(activeAdminsList)).build();
	}
	
	@POST
	@Path("/updateRole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRole(UpdateRoleData updateData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to update role by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_UPDATE_ROLE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(updateData.username);
			if (txn.get(userKey) == null) {
				LOG.warning("Failed update attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			Entity user = datastore.get(userKey);
				user = Entity.newBuilder(userKey)
						.set("user_name", user.getString("user_name"))
						.set("user_pwd", user.getString("user_pwd"))
						.set("user_email", user.getString("user_email"))
						.set("user_role", updateData.newRole)
						.set("user_creation_time", user.getTimestamp("user_creation_time"))
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", user.getString("user_street"))
						.set("user_place", user.getString("user_place"))
						.set("user_country", user.getString("user_country"))
						.set("active_account", user.getBoolean("active_account"))
						.set("activation_code", user.getString("activation_code"))
						.set("user_birthday", user.getString("user_birthday"))
						.set("user_zip_code", user.getString("user_zip_code"))
						.build();
				txn.update(user);
				LOG.info("User role changed by: " + data.username);
				txn.commit();
				return Response.ok("{}").build();
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
}
