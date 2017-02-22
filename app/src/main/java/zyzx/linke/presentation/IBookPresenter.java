package zyzx.linke.presentation;

import zyzx.linke.model.CallBack;

/**
 * Created by austin on 2017/2/17.
 * Desc: 图书操作相关逻辑
 */

public interface IBookPresenter {

    //通过ISBN获取图书详情
    void getBookDetailByISBN(String isbn,CallBack viewCallBack);
}
