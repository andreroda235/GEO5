package pt.unl.fct.di.apdc.geo5.resources;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
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

import com.google.cloud.datastore.Key;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.LoginData;
import pt.unl.fct.di.apdc.geo5.util.Jwt;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");


	private final Gson g = new Gson();

	public LoginResource() { }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin(LoginData data,
			@Context HttpServletRequest request,
			@Context HttpHeaders headers) {
		LOG.fine("Attempt to login user: " + data.username);
		
		//KEYS SHOULD BE GENERATED OUTSIDE TRANSACTIONS
		//Construct the key from the username
		Key userKey = userKeyFactory.newKey(data.username);
		Key ctrsKey = datastore.newKeyFactory()
				.addAncestors(PathElement.of("User", data.username))
				.setKind("UserStats")
				.newKey("counters");
		//Generate automatically a key
		Key logKey = datastore.allocateId(
				datastore.newKeyFactory()
				.addAncestors(PathElement.of("User", data.username))
				.setKind("UserLog").newKey());
		
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			if (user == null) {
				//Username does not exist
				LOG.warning("Failed login attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}

			if (!user.getBoolean("active_account")) {
				//Account is inactive
				LOG.warning("Account is inactive: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			
			//We get the user stats from the storage
			Entity stats = txn.get(ctrsKey);
			if(stats == null) {
				stats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", 0L)
						.set("user_stats_failed", 0L)
						.set("user_first_login", Timestamp.now())
						.set("user_last_login", Timestamp.now())
						.set("user_last_attempt", Timestamp.now())
						.build();
			}
			
			String hashedPWD = (String) user.getString("user_pwd");
			if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				//Password is correct
				//Construct the logs
				Entity log = Entity.newBuilder(logKey)
						.set("user_login_ip", request.getRemoteAddr())
						.set("user_login_host", request.getRemoteHost())
						.set("user_login_latlon", 
								//Does not index this property value
								StringValue.newBuilder(headers.getHeaderString("X-AppEngine-CityLatLong")).setExcludeFromIndexes(true).build())
						.set("user_login_city", headers.getHeaderString("X-AppEngine-City"))
						.set("user_login_country", headers.getHeaderString("X-AppEngine-Country"))
						.set("user_login_time", Timestamp.now())
						.build();
				
				String role = (String) user.getString("user_role");
				AuthToken t = new AuthToken(data.username, role);

				//Return token
				Jwt j = new Jwt();
				String token = j.generateJwtToken(t);

				//Gets the user statistics and updates it
				//Copying information every time a user logins maybe is not a good solution (why?)
				Entity ustats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", 1L + stats.getLong("user_stats_logins"))
						.set("user_stats_failed", 0L)
						.set("user_first_login", stats.getTimestamp("user_first_login"))
						.set("user_last_login", Timestamp.now())
						.set("user_last_attempt", Timestamp.now())
						.set("user_token", token)
						.build();
				
				//Batch operation
				txn.put(log, ustats);
				txn.commit();
				
				LOG.info("User '" + data.username + "' logged in successfully.");
				return Response.ok(g.toJson(token)).build();
			} else {
				//Incorrect password
				//Copying here is even worse. Propose a better solution!
				Entity ustats = txn.get(ctrsKey);
				ustats = Entity.newBuilder(ctrsKey)
						.set("user_stats_logins", stats.getLong("user_stats_logins"))
						.set("user_stats_failed", 1L + stats.getLong("user_stats_failed"))
						.set("user_first_login", stats.getTimestamp("user_first_login"))
						.set("user_last_login", stats.getTimestamp("user_last_login"))
						.set("user_last_attempt", Timestamp.now())
						.set("user_token", ustats.getString("user_token"))
						.build();
				txn.put(ustats);
				txn.commit();
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}
