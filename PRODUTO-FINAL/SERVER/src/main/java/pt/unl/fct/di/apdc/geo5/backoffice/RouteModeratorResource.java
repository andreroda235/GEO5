package pt.unl.fct.di.apdc.geo5.backoffice;

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
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.RouteData;
import pt.unl.fct.di.apdc.geo5.resources.LoginResource;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/routeModerator")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RouteModeratorResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public RouteModeratorResource() { }
	
	@POST
	@Path("/makeRouteActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeRouteActive(RouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to make route active");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_ROUTE_ACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.id);
			Entity r = datastore.get(routeKey);		
			if (r == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} else {
				r = Entity.newBuilder(routeKey)
					.set("route_name", r.getString("route_name"))
					.set("route_owner", r.getString("route_owner"))
					.set("route_description", r.getString("route_description"))
					.set("route_travel_mode", r.getString("route_travel_mode"))
					.set("route_start_lat", r.getString("route_start_lat"))
					.set("route_start_lon", r.getString("route_start_lon"))
					.set("route_end_lat", r.getString("route_end_lat"))
					.set("route_end_lon", r.getString("route_end_lon"))
					.set("route_creation_time", r.getTimestamp("route_creation_time").toString())
					.set("active_route", true)
					.set("has_intermidiate_points", r.getBoolean("hasIntermidiatePoints"))
					.build();
				txn.update(r);
				LOG.info("Route status changed to active: " + data.username);
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
	@Path("/makeRouteInactive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeRouteInactiva(RouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to make route active");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_MODERATOR_MAKE_ROUTE_INACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.id);
			Entity r = datastore.get(routeKey);		
			if (r == null) {
				return Response.status(Status.BAD_REQUEST).entity("Username does not exist.").build();
			} else {
				r = Entity.newBuilder(routeKey)
					.set("route_name", r.getString("route_name"))
					.set("route_owner", r.getString("route_owner"))
					.set("route_description", r.getString("route_description"))
					.set("route_travel_mode", r.getString("route_travel_mode"))
					.set("route_start_lat", r.getString("route_start_lat"))
					.set("route_start_lon", r.getString("route_start_lon"))
					.set("route_end_lat", r.getString("route_end_lat"))
					.set("route_end_lon", r.getString("route_end_lon"))
					.set("route_creation_time", r.getTimestamp("route_creation_time").toString())
					.set("active_route", false)
					.set("has_intermidiate_points", r.getBoolean("hasIntermidiatePoints"))
					.build();
				txn.update(r);
				LOG.info("Route status changed to inactive: " + data.username);
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
	
}
