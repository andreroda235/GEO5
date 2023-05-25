package pt.unl.fct.di.apdc.geo5.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class AccessMap {

	public AccessMap () {
		
	}
	
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Map<String, Set<String>> accessMap;
	
	//TODO
	static {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        //MOD
        Set<String> modAccess = new HashSet<String>();
        map.put("MOD", modAccess);
        
        //GBO
        Set<String> gboAccess = new HashSet<String>();
        map.put("GBO", gboAccess);
        modAccess.add(Access.PERMISSION_DELETE_USER);
        modAccess.add(Access.PERMISSION_DELETE_INACTIVE_USERS);

        //SU
        Set<String> suAccess = new HashSet<String>();
        map.put("SU", suAccess);
        modAccess.addAll(modAccess);
        modAccess.addAll(gboAccess);

        accessMap = Collections.unmodifiableMap(map);

	}
	
	public static boolean hasAccess(String access, String user) {
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(user);
			Entity u = datastore.get(userKey);
			String role = u.getString("user_role");
			return accessMap.get(role).contains(access);
		}
		catch (Exception e) {
			return false;
		}	
	}
}
