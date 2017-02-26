package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.BookDetail;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.BeanFactoryUtil;
import zyzx.linke.utils.GlobalParams;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class BookPresenter implements IBookPresenter {


    @Override
    public void getBookDetailByISBN(String isbn, final CallBack viewCallBack) {

        try {
            GlobalParams.getgModel().post(GlobalParams.urlISBNAPI+isbn,null, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String response = (String)obj;
                    if(response.contains("<html>")){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("服务器错误，请检查URL");
                        }
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(response);
                    Integer code = jsonObject.getInteger("code");
                    if(code != null){
                        viewCallBack.onFailure("未找到相关书籍信息");
                        UIUtil.showTestLog("zyzx",jsonObject.getString("msg"));
                        return;
                    }
                    BookDetail bookDetail = JSON.parseObject(response, BookDetail.class);
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(bookDetail);
                    }
                }

                @Override
                public void onFailure(Object obj) {
                    viewCallBack.onFailure("未找到相关书籍信息");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBook2MyLib(BookDetail mBook,Integer userId,CallBack viewCallBack) {
        HashMap<String,String> param = new HashMap<>();
        param.put("book",JSON.toJSONString(mBook));
        param.put("userId",userId+"");
        try {
            GlobalParams.getgModel().post(GlobalParams.urlAddBook2MyLib, param, viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null){
                viewCallBack.onFailure("发生错误,请重试");
            }
        }
    }
}
