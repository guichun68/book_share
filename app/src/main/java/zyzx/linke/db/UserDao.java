package zyzx.linke.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import zyzx.linke.model.bean.User;
import zyzx.linke.utils.UIUtil;

public class UserDao {
	/**
	 * 用于更新cursor的Uri
	 */
	private static final Uri uri = Uri.parse("content://www.linke.com");
	private DbHelper helper;
	private Context context;
	private final String TABLE_USER_NAME = "zyzx_user";//用户表

	private static UserDao mUserSQLDao;

	private UserDao(Context context) {
		helper = new DbHelper(context);
		this.context = context;
	}

	public static synchronized UserDao getInstance(Context ctx) {
		if (mUserSQLDao == null) {
			mUserSQLDao = new UserDao(ctx);
		}
		return mUserSQLDao;
	}

	/**
	 * 普通消息表中的内容已经发生变化
	 */
	public void notifyCursor() {
		context.getContentResolver().notifyChange(uri, null);
	}

	/**
	 * 添加一条用户数据
	 * @param user 带插入db用户
	 */
	public void add(User user) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userid", user.getUserid());
		values.put("login_name", user.getLogin_name());
		values.put("head_icon", user.getHead_icon());
		long affectRows = db.insert(TABLE_USER_NAME, null, values);
		// db.close();

		// 数据变化了--被观察者
		// Uri uri = Uri.parse("content://com.app.hbx.changedb");
		// context.getContentResolver().notifyChange(uri, null);
//		notifyCursor();
		db.close();
	}

	/**
	 * 更新用户信息（根据userId更新）
	 */
	public void updateUser(User user){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("login_name", user.getLogin_name());
		values.put("head_icon", user.getHead_icon());

		int affectRows = db.update(TABLE_USER_NAME,values,"userid=?",new String[]{String.valueOf(user.getUserid())});
		System.out.println("affect:"+affectRows);
	}


	/**根据用户id来删除特定的用户
	 * @param userId
	 * @return 影响的行数
	 */
	public void delUserByUid(Integer userId){
		try {
			SQLiteDatabase db = helper.getWritableDatabase();
			String sql = "delete from "+ TABLE_USER_NAME +" where userid='"+userId+"';";
			db.execSQL(sql);
//			delete = db.delete(ORD_TAB_NAME, "msgid=?", new String[]{msgId});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/*
	 * 删除所有用户
	 */
	public void deleteAllUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(TABLE_USER_NAME, null, null);
	}


	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	public ArrayList<User> queryAll() {
		ArrayList<User> allUserInfo = new ArrayList<>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(TABLE_USER_NAME, new String[] { "userid", "login_name", "head_icon" }, null, null, null,null,
				"login_name desc");
		while (cursor.moveToNext()) {
			allUserInfo.add(new User(cursor.getInt(cursor.getColumnIndex("userid")), cursor.getString(cursor
					.getColumnIndex("login_name")), cursor.getString(cursor.getColumnIndex("head_icon"))));
		}
		// 为cursor设置通知提醒的uri
		cursor.setNotificationUri(context.getContentResolver(), uri);
		cursor.close();
		db.close();
		return allUserInfo;
	}


	/**
	 * 超期消息删除(删除本地数据库中超过30天的推送消息(普通文本消息))
	 * 
	 * @return true:删除成功(影响>=0条记录) false:删除失败
	 */
	public boolean delMsgOutOfDays() {
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "delete from tab_msg where _id in( select _id from tab_msg msg where julianday('now')-julianday(date(substr(msg.[date_msg],1,10)))>30);";
		try {
			db.execSQL(sql);
			notifyCursor();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 用过用户id查询用户
	 * @param uid
	 * @return
	 */
	public User queryUserByUid(Integer uid){
		SQLiteDatabase db = helper.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_USER_NAME, new String[]{"userid", "login_name", "head_icon"}, "userid=?", new String[]{String.valueOf(uid)}, null, null, null);
		Cursor cursor = db.rawQuery("select * from zyzx_user where userid=?",new String[]{String.valueOf(uid)});
		User u=null;
		if(cursor.getCount()==1){
			cursor.moveToNext();
			u = new User(cursor.getInt(cursor.getColumnIndex("userid")),
					cursor.getString(cursor.getColumnIndex("login_name")),
					cursor.getString(cursor.getColumnIndex("head_icon")));
		}
		if(cursor.getCount()>1){
			deleteAllUser();
			UIUtil.showTestLog("zyzx","在sqlite表中查找到多个uid为"+uid+"的记录!??,已清空user表");
		}
		// 为cursor设置通知提醒的uri
		cursor.setNotificationUri(context.getContentResolver(), uri);
		cursor.close();
		db.close();
		return u;
	}
}
