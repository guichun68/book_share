package zyzx.linke.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import zyzx.linke.global.Const;
import zyzx.linke.model.Area;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.UIUtil;

public class UserDao {
	/**
	 * 用于更新cursor的Uri
	 */
	private static final Uri uri = Uri.parse("content://www.linke.com");
	SQLiteDatabase db;
	private DbHelper helper;
	private Context context;
	public static final String TABLE_USER_NAME = "zyzx_user";//用户表
	public static final String TABLE_PROVINCE = "zyzx_provice";//省份表

	private static final String COLUM_USER_ID = "userid";
	private static final String COLUM_LOGIN_NAME= "login_name";
	private static final String COLUM_HEAD_ICON= "head_icon";

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
	 * @param userVO 带插入db用户
	 */
	public void add(UserVO userVO) {
		if(queryUserByUid(userVO.getUserid())!=null){
			updateUser(userVO);
			return;
		}
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userid", userVO.getUserid());
		values.put("login_name", userVO.getLogin_name());
		values.put("head_icon", userVO.getHead_icon());
		long affectRows = db.insert(TABLE_USER_NAME, null, values);
		// db.close();

		// 数据变化了--被观察者
		// Uri uri = Uri.parse("content://com.app.hbx.changedb");
		// context.getContentResolver().notifyChange(uri, null);
//		notifyCursor();
		UIUtil.print("zyzx-affectRows:"+affectRows);
//		db.close();
	}

	/**
	 * 更新用户信息（根据userId更新）
	 */
	public void updateUser(UserVO userPO){
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(userPO.getLogin_name()!=null)
			values.put(COLUM_LOGIN_NAME, userPO.getLogin_name());
		if(userPO.getHead_icon()!=null)
			values.put(COLUM_HEAD_ICON, userPO.getHead_icon());
		int affectRows = db.update(TABLE_USER_NAME,values,"userid=?",new String[]{String.valueOf(userPO.getUserid())});
		UIUtil.print("zyzx-affectRows:"+affectRows);
	}


