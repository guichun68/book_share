package zyzx.linke.presentation;

import android.util.ArrayMap;

import zyzx.linke.base.IPresenter;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;

/**
 * Created by austin on 2017/2/17.
 * Desc: 图书操作相关逻辑
 */

public abstract class IBookPresenter extends IPresenter {

    //通过ISBN获取图书详情
    public abstract void getBookDetailByISBN(String isbn, CallBack viewCallBack);

    /**
    *将图书添加到我的书库（仅仅添加，不在地图中展示）
    *@param mBook 书籍
     *@param userId 用户id（取自uuid）
     *@param viewCallBack 回调
     **/
    public abstract void addBook2MyLib(BookDetail2 mBook,Integer userId, CallBack viewCallBack);


    /**
     * 获取用户信息和其下所有书籍
     * @param uid 用户id
     * @param pageNum 页数
     * @param viewCallBack 回调
     */
    public abstract void getUserBooks(String uid,final int pageNum,final CallBack viewCallBack);


    /**
     * 上传图片
     * @param
     * @param
     */
    public abstract void uploadBook(ArrayMap<String, Object> params, CallBack viewCallBack);

    public abstract void getMyBooks(String uid, int pageNum, CallBack viewCallBack);
    public abstract void getMySharedBooks(String uid, int pageNum, CallBack viewCallBack);

    /**
     * 用户删除图书
     * @param userid
     * @param userBookId userBook表主键
     * @param bid
     * @param callBack
     */
    public abstract void deleteUserBook(String userid,String userBookId, String bid, CallBack callBack);

    /**
     * 取消书籍分享
     * @param userBookId 用户书籍关系表（zyzx_user_books）id
     */
    public abstract void cancelShare(String userBookId,CallBack callBack);

    /**
     * 取消分享并从书架删除
     * @param userBookId
     * @param mapId
     * @param callBack
     */
    public abstract void cancelShareAndDelBook(Integer userBookId,Integer mapId,CallBack callBack);

    /**
     * 获取我所有的借入的书籍
     * @param userid
     * @param pageNum
     * @param callBack
     */
    public abstract void getMyBorrowedInBooks(Integer userid, int pageNum, CallBack callBack);

    /**
     * 检查更新
     * @param currVersionCode 当前版本号
     * @param callBack 回调
     * @param flag
     */
    public abstract void checkUpdate(int currVersionCode, CallBack callBack, boolean flag);

    //得到用户发布的图书资料分类，如 考研、中考、高考
    public abstract void getBookClassify(CallBack callBack);

    /**
     * 得到交换状态中的书籍
     * @param pageNum
     * @param viewCallBack
     */
    public abstract void getSwapBooks(int pageNum, CallBack viewCallBack);

    /**
     * 获取待交换图书详情
     * @param userBookId zyzx_user_books表主键
     * @param viewCallBack
     */
    public abstract void getSwapBookInfo(String userBookId, CallBack viewCallBack);

    //获取指定页码的交换中的技能（们）
    public abstract void getSwapSkills(int pageNum, CallBack callBack);
}
