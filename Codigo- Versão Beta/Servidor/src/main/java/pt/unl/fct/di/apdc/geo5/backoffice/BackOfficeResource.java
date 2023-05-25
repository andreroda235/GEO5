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

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.RegisterBackOfficeData;
import pt.unl.fct.di.apdc.geo5.resources.LoginResource;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;

@Path("/backOffice")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BackOfficeResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public BackOfficeResource() { }
	
	@POST
	@Path("/addBO")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAdmin(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BO: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BO, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "SU")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "SU")
						.build();
				txn.add(user);
				LOG.info("BO registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addBOM")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBOM(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BOM: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BOM, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "BOM")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "BOM")
						.build();
				txn.add(user);
				LOG.info("BOM registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addBOP")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBOP(RegisterBackOfficeData boData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to register BOP: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning(Logs.LOG_INVALID_TOKEN + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!boData.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_BACKOFFICE_ADD_BOP, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", boData.name)
						.set("user_pwd", DigestUtils.sha512Hex(boData.password))
						.set("user_email", boData.email)
						.set("user_role", "BOP")
						.set("user_creation_time", Timestamp.now())
						.set("user_last_update_time", Timestamp.now())
						.set("user_street", boData.street)
						.set("user_place", boData.place)
						.set("user_country", boData.country)
						.set("active_account", true)
						.set("activation_code", "BOP")
						.build();
				txn.add(user);
				LOG.info("BOP registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
