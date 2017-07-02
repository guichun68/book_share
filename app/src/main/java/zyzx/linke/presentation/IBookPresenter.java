package zyzx.linke.presentation;

import java.util.HashMap;
import java.util.List;

import zyzx.linke.base.IPresenter;
import zyzx.linke.base.UpdateService;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.RequestParamGetBookInfos;
import zyzx.linke.model.bean.Tags;

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
     * 向高德地图中添加图书坐标
     * @param bookId 指定图书
     * @param userid 指定用户
     * @param isSameBookNewPoint 如果服务器检测到用户分享过同名书籍了，是否继续增加地图分享点
     * @param latitude 纬度
     * @param longitude 经度
     * @param viewCallBack 回调
     */
    public abstract void addBook2Map(BookDetail2 bookId, Integer userid, boolean isSameBookNewPoint, double latitude, double longitude, CallBack viewCallBack);

    /**
     * 查询指定位置为中心，方圆around范围内的所有的图书信息
     * @param mLongti
     * @param mLati
     * @param around
     * @param callBack
     */
    public abstract void getMapBookAllAround(double mLongti, double mLati, Integer around, CallBack callBack);

    /**
     * 获取用户信息和其下所有书籍
     * @param uid 用户id
     * @param pageNum 页数
     * @param viewCallBack 回调
     */
    public abstract void getUserBooks(String uid,final int pageNum,final CallBack viewCallBack);

    /**
     * 通过给定的bookId集合来批量获取这些书籍的详细信息
     */
    public abstract void getBookInfosByBookIds(List<RequestParamGetBookInfos> requestParamJson, CallBack viewCallBack);

    /**
     * 上传图片
     * @param
     * @param
     */
    public abstract void uploadBook(HashMap<String, Object> params, CallBack viewCallBack);

    public abstract void getMyBooks(Integer userid, int pageNum, CallBack viewCallBack);

    /**
     * 用户删除图书
     * @param userid
     * @param b_id
     * @param callBack
     */
    public abstract void deleteUserBook(Integer userid, Integer b_id,Integer mapItemId, CallBack callBack);

    /**
     * 取消书籍分享
     * @param userBookId 用户书籍关系表（zyzx_user_books）id
     */
    public abstract void cancelShare(Integer userBookId,Integer mapId,CallBack callBack);

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
}
