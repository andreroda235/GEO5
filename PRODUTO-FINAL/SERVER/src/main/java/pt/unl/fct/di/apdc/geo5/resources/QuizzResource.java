package pt.unl.fct.di.apdc.geo5.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.QuestionData;
import pt.unl.fct.di.apdc.geo5.data.QuizzData;
import pt.unl.fct.di.apdc.geo5.data.SearchData;
import pt.unl.fct.di.apdc.geo5.util.Access;
import pt.unl.fct.di.apdc.geo5.util.AccessMap;
import pt.unl.fct.di.apdc.geo5.util.Jwt;
import pt.unl.fct.di.apdc.geo5.util.Logs;
import pt.unl.fct.di.apdc.geo5.util.Search;

@Path("/quizz")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizzResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	// Instantiates a client
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	private final Gson g = new Gson();

	public QuizzResource() { }
	
	@POST
	@Path("/submit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitQuiz(QuizzData quizzData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to submit quiz: " + quizzData.title + " from user: " + data.username);
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_QUIZZ_SUBMIT, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			Key quizzKey = datastore.newKeyFactory().setKind("Quizz").newKey(quizzData.title);
			Entity quizz = datastore.get(quizzKey);
			if (quizz != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Quizz already exists.").build();
			} else {
				quizz = Entity.newBuilder(quizzKey)
						.set("quizz_description", quizzData.description)
						.set("quizz_keywords", quizzData.keywords)
						.set("quizz_creation_time", Timestamp.now())
						.set("active_quizz", true)
						.build();
				for (QuestionData i : quizzData.questions) {
						Key questionKey = datastore.allocateId(
								datastore.newKeyFactory()
								.addAncestors(PathElement.of("Quizz", quizzData.title))
								.setKind("Question").newKey());
						Entity question = Entity.newBuilder(questionKey)
								.set("question_number", i.number)
								.set("question_question", i.question)
								.set("question_right_answer", i.rightAnswer)
								.set("question_wrong_answer_1", i.wrongAnswer1)
								.set("question_wrong_answer_2", i.wrongAnswer2)
								.set("question_wrong_answer_3", i.wrongAnswer3)
								.build();	
						txn.add(question);
				}
				txn.add(quizz);
				LOG.info("Quizz registered " + quizzData.title + "from user: " + data.username);
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
	
	public Set<QuestionData> getQuestions(String title) {
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Question")
				.setFilter(PropertyFilter.hasAncestor(datastore.newKeyFactory().setKind("Quizz").newKey(title)))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		Set<QuestionData> questions = new HashSet<QuestionData>();
		logs.forEachRemaining(questionLog -> {
			QuestionData q = new QuestionData(
					questionLog.getString("question_number"),
					questionLog.getString("question_question"),
					questionLog.getString("question_right_answer"),
					questionLog.getString("question_wrong_answer_1"),
					questionLog.getString("question_wrong_answer_2"),
					questionLog.getString("question_wrong_answer_3")
			);
			questions.add(q);
		});
		return questions;
	}
	
	@POST
	@Path("/listActive")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listActiveQuizzes(@Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to list active quizzes");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_QUIZZ_LIST_ACTIVE, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Quizz")
				.setFilter(PropertyFilter.eq("active_quizz", true))
				.build();
		QueryResults<Entity> logs = datastore.run(query);
		List<QuizzData> activeQuizzes = new ArrayList<QuizzData>();
		logs.forEachRemaining(activeQuizzesLog -> {
			QuizzData quizz;
			quizz = new QuizzData(
					activeQuizzesLog.getKey().getName().toString(),
					activeQuizzesLog.getString("quizz_description"),
					activeQuizzesLog.getString("quizz_keywords"),
					getQuestions(activeQuizzesLog.getKey().getName().toString())
			);
			activeQuizzes.add(quizz);
		});
		LOG.info("Got list of active quizzes");
		return Response.ok(g.toJson(activeQuizzes)).build();
	}
	
	@POST
	@Path("/getRandom")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRandomQuizz(SearchData searchData, @Context HttpHeaders headers) {
		Jwt j = new Jwt();
		AuthToken data = j.getAuthToken(headers.getHeaderString("token"));
		LOG.fine("Attempt to get random quizz by tag");
		if (!j.validToken(headers.getHeaderString("token"))) {
			LOG.warning("Invalid token for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		if (!AccessMap.hasAccess(Access.PERMISSION_QUIZZ_GET_RANDOM, data.username)) {
			LOG.warning(Logs.LOG_INSUFFICIENT_PERMISSIONS + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
		String[] splitStr = searchData.search.trim().split("\\s+");
		Query<Entity> query = Query.newEntityQueryBuilder()
			    .setKind("Quizz")
				.setFilter(PropertyFilter.eq("active_quizz", true))
			    .build();
		QueryResults<Entity> logs = datastore.run(query);
		List<QuizzData> searchResults = new ArrayList<QuizzData>();
		logs.forEachRemaining(searchResultsLog -> {
			if (Search.containsWords(searchResultsLog.getString("quizz_keywords").toLowerCase(), splitStr)) {
				QuizzData quizz;
				quizz = new QuizzData(
						searchResultsLog.getKey().getName().toString(),
						searchResultsLog.getString("quizz_description"),
						searchResultsLog.getString("quizz_keywords"),
						getQuestions(searchResultsLog.getKey().getName().toString())
						);
				searchResults.add(quizz);
			}
		});
		if(searchResults.isEmpty())
			return Response.ok("{}").build();
		Random rand = new Random();
	    QuizzData randomElement = searchResults.get(rand.nextInt(searchResults.size()));
		LOG.info("Got random quizz");
		return Response.ok(g.toJson(randomElement)).build();
	}
}
