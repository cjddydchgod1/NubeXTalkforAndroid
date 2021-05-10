/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module;

import x.com.nubextalk.R;

public class CodeResources {
    public static final String TAG = "NubeX Talk";
    public static final String VERSION = "NubeX Talk 1.0.0";
    public static final String EMPTY = "";
    public static final String FIREBASE_LOC = "asia-northeast3";

    public static final String LOGIN = "로그인";
    public static final String LOGOUT = "로그아웃";
    public static final String HOSPITAL_ID = "w34qjptO0cYSJdAwScFQ";
    public static final String PICTURE = "사진";
    public static final String MODIFICATION = "수정";
    public static final String COMPLETE = "완료";
    public static final String CONFIRM = "확인";
    public static final String CANCEL = "취소";
    public static final String ALARM = "알림";
    public static final String EXIT = "나가기";


    /**
     * Title Resource
     */
    public static final String TITLE_CHAT_LIST = "채팅";
    public static final String TITLE_CHAT_SETTING = "채팅방 설정";
    public static final String TITLE_FRIEND_LIST = "친구";
    public static final String TITLE_PACS = "PACS";
    public static final String TITLE_SETTING = "설정";


    /**
     * Path Resource
     */

    public static final String PATH_IMAGE = "image/*";
    public static final String PATH_STORAGE1 = "hospital/";
    public static final String PATH_STORAGE2 = "/users/";
    public static final String PATH_STORAGE3 = "/chatroom/";
    public static final String PATH_PACS_VIEWER = "/mobile/app/?studyId=";
    public static final String PATH_PACS_HOME = "/mobile/app/";


    /**
     * Date Format
     */
    public static final String DATE_FORMAT1 = "HH:mm";
    public static final String DATE_FORMAT2 = "yyyy.MM.dd (E)";
    public static final String DATE_FORMAT3 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT4 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FINAL = "2100-12-31 23:59:59";

    /**
     * Icon Resource
     */
    public static final String ICON_MINUS = "{fas-times 10dp #ffffff}";
    public static final String ICON_FIX_TOP = "{fas-map-pin 16dp}";
    public static final String ICON_ALARM_OFF = "{far-bell-slash 16dp}";
    public static final String ICON_DOWNLOAD = "{fas-download 35dp #000000}";
    public static final String ICON_SEND_CHAT = "{far-paper-plane 30dp #747475}";

    /**
     * Drawable Resource
     */
    public static final int STATUS_BUSY = R.drawable.ic_status_busy;
    public static final int STATUS_OFF = R.drawable.ic_status_off;
    public static final int STATUS_ON = R.drawable.ic_status_on;
    public static final int DEFAULT_PROFILE = R.drawable.ic_default_profile_24;
    public static final int DEFAULT_GROUP_PROFILE = R.drawable.ic_default_people_24;
    public static final int SEARCH = R.id.toolbar_search;

    /**
     * FirebaseFunctionsManager Resource
     */
    public static final String FUNCTION_CREATE_CHAT_ROOM = "createChatRoom";
    public static final String FUNTION_CREATE_CHAT = "createChat";
    public static final String FUNCTION_GET_CHAT_ROOM = "getChatRoom";
    public static final String FUNCTION_NOTIFY_TO_CHAT_ROOM_ADDED_USER = "notifyToChatRoomAddedUser";
    public static final String FUNCTION_EXIT_CHAT_ROOM = "exitChatRoom";
    public static final String FUNCTION_GET_USER_IN_CHAT_ROOMS_ID = "getUserInChatRoomsId";
    public static final String FUNCTION_GET_PERSONAL_CHAT_ROOM_ID = "getPersonalChatRoomId";

    /**
     * FirebaseMsgService Resource
     */
    public static final String CODE_SYSTEM_ROOM_CREATED = "SYSTEM_ROOM_CREATED";
    public static final String CODE_SYSTEM_MEMBER_ADD = "SYSTEM_MEMBER_ADD";
    public static final String CODE_SYSTEM_MEMBER_EXIT = "SYSTEM_MEMBER_EXIT";
    public static final String CODE_CHAT_CONTENT_CREATED = "CHAT_CONTENT_CREATED";

