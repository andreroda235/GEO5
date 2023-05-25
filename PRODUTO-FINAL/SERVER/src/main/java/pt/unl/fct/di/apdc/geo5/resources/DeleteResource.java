package pt.unl.fct.di.apdc.geo5.resources;

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
import pt.unl.fct.di.apdc.geo5.data.DeleteData;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();

	public DeleteResource() {}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doDelete(DeleteData deleteData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine(Logs.LOG_DELETE_ATTEMPT + deleteData.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_DELETE_USER, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(deleteData.username);
			if (txn.get(userKey) == null) {
				LOG.warning(Logs.LOG_DELETE_FAIL + deleteData.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			txn.delete(userKey);
			LOG.info(Logs.LOG_DELETE_SUCCESS + deleteData.username);
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
	@Path("/inactiveUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInactiveUsers(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to delete inactive users");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_DELETE_INACTIVE_USERS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Query<Entity> query = Query.newEntityQueryBuilder()
					.setKind("User")
					.setFilter(PropertyFilter.eq("active_account", false))
					.build();
			QueryResults<Entity> logs = datastore.run(query);
			List<Entity> inactiveUsersList = new ArrayList<Entity>();
			logs.forEachRemaining(inactiveUsersLog -> {
				inactiveUsersList.add(inactiveUsersLog);
				txn.delete(inactiveUsersLog.getKey());
			});
			LOG.info("Inactive users deleted");
			txn.commit();
			return Response.ok(g.toJson(inactiveUsersList)).build();
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
	
	/**
	@POST
	@Path("/invalidTokens")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteInvalidTokens(DeleteData data) {
		LOG.fine("Attempt to delete invalid tokens");
		Transaction txn = datastore.newTransaction();
		try {
			Query<Entity> query = Query.newEntityQueryBuilder()
					.setKind("Token")
					.setFilter(PropertyFilter.eq("validity", false))
					.build();
			QueryResults<Entity> logs = datastore.run(query);
			List<Entity> invalidTokensList = new ArrayList<Entity>();
			logs.forEachRemaining(invalidTokensLog -> {
				invalidTokensList.add(invalidTokensLog);
				txn.delete(invalidTokensLog.getKey());
			});
			LOG.info("Invalid tokens deleted");
			txn.commit();
			return Response.ok(g.toJson(invalidTokensList)).build();
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}**/
}