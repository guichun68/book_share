package zyzx.linke.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper{

	public static final int DB_VERSION = 1;

    public DbHelper(Context context) {
		super(context, "linke.db", null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate db 执行了");
		/*
		 * 本地创建缓存sqlite数据库，缓存用户信息 userid/login_name/headUrl(头像地址)
		 */
		db.execSQL("CREATE TABLE IF NOT EXISTS `zyzx_user` "
				+ "(`_id` INTEGER PRIMARY KEY autoincrement ,"
				+ "`userid` INTEGER ,"
				+ "`login_name` varchar(30) ,"
				+ "`head_icon` varchar(255) DEFAULT NULL);");

	}
	//数据库的版本变更时候执行1---》2
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("oldVersion:"+oldVersion+" newVersion:"+newVersion);
			if(oldVersion==1 && newVersion==2){

			}
	}
	
	 @Override
	 public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	          /**
	           * 执行数据库的降级操作
	           * 1、只有新版本比旧版本低的时候才会执行
	           * 2、如果不执行降级操作，会抛出异常
	           */
	          System.out.println("#############数据库降级了##############：" + DB_VERSION);
//	          super.onDowngrade(db, oldVersion, newVersion);
	 }
	 
	 
}
