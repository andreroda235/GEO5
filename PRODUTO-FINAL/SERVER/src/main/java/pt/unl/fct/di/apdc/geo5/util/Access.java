package pt.unl.fct.di.apdc.geo5.util;

//TODO
public class Access {
	public static final String PERMISSION_DELETE_USER = "/delete/user";
	public static final String PERMISSION_DELETE_INACTIVE_USERS = "/delete/inactiveUsers";

	public static final String PERMISSION_BACKOFFICE_ADD_BO = "/backOffice/addBO";
	public static final String PERMISSION_BACKOFFICE_ADD_BOM = "/backOffice/addBOM";
	public static final String PERMISSION_BACKOFFICE_ADD_BOP = "/backOffice/addBOP";
	public static final String PERMISSION_BACKOFFICE_LIST_ACTIVE_ADMINS = "/backOffice/listActiveAdmins";
	public static final String PERMISSION_BACKOFFICE_UPDATE_ROLE = "/backOffice/updateRole";

	public static final String PERMISSION_MODERATOR_MAKE_USER_ACTIVE= "/communityModerator/makeUserActive";
	public static final String PERMISSION_MODERATOR_MAKE_USER_INACTIVE = "/communityModerator/makeUserInactive";
	public static final String PERMISSION_MODERATOR_MAKE_COMMENT_ROUTE_INACTIVE = "/makeRouteCommentInactive/{commentID}";
	public static final String PERMISSION_MODERATOR_MAKE_COMMENT_GEOSPOT_INACTIVE = "/makeGeoSpotCommentInactive/{commentID}";

	public static final String PERMISSION_MODERATOR_MAKE_ROUTE_ACTIVE= "/routeModerator/makeRouteActive";
	public static final String PERMISSION_MODERATOR_MAKE_ROUTE_INACTIVE = "/routeModerator/makeRouteInactive";
	
	public static final String PERMISSION_ROUTE_SUBMIT = "/route/submit";
	public static final String PERMISSION_ROUTE_GET = "/route/get";
	public static final String PERMISSION_ROUTE_GET_OF_USER = "/route/user";
	public static final String PERMISSION_ROUTE_LIST_ACTIVE = "/route/listActive";
	public static final String PERMISSION_ROUTE_SEARCH_ACTIVE = "/route/searchActive";
	public static final String PERMISSION_ROUTE_SEARCH_OF_USER = "/route/searchUser";
	public static final String PERMISSION_ROUTE_GET_PICTURES = "/route/{routeID}/pictures";
	
	public static final String PERMISSION_GEOSPOT_SUBMIT = "/geoSpot/submit";
	public static final String PERMISSION_GEOSPOT_GET = "/geoSpot/get";
	public static final String PERMISSION_GEOSPOT_LIST_ACTIVE = "/geoSpot/listActive";
	public static final String PERMISSION_GEOSPOT_GET_PICTURES = "/geoSpot/{geoSpotName}/pictures";
	
	public static final String PERMISSION_USER_GET = "/user/get";
	public static final String PERMISSION_USER_LIST_ACTIVE = "/user/listActive";
	public static final String PERMISSION_USER_LIST_INACTIVE = "/user/listInactive";
	public static final String PERMISSION_USER_LAST_24H_LOGINS = "/user/last24hlogins";
	public static final String PERMISSION_USER_GET_USER_PICTURE = "/user/{username}/picture";
	public static final String PERMISSION_USER_MAKE_ACCOUNT_INACTIVE = "/user/makeAccountInactive";
	public static final String PERMISSION_USER_LIST_ALL_USERS = "/user/listAllUsers";
	public static final String PERMISSION_USER_LIST_ALL_ADMINS = "/user/listAllAdmins";

	public static final String PERMISSION_UPDATE_USER = "/update";
	public static final String PERMISSION_UPDATE_USER_NOPASS = "/update/v2";
	
	public static final String PERMISSION_STORAGE_UPLOAD = "/storage/upload";
	public static final String PERMISSION_STORAGE_UPLOAD_USER_PICTURE = "/storage/upload/user/{username}";
	public static final String PERMISSION_STORAGE_UPLOAD_ROUTE_PICTURE = "/storage/upload/route/{routeid}";
	public static final String PERMISSION_STORAGE_UPLOAD_GEOSPOT_PICTURE = "/storage/upload/geoSpot/{geoSpotName}";
	public static final String PERMISSION_STORAGE_UPLOAD_INFO_PICTURE = "/storage/upload/info/{infoName}";
	public static final String PERMISSION_STORAGE_DOWNLOAD = "/storage/download";
	public static final String PERMISSION_STORAGE_DELETE = "/storage/delete";
	
	public static final String PERMISSION_QUIZZ_SUBMIT = "/quizz/sumbit";
	public static final String PERMISSION_QUIZZ_LIST_ACTIVE = "/quizz/listActive";
	public static final String PERMISSION_QUIZZ_GET_RANDOM = "/quizz/getRandom";
	
	public static final String PERMISSION_INFO_SUBMIT = "/info/sumbit";
	public static final String PERMISSION_INFO_LIST_ACTIVE = "/info/listActive";
	public static final String PERMISSION_INFO_GET_PICTURES = "/info/{infoName}/pictures";
	
	public static final String PERMISSION_COMMENT_ROUTE_ADD = "/comment/route";
	public static final String PERMISSION_COMMENT_GEOSPOT_ADD = "/comment/geoSpot";
	public static final String PERMISSION_COMMENT_LIST_ACTIVE_ROUTE = "/comment/route/listActive";
	public static final String PERMISSION_COMMENT_LIST_ACTIVE_GEOSPOTS = "/comment/geoSpot/listActive";
	public static final String PERMISSION_COMMENT_ROUTE_GET = "/comment/route/{routeID}";
	public static final String PERMISSION_COMMENT_GEOSPOT_GET = "/comment/geoSpot/{geoSpotName}";

}