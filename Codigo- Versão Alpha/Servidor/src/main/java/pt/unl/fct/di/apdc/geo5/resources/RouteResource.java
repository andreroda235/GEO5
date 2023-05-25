package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.PointerData;
import pt.unl.fct.di.apdc.geo5.data.RouteData;
import pt.unl.fct.di.apdc.geo5.data.AddRouteData;
import pt.unl.fct.di.apdc.geo5.util.Jwt;

@Path("/route")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RouteResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();

	public RouteResource() { }

	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitRoute(AddRouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to submit route: " + routeData.title + " from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!routeData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.id);
			Entity route = datastore.get(routeKey);
			if (route != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Route already exists.").build();
			} else {
				boolean hasIntermidiatePoints = !routeData.intermidiatePoints.isEmpty();
				route = Entity.newBuilder(routeKey)
						.set("route_name", routeData.title)
						.set("route_owner", data.username)
						.set("route_description", routeData.description)
						.set("route_travel_mode", routeData.travelMode)
						.set("route_start_lat", routeData.origin.lat)
						.set("route_start_lon", routeData.origin.lng)
						.set("route_end_lat", routeData.destination.lat)
						.set("route_end_lon", routeData.destination.lng)
						.set("route_creation_time", Timestamp.now())
						.set("active_route", true)
						.set("has_intermidiate_points", hasIntermidiatePoints)
						.build();
				if (hasIntermidiatePoints) {
					for (PointerData i : routeData.intermidiatePoints) {
						Key intPointsKey = datastore.allocateId(
								datastore.newKeyFactory()
								.addAncestors(PathElement.of("Route", routeData.id))
								.setKind("RouteIntermidiatePoint").newKey());
						Entity intPoint = Entity.newBuilder(intPointsKey)
								.set("point_lat", i.lat)
								.set("point_lon", i.lng)
								.build();		
						txn.add(intPoint);
					}
				}
				txn.add(route);
				LOG.info("Route registered " + routeData.title + "from user: " + data.username);
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
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRoute(RouteData routeData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get route with id: " + routeData.id + " by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (routeData.id.equals("")) {
			return Response.status(Status.BAD_REQUEST).entity("Please enter a valid id.").build();
		}
		try {
			Key routeKey = datastore.newKeyFactory().setKind("Route").newKey(routeData.id);
			Entity r = datastore.get(routeKey);
			if (r == null) {
				return Response.status(Status.BAD_REQUEST).entity("Route does not exist.").build();
			} else {
		        JsonObject result = new JsonObject();
		        result.addProperty("route_name", r.getString("route_name"));
		        result.addProperty("route_owner", r.getString("route_owner"));
		        result.addProperty("route_description", r.getString("route_description"));
		        result.addProperty("route_travel_mode", r.getString("route_travel_mode"));
		        result.addProperty("route_start_lat", r.getString("route_start_lat"));
		        result.addProperty("route_start_lon", r.getString("route_start_lon"));
		        result.addProperty("route_end_lat", r.getString("route_end_lat"));
		        result.addProperty("route_end_lon", r.getString("route_end_lon"));
		        result.addProperty("route_creation_time", r.getTimestamp("route_creation_time").toString());
		        result.addProperty("active_route", r.getBoolean("active_route"));
				LOG.info("Got route with id: " + routeData.id + " for user: " + data.username);
				return Response.ok(g.toJson(result)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public Set<PointerData> getIntermidiatePoints(String id) {
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("RouteIntermidiatePoint")
				.setFilter(PropertyFilter.hasAncestor(datastore.newKeyFactory().setKind("Route").newKey(id)))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		Set<PointerData> intermidiatePoints = new HashSet<PointerData>();
		logs.forEachRemaining(intermidiatePointsLog -> {
			PointerData p = new PointerData(intermidiatePointsLog.getString("point_lat"), intermidiatePointsLog.getString("point_lon"));
			intermidiatePoints.add(p);
		});
		return intermidiatePoints;
	}
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getRoutesOfUser(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get routes from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Route")
				.setFilter(PropertyFilter.eq("route_owner", data.username))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<AddRouteData> userRoutes = new ArrayList<AddRouteData>();
		logs.forEachRemaining(userRoutesLog -> {
			PointerData start = new PointerData(userRoutesLog.getString("route_start_lat"), userRoutesLog.getString("route_start_lon"));
			PointerData end = new PointerData(userRoutesLog.getString("route_end_lat"), userRoutesLog.getString("route_end_lon"));
			AddRouteData route;
			if (userRoutesLog.getBoolean("has_intermidiate_points")) {
				route = new AddRouteData(
						userRoutesLog.getKey().getName().toString(),
						start,
						end,
						userRoutesLog.getString("route_name"),
						userRoutesLog.getString("route_owner"),
						userRoutesLog.getString("route_description"),
						userRoutesLog.getString("route_travel_mode"),
						getIntermidiatePoints(userRoutesLog.getKey().getName().toString()),
						true
				);
			}
			else {
				route = new AddRouteData(
						userRoutesLog.getKey().getName().toString(),
						start,
						end,
						userRoutesLog.getString("route_name"),
						userRoutesLog.getString("route_owner"),
						userRoutesLog.getString("route_description"),
						userRoutesLog.getString("route_travel_mode"),
						true
				);
			}
			userRoutes.add(route);
		});
		LOG.info("Got routes from user: " + data.username);
		return Response.ok(g.toJson(userRoutes)).build();
	}	
	
	@POST
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveRoutes(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active routes");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Route")
				.setFilter(PropertyFilter.eq("active_route", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<AddRouteData> activeRoutes = new ArrayList<AddRouteData>();
		logs.forEachRemaining(activeRoutesLog -> {
			PointerData start = new PointerData(activeRoutesLog.getString("route_start_lat"), activeRoutesLog.getString("route_start_lon"));
			PointerData end = new PointerData(activeRoutesLog.getString("route_end_lat"), activeRoutesLog.getString("route_end_lon"));
			AddRouteData route;
			if (activeRoutesLog.getBoolean("has_intermidiate_points")) {
				route = new AddRouteData(
						activeRoutesLog.getKey().getName().toString(),
						start,
						end,
						activeRoutesLog.getString("route_name"),
						activeRoutesLog.getString("route_owner"),
						activeRoutesLog.getString("route_description"),
						activeRoutesLog.getString("route_travel_mode"),
						getIntermidiatePoints(activeRoutesLog.getKey().getName().toString()),
						true
				);
			}
			else {
				route = new AddRouteData(
						activeRoutesLog.getKey().getName().toString(),
						start,
						end,
						activeRoutesLog.getString("route_name"),
						activeRoutesLog.getString("route_owner"),
						activeRoutesLog.getString("route_description"),
						activeRoutesLog.getString("route_travel_mode"),
						true
				);
			}
			activeRoutes.add(route);
		});
		LOG.info("Got list of active routes");
		return Response.ok(g.toJson(activeRoutes)).build();
	}
}
