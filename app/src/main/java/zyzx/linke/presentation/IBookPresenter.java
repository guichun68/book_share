package zyzx.linke.presentation;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail;

/**
 * Created by austin on 2017/2/17.
 * Desc: 图书操作相关逻辑
 */

public interface IBookPresenter {

    //通过ISBN获取图书详情
    void getBookDetailByISBN(String isbn,CallBack viewCallBack);

    /*
    *将图书添加到我的书库（仅仅添加，不在地图中展示）
     */
    void addBook2MyLib(BookDetail mBook,Integer userId,CallBack viewCallBack);

    /**
     * 向地图中添加图书坐标
     * @param bookId 指定图书
     * @param userid 指定用户
     * @param isSameBookNewPoint 如果服务器检测到用户分享过同名书籍了，是否继续增加地图分享点
     * @param latitude 纬度
     * @param longitude 经度
     * @param viewCallBack 回调
     */
    void addBook2Map(String bookId, Integer userid, boolean isSameBookNewPoint,double latitude, double longitude,CallBack viewCallBack);
}
