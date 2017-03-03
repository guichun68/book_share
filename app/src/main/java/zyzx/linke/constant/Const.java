package zyzx.linke.constant;

import android.graphics.Color;

/**
 * @since 3.3.0
 * Created by hongming.wang on 2016/12/19.
 */

public class Const {
	public static final int SUCCESSCODE = 1000;
	/**
	 *高德web服务--临客-存储图书经纬度--key
	 */
	public static String key = "49f0e1fd42a68fcf794c5e135a357f1a";
	/**
	 *高德web服务--临客-存储图书经纬度--tableid标识高德后台创建的哪一个地图表
	 */
	public static String mTableID = "58b687cc305a2a6810d2b236";

	/**
	 * 地图中绘制多边形、圆形的边界颜色
	 * @since 3.3.0
	 */
	public static final int STROKE_COLOR = Color.argb(180, 63, 145, 252);
	/**
	 * 地图中绘制多边形、圆形的填充颜色
	 * @since 3.3.0
	 */
	public static final int FILL_COLOR = Color.argb(163, 118, 212, 243);

	/**
	 * 地图中绘制多边形、圆形的边框宽度
	 * @since 3.3.0
	 */
	public static final float STROKE_WIDTH = 5F;


	public static final String Kilometer = "\u516c\u91cc";// "公里";
	public static final String Meter = "\u7c73";// "米";
	public static final String ByFoot = "\u6b65\u884c";// "步行";
	public static final String To = "\u53bb\u5f80";// "去往";
	public static final String Station = "\u8f66\u7ad9";// "车站";
	public static final String TargetPlace = "\u76ee\u7684\u5730";// "目的地";
	public static final String StartPlace = "\u51fa\u53d1\u5730";// "出发地";
	public static final String About = "\u5927\u7ea6";// "大约";
	public static final String Direction = "\u65b9\u5411";// "方向";

	public static final String GetOn = "\u4e0a\u8f66";// "上车";
	public static final String GetOff = "\u4e0b\u8f66";// "下车";
	public static final String Zhan = "\u7ad9";// "站";

	public static final String cross = "\u4ea4\u53c9\u8def\u53e3"; // 交叉路口
	public static final String type = "\u7c7b\u522b"; // 类别
	public static final String address = "\u5730\u5740"; // 地址
	public static final String PrevStep = "\u4e0a\u4e00\u6b65";
	public static final String NextStep = "\u4e0b\u4e00\u6b65";
	public static final String Gong = "\u516c\u4ea4";
	public static final String ByBus = "\u4e58\u8f66";
	public static final String Arrive = "\u5230\u8FBE";// 到达
	public static final String HOUR = "\u5C0F\u65F6"; // 小时
	public static final String MINIATE = "\u5206\u949F"; // 分钟
	public static final String LODING_LOCATION = "定位中...";
	public static final String LODING_GET_DATA = "数据加载中...";
	public static final String BUS_ROUTE_DETAIL = "公交路线详情";
	public static final String Drive_ROUTE_DETAIL = "驾车路线详情";
	public static final String WALK_ROUTE_DETAIL = "步行路线详情";
	public static final String TAXI_TIP = "打车约";
	public static final String YUAN = "元";
	//默认搜索半径 单位m 取值范围[0,50000]
	public static final int SEARCH_AROUND = 10000;
	public static final int WALK_DISTANCE = 1000;
	public static final float ROUTE_LINE_WIDTH = 22f;

	public static final int ERROR_CODE_SOCKE_TIME_OUT = 23;
	public static final int ERROR_CODE_UNKNOW_HOST = 27;
	public static final int ERROR_CODE_UNKNOWN = 31;
	public static final int ERROR_CODE_FAILURE_AUTH = 32;
	public static final int ERROR_CODE_SCODE = 33;
	public static final int ERROR_CODE_TABLEID = 34;
	public static final int NO_ERROR = 1000;
	public static final int IAMGE_MAX_WIDTH = 960;
	public static final int IMAGE_MAX_HEIGHT = 1024;
}
