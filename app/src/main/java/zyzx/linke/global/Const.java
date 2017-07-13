package zyzx.linke.global;

import android.graphics.Color;


/**
 * @since 3.3.0
 * Created by hongming.wang on 2016/12/19.
 */

public class Const {
	public static final int SUCC_ERR_CODE = 0;

    public static final String TAG = "BookShare";
	public static final String ONCLICK = "zyzx.linke";
    /**
	 *高德web服务--临客-存储图书经纬度--key
	 */
	public static String key = "49f0e1fd42a68fcf794c5e135a357f1a";


	public static final String type = "\u7c7b\u522b"; // 类别
	public static final String address = "\u5730\u5740"; // 地址
	public static final String LODING_LOCATION = "定位中...";
	//默认搜索半径 单位m 取值范围[0,50000]

	//-----------------------zyzx------
	public static final int CAMERA_REQUEST_CODE=0x37F;
	//做扩展消息时使用的key
	public static final String MSG_FLAG_UERID="msg_flag_uerid";
	public static final String MSG_FLAG_LOGIN_NAME="msg_flag_login_name";
	public static final String MSG_FLAG_HEAD_URL="msg_flag_head_url";

	public static final int AVATAR_SELECTION=0x38F;//头像选择
	public static final int PAGE_SIZE_MYBOOKS = 10;//我的所有书籍 页 每次刷新页面条目数量

	//状态：在架
	public static final String BOOK_STATUS_ONSHELF="2308b2e0-3df2-11e7-84d2-005056c00001";
	//分享中
	public static final String BOOK_STATUS_SHARED="29890cac-3df2-11e7-84d2-005056c00001";
	//已借出
	public static final String BOOK_STATUS_LOANED="5c79df56-5f9c-11e7-bccd-00163e062b56";
	//已借入
	public static final String BOOK_STATUS_BORROWED="37277044-3df2-11e7-84d2-005056c00001";
	//交换中
	public static final String BOOK_STATUS_EXCHANGING="7fc7a219-5f9c-11e7-bccd-00163e062b56";

	//个人笔记分类
	public static final String CLASSIFY_ZHONGKAO ="548f5fca-6492-11e7-b86c-68f72877da0a";//中考
	public static final String CLASSIFY_GAOKAO ="67c6918d-6492-11e7-b86c-68f72877da0a";//高考
	public static final String CLASSIFY_KAOYAN ="7c729c27-6493-11e7-b86c-68f72877da0a";//考研
	public static final String CLASSIFY_ZIXUE ="8d29dda9-6493-11e7-b86c-68f72877da0a";//自学考试
	public static final String CLASSIFY_SIJI ="97ad84f6-6493-11e7-b86c-68f72877da0a";//四级
	public static final String CLASSIFY_LIUJI ="a1f7b723-6493-11e7-b86c-68f72877da0a";//六级
	public static final String CLASSIFY_GONGWUYUAN ="a81f8054-6493-11e7-b86c-68f72877da0a";//公务员
	public static final String CLASSIFY_SIKAO ="ad7a5ff2-6493-11e7-b86c-68f72877da0a";//司考
	public static final String CLASSIFY_YIXUE ="b3ef6ccf-6493-11e7-b86c-68f72877da0a";//医学类
	public static final String CLASSIFY_TUOFU ="b9eec048-6493-11e7-b86c-68f72877da0a";//托福
	public static final String CLASSIFY_YASI ="bf317697-6493-11e7-b86c-68f72877da0a";//雅思
	public static final String CLASSIFY_GRE ="c410f1ae-6493-11e7-b86c-68f72877da0a";//GRE
	public static final String CLASSIFY_JLPT ="c98b3ad9-6493-11e7-b86c-68f72877da0a";//JLPT
	public static final String CLASSIFY_XIAOYUZHONG ="cf9af9b2-6493-11e7-b86c-68f72877da0a";//小语种
	public static final String CLASSIFY_BIJI ="d444e6d8-6493-11e7-b86c-68f72877da0a";//课堂笔记
	public static final String CLASSIFY_DAAN ="d9057ccf-6493-11e7-b86c-68f72877da0a";//答案
	public static final String CLASSIFY_QITA ="de18452c-6493-11e7-b86c-68f72877da0a";//	其他

}
