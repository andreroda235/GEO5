package pt.unl.fct.di.apdc.geo5.resources;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;
import pt.unl.fct.di.apdc.geo5.util.MediaResourceServlet;

@Path("/storage")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class StorageResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	public StorageResource() {
		
	}

	@POST
	@Path("/upload")
	public Response upload(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt upload file by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_UPLOAD, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		LOG.info("Attempting to upload file: " + filename);
		try {
			m.doPost(req, resp, filename);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Uploaded successfully: " + filename);
		return Response.ok("{}").build();
	}
	
	@POST
	@Path("/upload/user/{username}")
	public Response uploadUserPicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("username") String username) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to upload user picture: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_UPLOAD_USER_PICTURE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		Transaction txn = datastore.newTransaction();
		try {
			Key pictureKey = datastore.newKeyFactory().setKind("UserProfilePicture").newKey(data.username);
			Entity picture = datastore.get(pictureKey);		
			if (picture == null) {
				m.doPost(req, resp, filename);
				picture = Entity.newBuilder(pictureKey)
						.set("file_name", filename)
						.set("file_type", req.getContentType())
						.set("file_upload_date", Timestamp.now())
						.build();
				txn.add(picture);
				LOG.info("Uploaded user picture successfully: " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
			else {
				m.doPost(req, resp, filename);
				picture = Entity.newBuilder(pictureKey)
						.set("file_name", filename)
						.set("file_type", req.getContentType())
						.set("file_upload_date", Timestamp.now())
						.build();
				txn.update(picture);
				LOG.info("Uploaded user picture successfully: " + data.username);
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
	@Path("/upload/route/{routeid}")
	public Response uploadRoutePicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("routeid") String routeID) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to upload route picture: " + routeID);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_UPLOAD_ROUTE_PICTURE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		Transaction txn = datastore.newTransaction();
		try {
			m.doPost(req, resp, filename);
			Key pictureKey = datastore.allocateId(
					datastore.newKeyFactory()
					.setKind("RoutePicture").newKey());
			Entity fileUpload = datastore.get(pictureKey);
			fileUpload = Entity.newBuilder(pictureKey)
					.set("routeID", routeID)
					.set("file_name", filename)
					.set("file_type", req.getContentType())
					.set("file_upload_date", Timestamp.now())
					.build();
			txn.add(fileUpload);
			LOG.info("Uploaded route picture successfully: " + routeID);
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
	@Path("/upload/geoSpot/{geoSpotName}")
	public Response uploadGeoSpotPicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("geoSpotName") String geoSpotName) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt upload GeoSpot picture by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_UPLOAD_GEOSPOT_PICTURE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		Transaction txn = datastore.newTransaction();
		try {
			m.doPost(req, resp, filename);
			Key pictureKey = datastore.allocateId(
					datastore.newKeyFactory()
					.setKind("GeoSpotPicture").newKey());
			Entity fileUpload = datastore.get(pictureKey);
			fileUpload = Entity.newBuilder(pictureKey)
					.set("geoSpot_name", geoSpotName)
					.set("file_name", filename)
					.set("file_type", req.getContentType())
					.set("file_upload_date", Timestamp.now())
					.build();
			txn.add(fileUpload);
			LOG.info("Uploaded GeoSpot picture successfully: " + geoSpotName);
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
	@Path("/upload/info/{infoName}")
	public Response uploadInfoPicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("infoName") String infoName) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt upload Info picture by user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_UPLOAD_INFO_PICTURE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		Transaction txn = datastore.newTransaction();
		try {
			m.doPost(req, resp, filename);
			Key pictureKey = datastore.allocateId(
					datastore.newKeyFactory()
					.setKind("InfoPicture").newKey());
			Entity fileUpload = datastore.get(pictureKey);
			fileUpload = Entity.newBuilder(pictureKey)
					.set("info_name", infoName)
					.set("file_name", filename)
					.set("file_type", req.getContentType())
					.set("file_upload_date", Timestamp.now())
					.build();
			txn.add(fileUpload);
			LOG.info("Uploaded Info picture successfully: " + infoName);
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
	@Path("/download/{filename}")
	public Response download(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("filename") String filename) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt download: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_DOWNLOAD, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		LOG.info("Attempting to download file: " + filename);
		try {
			m.doGet(req, resp, filename);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Downloaded successfully: " + filename);
		return Response.ok("{}").build();
	}
	
	@POST
	@Path("/delete/{filename}")
	public Response delete(@Context HttpServletRequest req, @Context HttpServletResponse resp, @Context HttpHeaders headers, 
			@PathParam("filename") String filename) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.info("Attempting to delete file: " + filename);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_STORAGE_DELETE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		MediaResourceServlet m = new MediaResourceServlet();
		try {
			m.doDelete(req, resp, filename);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		LOG.info("Deleted successfully: " + filename);
		return Response.ok("{}").build();
	}
}
