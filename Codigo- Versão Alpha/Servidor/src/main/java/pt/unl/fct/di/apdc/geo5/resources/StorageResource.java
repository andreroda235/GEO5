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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

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
	public Response upload(@Context HttpServletRequest req, @Context HttpServletResponse resp) {
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
	public Response uploadUserPicture(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("username") String username) {
		MediaResourceServlet m = new MediaResourceServlet();
		String filename = UUID.randomUUID().toString();
		LOG.info("Attempting to upload user picture: " + username);
		Transaction txn = datastore.newTransaction();
		try {
			m.doPost(req, resp, filename);
			Key pictureKey = datastore.newKeyFactory()
					.setKind("UserProfilePicture")
					.newKey(username);
			Entity fileUpload = datastore.get(pictureKey);
			fileUpload = Entity.newBuilder(pictureKey)
					.set("file_name", filename)
					.set("file_type", req.getContentType())
					.set("file_upload_date", Timestamp.now())
					.build();
			txn.add(fileUpload);
			LOG.info("Uploaded user picture successfully: " + username);
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
	public Response download(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("filename") String filename) {
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
	public Response delete(@Context HttpServletRequest req, @Context HttpServletResponse resp, @PathParam("filename") String filename) {
		MediaResourceServlet m = new MediaResourceServlet();
		LOG.info("Attempting to delete file: " + filename);
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
