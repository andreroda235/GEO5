package pt.unl.fct.di.apdc.geo5.resources;

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
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.UpdateUserData;
import pt.unl.fct.di.apdc.geo5.util.Jwt;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdate(UpdateUserData updateData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to edit user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!updateData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			if (txn.get(userKey) == null) {
				LOG.warning("Failed update attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			Entity user = datastore.get(userKey);
				user = Entity.newBuilder(userKey)
						.set("user_name", updateData.name)
						.set("user_pwd", DigestUtils.sha512Hex(updateData.password))
						.set("user_email", updateData.email)
						.set("user_role", updateData.role)
						.set("user_creation_time", user.getTimestamp("user_creation_time"))
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", updateData.street)
						.set("user_place", updateData.place)
						.set("user_country", updateData.country)
						.set("active_account", updateData.isActive)
						.build();
				txn.update(user);
				LOG.info("User information changed: " + data.username);
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
