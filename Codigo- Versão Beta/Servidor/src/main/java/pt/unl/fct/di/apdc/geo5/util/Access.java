package pt.unl.fct.di.apdc.geo5.util;

//TODO
public class Access {
	public static final String PERMISSION_DELETE_USER = "/delete/user";
	public static final String PERMISSION_DELETE_INACTIVE_USERS = "/delete/inactiveUsers";

	public static final String PERMISSION_BACKOFFICE_ADD_BO = "/backOffice/addBO";
	public static final String PERMISSION_BACKOFFICE_ADD_BOM = "/backOffice/addBOM";
	public static final String PERMISSION_BACKOFFICE_ADD_BOP = "/backOffice/addBOP";
	
	public static final String PERMISSION_MODERATOR_MAKE_USER_ACTIVE= "/communityModerator/makeUserActive";
	public static final String PERMISSION_MODERATOR_MAKE_USER_INACTIVE = "/communityModerator/makeUserInactive";

	public static final String PERMISSION_MODERATOR_MAKE_ROUTE_ACTIVE= "/routeModerator/makeRouteActive";
	public static final String PERMISSION_MODERATOR_MAKE_ROUTE_INACTIVE = "/routeModerator/makeRouteInactive";
	
	public static final String PERMISSION_ROUTE_SUBMIT = "/route/submit";
	public static final String PERMISSION_ROUTE_GET = "/route/get";
	public static final String PERMISSION_ROUTE_GET_OF_USER = "/route/user";
	public static final String PERMISSION_ROUTE_LIST_ACTIVE = "/route/listActive";
	public static final String PERMISSION_ROUTE_SEARCH_ACTIVE = "/route/searchActive";
	public static final String PERMISSION_ROUTE_SEARCH_OF_USER = "/route/searchUser";
	
	public static final String PERMISSION_GEOSPOT_SUBMIT = "/geoSpot/submit";
	public static final String PERMISSION_GEOSPOT_GET = "/geoSpot/get";
	public static final String PERMISSION_GEOSPOT_LIST_ACTIVE = "/geoSpot/listActive";
	
	public static final String PERMISSION_USER_GET = "/user/get";
	public static final String PERMISSION_USER_LIST_ACTIVE = "/user/listActive";
	public static final String PERMISSION_USER_LAST_24H_LOGINS = "/user/last24hlogins";
	public static final String PERMISSION_USER_GET_USER_PICTURE = "/user/{username}/picture";
}