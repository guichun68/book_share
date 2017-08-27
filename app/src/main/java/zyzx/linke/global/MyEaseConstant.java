package zyzx.linke.global;

/**
 * Created by austin on 2017/7/17.
 */


import com.hyphenate.easeui.EaseConstant;

public class MyEaseConstant extends EaseConstant{
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACCOUNT_LOST_CONN = "lost_con";
    public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

    //扩展消息用key (必须跟easeUI库中的EaseConstant中相关变量一致)
    public static final String EXTRA_FROM_AVATAR = "avator";
    public static final String EXTRA_TO_AVATAR = "to_avator";
    public static final String EXTRA_FROM_NICKNAME = "nick";
    public static final String EXTRA_TO_NICKNAME = "to_nick";
    public static final String EXTRA_BOOKID = "bookId";
    public static final String EXTRA_BOOKTITLE = "bookTitle";
    public static final String EXTRA_UID = "uid";
    public static final String EXTRA_USERID = "userId";//环信id
    //同APP主项目Const类中同名变量一致
    public static final String ADMIN_USERID = "1053";//系统 用户的环信id
    public static final String EXTRA_SHARE_TYPE = "bookShareType";
    public static final String EXTRA_BEG_AGREE = "beg_agree";
}