	/**根据用户id来删除特定的用户
	 * @param userId
	 * @return 影响的行数
	 */
	public void delUserByUid(Integer userId){
		try {
			db = helper.getWritableDatabase();
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
		db = helper.getWritableDatabase();
		db.delete(TABLE_USER_NAME, null, null);
	}


	/**
	 * 查询所有用户
	 * 
	 * @return
	 */
	public ArrayList<UserVO> queryAll() {
		ArrayList<UserVO> allUserPOInfo = new ArrayList<>();
		db = helper.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(TABLE_USER_NAME, new String[] { "userid", "login_name", "head_icon" }, null, null, null,null,
				"login_name desc");
		while (cursor.moveToNext()) {
			allUserPOInfo.add(new UserVO(cursor.getInt(cursor.getColumnIndex("userid")), cursor.getString(cursor
					.getColumnIndex("login_name")), cursor.getString(cursor.getColumnIndex("head_icon"))));
		}
		// 为cursor设置通知提醒的uri
		cursor.setNotificationUri(context.getContentResolver(), uri);
		cursor.close();
//		db.close();
		return allUserPOInfo;
	}


	/**
	 * 超期消息删除(删除本地数据库中超过30天的推送消息(普通文本消息))
	 * 
	 * @return true:删除成功(影响>=0条记录) false:删除失败
	 */
	public boolean delMsgOutOfDays() {
		db = helper.getWritableDatabase();
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
	public UserVO queryUserByUid(Integer uid){
		db = helper.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_USER_NAME, new String[]{"userid", "login_name", "head_icon"}, "userid=?", new String[]{String.valueOf(uid)}, null, null, null);
		Cursor cursor = db.rawQuery("select * from zyzx_user where userid=?",new String[]{String.valueOf(uid)});
		UserVO u=null;
		if(cursor.getCount()==1){
			cursor.moveToNext();
			u = new UserVO(cursor.getInt(cursor.getColumnIndex("userid")),
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
//		db.close();
		return u;
	}
	public void closeDb(){
		if(db!=null){
			db.close();
		}
	}

	/**
	 * 添加一条用户数据
	 * @param areas 待插入省份信息列表
	 */
	public void addAreas(SQLiteDatabase db,ArrayList<Area> areas) {
//		db = helper.getWritableDatabase();

		for(int i=0;i<areas.size();i++){
			ContentValues values = new ContentValues();
			values.put("pid", areas.get(i).getId());
			values.put("areacode", areas.get(i).getAreacode());
			values.put("depth", areas.get(i).getDepth());
			values.put("name", areas.get(i).getName());
			values.put("parentid", areas.get(i).getParentid());
			values.put("zipcode", areas.get(i).getZipcode());
			long affectRows = db.insert(TABLE_PROVINCE, null, values);
			if(affectRows>0){
				UIUtil.showTestLog(Const.TAG,"插入第"+i+"条数据成功");
			}else{
				UIUtil.showTestLog(Const.TAG,"插入第"+i+"条数据失败-----"+areas.get(i));
			}
		}
	}

	/**
	 * 查询指定id的省份信息
	 * @param pid
	 * @return
	 */
	public Area queryProByid(Integer pid){
		db = helper.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_USER_NAME, new String[]{"userid", "login_name", "head_icon"}, "userid=?", new String[]{String.valueOf(uid)}, null, null, null);
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_PROVINCE+" WHERE pid=?",new String[]{String.valueOf(pid)});
		Area a=null;
		if(cursor.getCount()==1){
			cursor.moveToNext();
			a = new Area(cursor.getInt(cursor.getColumnIndex("pid")),
					cursor.getString(cursor.getColumnIndex("areacode")),
					cursor.getInt(cursor.getColumnIndex("depth")),
					cursor.getString(cursor.getColumnIndex("name")),
					cursor.getInt(cursor.getColumnIndex("parentid")),
					cursor.getString(cursor.getColumnIndex("zipcode")));
		}
		if(cursor.getCount()>1){
			UIUtil.showTestLog(Const.TAG,"there are exceed one record in zyzx_area where pid="+pid+"!??");
		}
		cursor.close();
//		db.close();
		return a;
	}
	/**
	 * 查询指定名称的省份的详情
	 * @param proName 省份名（依据表中数据规范，不包含“省”字）
	 * @return
	 */
	public Area queryProByName(String proName){
		db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_PROVINCE+" WHERE name=?",new String[]{proName});
		Area a=null;
		if(cursor.getCount()==1){
			cursor.moveToNext();
			a = new Area(cursor.getInt(cursor.getColumnIndex("pid")),
					cursor.getString(cursor.getColumnIndex("areacode")),
					cursor.getInt(cursor.getColumnIndex("depth")),
					cursor.getString(cursor.getColumnIndex("name")),
					cursor.getInt(cursor.getColumnIndex("parentid")),
					cursor.getString(cursor.getColumnIndex("zipcode")));
		}
		if(cursor.getCount()>1){
			UIUtil.showTestLog(Const.TAG,"there are exceed one record in zyzx_area where name="+proName+"!??");
		}
		cursor.close();
//		db.close();
		return a;
	}

	//查询所有省份(或直辖市+“国外”)
	public ArrayList<Area> queryAllPro(){
		db = helper.getReadableDatabase();
		ArrayList<Area> areas = new ArrayList<>();
//		Cursor cursor = db.query(TABLE_USER_NAME, new String[]{"userid", "login_name", "head_icon"}, "userid=?", new String[]{String.valueOf(uid)}, null, null, null);
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_PROVINCE+" WHERE parentid=?",new String[]{String.valueOf(0)});
		while (cursor.moveToNext()) {
			Area a = new Area(cursor.getInt(cursor.getColumnIndex("pid")),
					cursor.getString(cursor.getColumnIndex("areacode")),
					cursor.getInt(cursor.getColumnIndex("depth")),
					cursor.getString(cursor.getColumnIndex("name")),
					cursor.getInt(cursor.getColumnIndex("parentid")),
					cursor.getString(cursor.getColumnIndex("zipcode")));
			areas.add(a);
		}
		cursor.close();
		return areas;
	}

}