    public static final String MSG_ROOM_CREATED = "님이 채팅방을 개설 하였습니다.";
    public static final String MSG_MEMBER_ADD1 = "님이\n";
    public static final String MSG_MEMBER_ADD2 = "님 ";
    public static final String MSG_MEMBER_ADD3 = "을 초대 하였습니다.";

    public static final String MSG_MEMBER_EXIT = "님이 채팅방을 나갔습니다.";

    /**
     * NotifyManager Resource
     */
    public static final String ID_NOTIFICATION_CHANNEL = "0608";
    public static final String ID_NOTIFICATION_GROUP = "NoticationGroup";
    public static final String NAME_NOTIFICATION_CHANNEL = "NubeXTalk Notification Channel";
    public static final String DESCRIPTION_NOTIFICATION_CHANNEL = "This is notification channel for NubeXTalk.";

    /**
     * Chat Adapter Resource
     */
    public static final String SENDING = "전송중";
    public static final String EMPTY_IMAGE = "empty image";
    /**
     * PACSPagerAdapter
     */
    public static final int REFERENCE_ITEM_SIZE = 2;

    /**
     * ChatListFragment Resource
     */
    public static final String ADD_MEMBER = "대화상대 추가";
    public static final String FIX_TOP = "상단 고정";
    public static final String RENAME_TITLE = "채팅방 이름 편집";
    public static final String ALARM_OFF = " 해제";
    public static final String ALARM_ON = " 설정";

    /**
     * FriendListFragment Resource
     */


    /**
     * PACSChatListFragment Resource
     */
    public static final String MSG_EMPTY_CHAT_LIST = "선택된 채팅 목록이 없습니다.";

    /**
     * PACSFriendListFragment Resource
     */
    public static final String MSG_EMPTY_FRIEND_LIST = "선택된 친구 목록이 없습니다.";

    /**
     * RoomNameModificationDialogFragment
     */

    public static final String MSG_EMPTY_ROOM_NAME = "채팅방 이름을 입력하세요.";

    /**
     * SettingFragment Resource
     */

    public static final int EXE_ALARM = 1;
    public static final int EXE_THEME = 2;
    public static final int EXE_HOW_TO_USE = 3;
    public static final int EXE_LOGOUT = 4;
    public static final int EXE_VERSION_INFO = 5;
    public static final String SETTING_THEME = "테마설정";

    /**
     * ChatAddActivity Resource
     */
    public static final String MSG_INVITE_MEMBER = "상대방을 초대중 입니다.";

    /**
     * ChatImageViewActivity Resource
     */
    public static final String MSG_COMPLETE_DOWNLOAD = "다운로드 완료";

    /**
     * ChatRoomActivity Resource
     */
    public static final String MSG_FIX_TOP_ON = "상단고정이 설정 되었습니다.";
    public static final String MSG_FIX_TOP_OFF = "상단고정이 해제 되었습니다.";
    public static final String MSG_ALARM_ON = "알람기능이 설정 되었습니다.";
    public static final String MSG_ALARM_OFF = "알람기능이 해제 되었습니다.";
    public static final String MSG_EMPTY_CONTENT = "메세지를 입력하세요";

    /**
     * LoginActivity Resource
     */
    public static final String MSG_LOGIN_FAIL = "아이디/비밀번호를 확인하세요.";

    /**
     * MainActivity Resource
     */
    public static final int CHAT_ADD = 0;
    public static final int MOVE_TO_CHAT_ROOM = 1;

    /**
     * ThemeMode Resource
     */
    public static final String THEME_MODE = "theme mode";
    public static final int LIGHT_MODE = 0;
    public static final int DARK_MODE = 1;
    public static final int USER_MODE = 2;
}

