package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.InfoData;
import pt.unl.fct.di.apdc.geo5.data.PointerData;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InfoResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();
	
	public InfoResource() { }

	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitInfo(InfoData infoData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to submit Info: " + infoData.title + " from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}		
		if (!AccessMap.hasAccess(Access.PERMISSION_INFO_SUBMIT, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key infoKey = datastore.newKeyFactory().setKind("Info").newKey(infoData.title);
			Entity info = datastore.get(infoKey);
			if (info != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Info already exists.").build();
			} else {
				info = Entity.newBuilder(infoKey)
						.set("info_title", infoData.title)
						.set("info_description", infoData.description)
						.set("info_map_link", infoData.mapLink)
						.set("info_notice_link", infoData.noticeLink)
						.set("info_lat", infoData.location.lat)
						.set("info_lon", infoData.location.lng)
						.set("info_creation_time", Timestamp.now())
						.set("active_info", true)
						.build();
				txn.add(info);
				LOG.info("Info registered " + infoData.title + "from user: " + data.username);
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
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveInfos(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active Infos");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_INFO_LIST_ACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Info")
				.setFilter(PropertyFilter.eq("active_info", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<InfoData> activeInfoList = new ArrayList<InfoData>();
		logs.forEachRemaining(activeInfoLog -> {
			PointerData location = new PointerData(activeInfoLog.getString("info_lat"), activeInfoLog.getString("info_lon"));
			InfoData info;
			info = new InfoData(
					activeInfoLog.getString("info_title"),
					activeInfoLog.getString("info_description"),
					activeInfoLog.getString("info_map_link"),
					activeInfoLog.getString("info_notice_link"),
					location
				);
			activeInfoList.add(info);
		});
		LOG.info("Got list of active Infos");
		return Response.ok(g.toJson(activeInfoList)).build();
	}
	
	@POST
	@Path("/{infoName}/pictures")
	public Response getInfotPictures(@Context HttpServletRequest req, @Context HttpServletResponse resp, 
			@PathParam("infoName") String infoName, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to get Info pictures: " + infoName);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_INFO_GET_PICTURES, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("InfoPicture")
				.setFilter(PropertyFilter.eq("info_name", infoName))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<String> infoPictureList = new ArrayList<String>();
		logs.forEachRemaining(infoPictureLog -> {
			infoPictureList.add(infoPictureLog.getString("file_name"));
		});
		LOG.info("User: " + data.username + " Got Info pictures for id: " + infoName);
		return Response.ok(g.toJson(infoPictureList)).build();
	}
}
