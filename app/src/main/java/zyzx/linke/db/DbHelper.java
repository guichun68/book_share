package zyzx.linke.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import zyzx.linke.model.Area;


public class DbHelper extends SQLiteOpenHelper{

	private static final int DB_VERSION = 1;
	private Context context;

	private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "+UserDao.TABLE_USER_NAME
			+ "(`_id` INTEGER PRIMARY KEY autoincrement ,"
			+ "`userid` INTEGER ,"
			+ "`login_name` varchar(30) ,"
			+ "`head_icon` varchar(255) DEFAULT NULL);";

	private static final String CREATE_PROVINCE_TABLE = "CREATE TABLE IF NOT EXISTS "+UserDao.TABLE_PROVINCE
			+ "(`pid` INTEGER PRIMARY KEY ,"
			+ "`areacode` varchar(6) ,"
			+ "`depth` INTEGER ,"
			+ "`name` varchar(30),"
			+ "`parentid` INTEGER ,"
			+ "`zipcode` varchar(6));";

    public DbHelper(Context context) {
		super(context, "linke.db", null, DB_VERSION);
		this.context = context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate db 执行了");
		/*
		 * 本地创建缓存sqlite数据库，缓存用户信息 userid/login_name/headUrl(头像地址)
		 */
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_PROVINCE_TABLE);
		insertProData(db);
	}
	//数据库的版本变更时候执行1---》2
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("oldVersion:"+oldVersion+" newVersion:"+newVersion);
			if(oldVersion<2){
				db.execSQL(CREATE_PROVINCE_TABLE);
				insertProData(db);
			}
	}


	private ArrayList<Area> areas = new ArrayList<>();
	/**
	 * 初始化省份数据
	 */
	private void insertProData(SQLiteDatabase db) {
		Area a = new Area(1,"010",1,"北京",0,"100000");
		Area a1 = new Area(2,"",1,"安徽",0,"");
		Area a2 = new Area(3,"",1,"福建",0,"");
		Area a3 = new Area(4,"",1,"甘肃",0,"");
		Area a4 = new Area(5,"",1,"广东",0,"");
		Area a5 = new Area(6,"",1,"广西",0,"");
		Area a6 = new Area(7,"",1,"贵州",0,"");
		Area a7 = new Area(8,"",1,"海南",0,"");
		Area a8 = new Area(9,"",1,"河北",0,"");
		Area a9 = new Area(10,"",1,"河南",0,"");
		Area a0 = new Area(11,"",1,"黑龙江",0,"");
		Area b1 = new Area(12,"",1,"湖北",0,"");
		Area b2 = new Area(13,"",1,"湖南",0,"");
		Area b3 = new Area(14,"",1,"吉林",0,"");
		Area b4 = new Area(15,"",1,"江苏",0,"");
		Area b5 = new Area(16,"",1,"江西",0,"");
		Area b6 = new Area(17,"",1,"辽宁",0,"");
		Area b7 = new Area(18,"",1,"内蒙古",0,"");
		Area b8 = new Area(19,"",1,"宁夏",0,"");
		Area b9 = new Area(20,"",1,"青海",0,"");
		Area b0 = new Area(21,"",1,"山东",0,"");
		Area c1 = new Area(22,"",1,"山西",0,"");
		Area c2 = new Area(23,"",1,"陕西",0,"");
		Area c3 = new Area(24,"021",1,"上海",0,"200000");
		Area c4 = new Area(25,"",1,"四川",0,"");
		Area c5 = new Area(26,"022",1,"天津",0,"300000");
		Area c6 = new Area(27,"",1,"西藏",0,"");
		Area c7 = new Area(28,"",1,"新疆",0,"");
		Area c8 = new Area(29,"",1,"云南",0,"");
		Area c9 = new Area(30,"",1,"浙江",0,"");
		Area c0 = new Area(31,"023",1,"重庆",0,"404100");
		Area d1 = new Area(32,"852",1,"香港",0,"999077");
		Area d2 = new Area(33,"853",1,"澳门",0,"999078");
		Area d3 = new Area(34,"886",1,"台湾",0,"");
		Area d4 = new Area(51,"",1,"国外",0,"");
		areas.add(a);
		areas.add(a0);
		areas.add(a1);
		areas.add(a2);
		areas.add(a3);
		areas.add(a4);
		areas.add(a5);
		areas.add(a6);
		areas.add(a7);
		areas.add(a8);
		areas.add(a9);
		areas.add(b1);
		areas.add(b2);
		areas.add(b3);
		areas.add(b4);
		areas.add(b5);
		areas.add(b6);
		areas.add(b7);
		areas.add(b8);
		areas.add(b9);
		areas.add(b0);
		areas.add(c1);
		areas.add(c2);
		areas.add(c3);
		areas.add(c4);
		areas.add(c5);
		areas.add(c6);
		areas.add(c7);
		areas.add(c8);
		areas.add(c9);
		areas.add(c0);
		areas.add(d1);
		areas.add(d2);
		areas.add(d3);
		areas.add(d4);
		UserDao dao = UserDao.getInstance(context);
		dao.addAreas(db,areas);
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
