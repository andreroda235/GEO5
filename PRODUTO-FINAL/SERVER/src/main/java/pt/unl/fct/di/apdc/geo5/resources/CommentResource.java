package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.CommentGeoSpotData;
import pt.unl.fct.di.apdc.geo5.data.CommentRouteData;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/comment")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CommentResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public CommentResource() {
		
	}
	
	@POST
	@Path("/route")
	public Response addRouteComment(CommentRouteData commentData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to add route comment: " + commentData.routeID);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_ROUTE_ADD, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			String uniqueID = UUID.randomUUID().toString();
			Key commentKey = datastore.newKeyFactory().setKind("RouteComment").newKey(uniqueID);
			Entity comment = datastore.get(commentKey);
			comment = Entity.newBuilder(commentKey)
					.set("routeID", commentData.routeID)
					.set("comment_user", data.username)
					.set("comment_content", commentData.content)
					.set("comment_add_date", Timestamp.now())
					.set("active_comment", true)
					.build();
			txn.add(comment);
			LOG.info("Uploaded route comment successfully: " + commentData.routeID);
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
	@Path("/geoSpot")
	public Response addGeoSpotComment(CommentGeoSpotData commentData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to add GeoSpot comment: " + commentData.geoSpotName);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_GEOSPOT_ADD, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			String uniqueID = UUID.randomUUID().toString();
			Key commentKey = datastore.newKeyFactory().setKind("GeoSpotComment").newKey(uniqueID);
			Entity comment = datastore.get(commentKey);
			comment = Entity.newBuilder(commentKey)
					.set("geoSpot_name", commentData.geoSpotName)
					.set("comment_user", data.username)
					.set("comment_content", commentData.content)
					.set("comment_add_date", Timestamp.now())
					.set("active_comment", true)
					.build();
			txn.add(comment);
			LOG.info("Uploaded GeoSpot comment successfully: " + commentData.geoSpotName);
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
	@Path("/route/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveRouteComments(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active comments of routes");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_LIST_ACTIVE_ROUTE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("RouteComment")
				.setOrderBy(OrderBy.asc("comment_add_date"))
				.setFilter(PropertyFilter.eq("active_comment", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<CommentRouteData> activeCommentList = new ArrayList<CommentRouteData>();
		logs.forEachRemaining(activeCommentLog -> {
			CommentRouteData comment;
			comment = new CommentRouteData(
					activeCommentLog.getKey().getName().toString(),
					activeCommentLog.getString("comment_user"),
					activeCommentLog.getString("routeID"),
					activeCommentLog.getString("comment_content")
				);
			activeCommentList.add(comment);
		});
		LOG.info("Got list of active comments of routes");
		return Response.ok(g.toJson(activeCommentList)).build();
	}
	
	@POST
	@Path("/geoSpot/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveGeoSpotComments(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active comments of GeoSpots");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_LIST_ACTIVE_GEOSPOTS, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("GeoSpotComment")
				.setOrderBy(OrderBy.asc("comment_add_date"))
				.setFilter(PropertyFilter.eq("active_comment", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<CommentGeoSpotData> activeCommentList = new ArrayList<CommentGeoSpotData>();
		logs.forEachRemaining(activeCommentLog -> {
			CommentGeoSpotData comment;
			comment = new CommentGeoSpotData(
					activeCommentLog.getKey().getName().toString(),
					activeCommentLog.getString("comment_user"),
					activeCommentLog.getString("geoSpot_name"),
					activeCommentLog.getString("comment_content")
				);
			activeCommentList.add(comment);
		});
		LOG.info("Got list of active comments of GeoSpots");
		return Response.ok(g.toJson(activeCommentList)).build();
	}
	
	@POST
	@Path("/comment/route/{routeID}")
	public Response getRouteComments(@Context HttpServletRequest req, @Context HttpServletResponse resp, 
			@PathParam("routeID") String routeID, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to get route comments: " + routeID);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_ROUTE_GET, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("RouteComment")
				.setOrderBy(OrderBy.asc("comment_add_date"))
				.setFilter(
						CompositeFilter.and(
								PropertyFilter.eq("routeID", routeID),
								PropertyFilter.eq("active_comment", true)
								)
						)
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<CommentRouteData> activeCommentList = new ArrayList<CommentRouteData>();
		logs.forEachRemaining(activeCommentLog -> {
			CommentRouteData comment;
			comment = new CommentRouteData(
					activeCommentLog.getKey().getName().toString(),
					activeCommentLog.getString("comment_user"),
					activeCommentLog.getString("routeID"),
					activeCommentLog.getString("comment_content")
				);
			activeCommentList.add(comment);
		});
		LOG.info("Got list of active comments of route: " + routeID);
		return Response.ok(g.toJson(activeCommentList)).build();
	}
	
	@POST
	@Path("/comment/geoSpot/{geoSpotName}")
	public Response getGeoSpotComments(@Context HttpServletRequest req, @Context HttpServletResponse resp, 
			@PathParam("geoSpotName") String geoSpotName, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to get GeoSpot comments: " + geoSpotName);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_COMMENT_GEOSPOT_GET, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("GeoSpotComment")
				.setOrderBy(OrderBy.asc("comment_add_date"))
				.setFilter(
						CompositeFilter.and(
								PropertyFilter.eq("geoSpot_name", geoSpotName),
								PropertyFilter.eq("active_comment", true)
								)
						)
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<CommentGeoSpotData> activeCommentList = new ArrayList<CommentGeoSpotData>();
		logs.forEachRemaining(activeCommentLog -> {
			CommentGeoSpotData comment;
			comment = new CommentGeoSpotData(
					activeCommentLog.getKey().getName().toString(),
					activeCommentLog.getString("comment_user"),
					activeCommentLog.getString("geoSpot_name"),
					activeCommentLog.getString("comment_content")
				);
			activeCommentList.add(comment);
		});
		LOG.info("Got list of active comments of GeoSpot: " + geoSpotName);
		return Response.ok(g.toJson(activeCommentList)).build();
	}
}
