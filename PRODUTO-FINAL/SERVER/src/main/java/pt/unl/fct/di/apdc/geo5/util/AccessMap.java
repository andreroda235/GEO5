package pt.unl.fct.di.apdc.geo5.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import pt.unl.fct.di.apdc.geo5.resources.LoginResource;

public class AccessMap {

	public AccessMap () {
		
	}
	
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Map<String, Set<String>> accessMap;
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	//TODO
	static {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        //User
        Set<String> userAccess = new HashSet<String>();
        map.put("User", userAccess);
        userAccess.add(Access.PERMISSION_ROUTE_SUBMIT);
        userAccess.add(Access.PERMISSION_ROUTE_GET);
        userAccess.add(Access.PERMISSION_ROUTE_GET_OF_USER);
        userAccess.add(Access.PERMISSION_ROUTE_LIST_ACTIVE);
        userAccess.add(Access.PERMISSION_ROUTE_SEARCH_ACTIVE);
        userAccess.add(Access.PERMISSION_ROUTE_SEARCH_OF_USER);
        userAccess.add(Access.PERMISSION_ROUTE_GET_PICTURES);
        userAccess.add(Access.PERMISSION_GEOSPOT_GET);
        userAccess.add(Access.PERMISSION_GEOSPOT_LIST_ACTIVE);
        userAccess.add(Access.PERMISSION_GEOSPOT_GET_PICTURES);
        userAccess.add(Access.PERMISSION_USER_GET);
        userAccess.add(Access.PERMISSION_USER_GET_USER_PICTURE);
        userAccess.add(Access.PERMISSION_USER_MAKE_ACCOUNT_INACTIVE);
        userAccess.add(Access.PERMISSION_STORAGE_UPLOAD_USER_PICTURE);
        userAccess.add(Access.PERMISSION_QUIZZ_LIST_ACTIVE);
        userAccess.add(Access.PERMISSION_INFO_LIST_ACTIVE);
        userAccess.add(Access.PERMISSION_INFO_GET_PICTURES);
        userAccess.add(Access.PERMISSION_STORAGE_UPLOAD_ROUTE_PICTURE);
        userAccess.add(Access.PERMISSION_COMMENT_ROUTE_ADD);
        userAccess.add(Access.PERMISSION_COMMENT_GEOSPOT_ADD);
        userAccess.add(Access.PERMISSION_COMMENT_ROUTE_GET);
        userAccess.add(Access.PERMISSION_COMMENT_GEOSPOT_GET);
        userAccess.add(Access.PERMISSION_QUIZZ_GET_RANDOM);
        userAccess.add(Access.PERMISSION_UPDATE_USER);
        userAccess.add(Access.PERMISSION_UPDATE_USER_NOPASS);

        //BOP
        Set<String> modAccess = new HashSet<String>();
        map.put("BOP", modAccess);
        modAccess.addAll(userAccess);
        modAccess.add(Access.PERMISSION_MODERATOR_MAKE_ROUTE_ACTIVE);
        modAccess.add(Access.PERMISSION_MODERATOR_MAKE_ROUTE_INACTIVE);
        modAccess.add(Access.PERMISSION_GEOSPOT_SUBMIT);
        modAccess.add(Access.PERMISSION_STORAGE_UPLOAD);
        modAccess.add(Access.PERMISSION_STORAGE_UPLOAD_GEOSPOT_PICTURE);
        modAccess.add(Access.PERMISSION_STORAGE_UPLOAD_INFO_PICTURE);
        modAccess.add(Access.PERMISSION_STORAGE_DOWNLOAD);
        modAccess.add(Access.PERMISSION_STORAGE_DELETE);
        modAccess.add(Access.PERMISSION_QUIZZ_SUBMIT);
        modAccess.add(Access.PERMISSION_INFO_SUBMIT);

        //BOM
        Set<String> gboAccess = new HashSet<String>();
        map.put("BOM", gboAccess);
        gboAccess.addAll(userAccess);
        gboAccess.add(Access.PERMISSION_DELETE_USER);
        gboAccess.add(Access.PERMISSION_DELETE_INACTIVE_USERS);
        gboAccess.add(Access.PERMISSION_MODERATOR_MAKE_USER_ACTIVE);
        gboAccess.add(Access.PERMISSION_MODERATOR_MAKE_USER_INACTIVE);
        gboAccess.add(Access.PERMISSION_USER_LIST_ACTIVE);
        gboAccess.add(Access.PERMISSION_USER_LIST_INACTIVE);
        gboAccess.add(Access.PERMISSION_USER_LAST_24H_LOGINS);
        gboAccess.add(Access.PERMISSION_COMMENT_LIST_ACTIVE_ROUTE);
        gboAccess.add(Access.PERMISSION_COMMENT_LIST_ACTIVE_GEOSPOTS);
        gboAccess.add(Access.PERMISSION_MODERATOR_MAKE_COMMENT_ROUTE_INACTIVE);
        gboAccess.add(Access.PERMISSION_MODERATOR_MAKE_COMMENT_GEOSPOT_INACTIVE);
        gboAccess.add(Access.PERMISSION_USER_LIST_ALL_USERS);
        gboAccess.add(Access.PERMISSION_USER_LIST_ALL_ADMINS);

        //SU
        Set<String> suAccess = new HashSet<String>();
        map.put("SU", suAccess);
        suAccess.addAll(userAccess);
        suAccess.addAll(modAccess);
        suAccess.addAll(gboAccess);
        suAccess.add(Access.PERMISSION_BACKOFFICE_ADD_BO);
        suAccess.add(Access.PERMISSION_BACKOFFICE_ADD_BOM);
        suAccess.add(Access.PERMISSION_BACKOFFICE_ADD_BOP);
        suAccess.add(Access.PERMISSION_BACKOFFICE_LIST_ACTIVE_ADMINS);
        suAccess.add(Access.PERMISSION_BACKOFFICE_UPDATE_ROLE);

        accessMap = Collections.unmodifiableMap(map);

	}
		
	public static boolean hasAccess(String access, String user) {
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(user);
			Entity u = datastore.get(userKey);
			boolean active = u.getBoolean("active_account");
			String role = u.getString("user_role");
			LOG.info("------- " + accessMap.get("SU").size());

			return active && accessMap.get(role).contains(access);
		}
		catch (Exception e) {
			return false;
		}	
	}
}
