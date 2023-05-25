package pt.unl.fct.di.apdc.geo5.backoffice;

import java.util.logging.Logger;

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
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.UserData;
import pt.unl.fct.di.apdc.geo5.resources.LoginResource;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/communityModerator")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CommunityModeratorResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public CommunityModeratorResource() { }

	@POST
	@Path("/makeUserActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeUserActive(UserData userData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to make user inactive");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_USER_ACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(userData.username);
			Entity u = datastore.get(userKey);		
			if (u == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} 
			/**
			Key userKey2 = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity u2 = datastore.get(userKey2);
			if (u2.getString("user_role").equals("BO") && !u.getString("user_role").equals("User")) {
				return Response.status(Status.BAD_REQUEST).entity(Logs.LOG_INSUFFICIENT_PERMISSIONS).build();
			}
			**/
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
						.set("activation_code", "NULL")
						.set("user_birthday", u.getString("user_birthday"))
						.set("user_zip_code", u.getString("user_zip_code"))
						.build();
				txn.update(u);
				LOG.info("User made active: " + data.username);
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
	@Path("/makeUserInactive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeUserInactive(UserData userData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to make user inactive");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_USER_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(userData.username);
			Entity u = datastore.get(userKey);		
			if (u == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} 
			/**
			Key userKey2 = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity u2 = datastore.get(userKey2);
			if (u2.getString("user_role").equals("BO") && !u.getString("user_role").equals("User")) {
				return Response.status(Status.BAD_REQUEST).entity(Logs.LOG_INSUFFICIENT_PERMISSIONS).build();
			}
			**/
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
	@Path("/makeRouteCommentInactive/{commentID}")
	public Response makeRouteCommentInactive(@PathParam("commentID") String commentID, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to make inactive route comment: " + commentID);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_COMMENT_ROUTE_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key commentKey = datastore.newKeyFactory().setKind("RouteComment").newKey(commentID);
			if (txn.get(commentKey) == null) {
				LOG.warning("Failed update attempt for id: " + commentID);
				return Response.status(Status.FORBIDDEN).build();
			}
			Entity comment = datastore.get(commentKey);
			comment = Entity.newBuilder(commentKey)
					.set("routeID", comment.getString("routeID"))
					.set("comment_user", comment.getString("comment_user"))
					.set("comment_content", comment.getString("comment_content"))
					.set("comment_add_date", comment.getTimestamp("comment_add_date"))
					.set("active_comment", false)
					.build();
			txn.update(comment);
			LOG.info("Comment made inactive: " + commentID);
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
	
	@POST
	@Path("/makeGeoSpotCommentInactive/{commentID}")
	public Response makeGeoSpotCommentInactive(@PathParam("commentID") String commentID, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to make inactive GeoSpot comment: " + commentID);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_COMMENT_GEOSPOT_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key commentKey = datastore.newKeyFactory().setKind("GeoSpotComment").newKey(commentID);
			if (txn.get(commentKey) == null) {
				LOG.warning("Failed update attempt for id: " + commentID);
				return Response.status(Status.FORBIDDEN).build();
			}
			Entity comment = datastore.get(commentKey);
			comment = Entity.newBuilder(commentKey)
					.set("geoSpot_name", comment.getString("geoSpot_name"))
					.set("comment_user", comment.getString("comment_user"))
					.set("comment_content", comment.getString("comment_content"))
					.set("comment_add_date", comment.getTimestamp("comment_add_date"))
					.set("active_comment", false)
					.build();
			txn.update(comment);
			LOG.info("Comment made inactive: " + commentID);
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
	

	