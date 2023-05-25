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
import com.google.gson.JsonObject;

import pt.unl.fct.di.apdc.geo5.data.AddGeoSpotData;
import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.GeoSpotData;
import pt.unl.fct.di.apdc.geo5.util.Jwt;

@Path("/geoSpot")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GeoSpotResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();
	
	public GeoSpotResource() { }

	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitRoute(AddGeoSpotData geoSpotData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to submit GeoSpot: " + geoSpotData.geoSpotName + " from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}		
		if (!geoSpotData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key geoSpotKey = datastore.newKeyFactory().setKind("GeoSpot").newKey(geoSpotData.geoSpotName);
			Entity geoSpot = datastore.get(geoSpotKey);
			if (geoSpot != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("GeoSpot already exists.").build();
			} else {
				geoSpot = Entity.newBuilder(geoSpotKey)
						.set("geoSpot_name", geoSpotData.geoSpotName)
						.set("geoSpot_owner", data.username)
						.set("geoSpot_description", geoSpotData.description)
						.set("geoSpot_creation_time", Timestamp.now())
						.set("geoSpot_lat", geoSpotData.location.lat)
						.set("geoSpot_lon", geoSpotData.location.lng)
						.set("active_geoSpot", true)
						.build();
				txn.add(geoSpot);
				LOG.info("GeoSpot registered " + geoSpotData.geoSpotName + "from user: " + data.username);
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
	public Response getGeoSpot(GeoSpotData geoSpotData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get GeoSpot: " + geoSpotData.geoSpotName + " by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (geoSpotData.geoSpotName.equals("")) {
			return Response.status(Status.BAD_REQUEST).entity("Please enter a GeoSpot name.").build();
		}
		try {
			Key geoSpotKey = datastore.newKeyFactory().setKind("GeoSpot").newKey(geoSpotData.geoSpotName);
			Entity gs = datastore.get(geoSpotKey);
			if (gs == null) {
				return Response.status(Status.BAD_REQUEST).entity("GeoSpot does not exist.").build();
			} else {
		        JsonObject result = new JsonObject();
		        result.addProperty("geoSpot_name", gs.getKey().getName());
		        result.addProperty("geoSpot_owner", gs.getString("geoSpot_owner"));
		        result.addProperty("geoSpot_description", gs.getString("geoSpot_description"));
		        result.addProperty("geoSpot_creation_time", gs.getTimestamp("route_creation_time").toString());
		        result.addProperty("geoSpot_lat", gs.getLong("geoSpot_lat"));
		        result.addProperty("geoSpot_lon", gs.getLong("geoSpot_lon"));
		        result.addProperty("active_geoSpot", gs.getBoolean("active_geoSpot"));
				LOG.info("Got GeoSpot: " + geoSpotData.geoSpotName + " for user: " + data.username);
				return Response.ok(g.toJson(result)).build();
			}
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveGeoSpots(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active GeoSpots");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("GeoSpot")
				.setFilter(PropertyFilter.eq("active_geoSpot", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<Entity> activeGeoSpotList = new ArrayList<Entity>();
		logs.forEachRemaining(activeGeoSpotLog -> {
			activeGeoSpotList.add(activeGeoSpotLog);
		});
		LOG.info("Got list of active GeoSpots");
		return Response.ok(g.toJson(activeGeoSpotList)).build();
	}
}
