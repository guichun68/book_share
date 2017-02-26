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
}
