package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.Page;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 用户逻辑实现类
 */

public class BookPresenter extends IBookPresenter {

    
    public void getBookDetailByISBN(String isbn, final CallBack viewCallBack) {

        try {
            getModel().post(GlobalParams.urlISBNAPI+isbn,null, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
                    String response = (String)obj;
                    if(response.toLowerCase().contains("</html>")){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("服务器错误，请检查URL");
                        }
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(response);
                    Integer code2 = jsonObject.getInteger("code");
                    if(code2 != null){
                        viewCallBack.onFailure("未找到相关书籍信息");
                        UIUtil.showTestLog("zyzx",jsonObject.getString("msg"));
                        return;
                    }
                    Date pubdate = StringUtil.getDate(jsonObject.getString("pubdate"));
                    Integer pages = StringUtil.getNumFromStr(jsonObject.getString("pages"));
                    BookDetail2 bookDetail = JSON.parseObject(response, BookDetail2.class);
                    if(pubdate!=null){
                        bookDetail.setPubdateDateType(pubdate);
                    }
                    if(pages !=null){
                        bookDetail.setPages(String.valueOf(pages));
                    }
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(bookDetail);
                    }
                }

                @Override
                public void onFailure(Object obj, int... code) {
                    viewCallBack.onFailure("未找到相关书籍信息");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBook2MyLib(BookDetail2 mBook, Integer userId, CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("book",JSON.toJSONString(mBook));
        param.put("bindName",mBook.getBinding());
        param.put("userId",userId+"");
        try {
            getModel().post(GlobalParams.urlAddBook2MyLib, param, viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null){
                viewCallBack.onFailure("发生错误,请重试");
            }
        }
    }

    @Override
    public void getUserBooks(String uid, final int pageNum,final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("uid",uid);
        param.put("pageNum",pageNum+"");

        try {
            getModel().post(GlobalParams.urlGetUserBooks, param, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
                    String jsonResult = (String) obj;
                    List<BookDetail2> bookDetails = JSONObject.parseArray(jsonResult, BookDetail2.class);
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(bookDetails);
                    }
                }

                @Override
                public void onFailure(Object obj, int... code) {
                    if(viewCallBack!=null){
                        viewCallBack.onFailure(obj);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void uploadBook(HashMap<String,Object> params, CallBack viewCallBack) {
        try {
            getModel().uploadMultiFile(GlobalParams.urlAddManualBook2Lib,params,viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null) {
                viewCallBack.onFailure("上传失败");
            }
        }
    }

    @Override
    public void getMyBooks(String uid, int pageNum, final CallBack viewCallBack) {
        String url = GlobalParams.urlGetMyBooks.replace("{uid}",uid);
        url = url.replace("{pageSize}",String.valueOf(Const.PAGE_SIZE_MYBOOKS));
        url = url.replace("{curPage}",String.valueOf(pageNum));
        try {
            getModel().get(url, null, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
                    String json = (String) obj;
                    if(StringUtil.isEmpty(json)){
                        if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                        return;
                    }
                    DefindResponseJson drj = new DefindResponseJson(json);
                    if(drj.errorCode!=null) {
                        if(drj.errorCode!=1 && viewCallBack!=null){
                                viewCallBack.onFailure(drj.errorMsg);
                        }
                        if(drj.errorCode==1){//成功获取
                            Page page = drj.data;
                            List<JSONObject> items = page.getItems();
                            ArrayList<MyBookDetailVO> myBooks = AppUtil.getBookDetailVOs(items);
                            if(viewCallBack!=null)viewCallBack.onSuccess(myBooks);
                        }
                    }
                }

                @Override
                public void onFailure(Object obj, int... code) {
                    if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
        }
    }

    @Override
    public void deleteUserBook(String userid, String userBookId,String b_id, CallBack callBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("uid",userid);
        param.put("bid",b_id);
        param.put("userBookId",userBookId);
        try {
            getModel().post(GlobalParams.urlDeleteUserBooks, param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelShare(String userBookId,CallBack callBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("userBookId",userBookId);
        try {
            getModel().post(GlobalParams.urlCancelShare,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("未能成功取消");
            }
        }
    }

    @Override
    public void cancelShareAndDelBook(Integer userBookId, Integer mapId, CallBack callBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_book_id",String.valueOf(userBookId));
        param.put("map_id",String.valueOf(mapId));
        try {
            getModel().post(GlobalParams.urlCancelShareAndDelBook,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("操作失败");
            }
        }

    }

    @Override
    public void getMyBorrowedInBooks(Integer userid, int pageNum,final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_id",String.valueOf(userid));
        param.put("page_num",String.valueOf(pageNum));
        try {
            getModel().post(GlobalParams.urlGetMyBorrowedInBooks, param, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
                    String json = (String) obj;
                    if(StringUtil.isEmpty(json)){
                        if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                        return;
                    }
                    List<MyBookDetailVO> myBookDetailVOs = JSON.parseArray(json, MyBookDetailVO.class);
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(myBookDetailVOs);
                    }
                }

                @Override
                public void onFailure(Object obj, int... code) {
                    if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
        }
    }

    @Override
    public void checkUpdate(int currVersionCode, CallBack callBack, boolean forceUpdate) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("version_code",String.valueOf(currVersionCode));
        try {
            getModel().post(GlobalParams.urlCheckUpdate,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("检查更新失败");
            }
        }
    }

    @Override
    public void getBookClassify(CallBack callBack) {
        getModel().get(GlobalParams.urlGetBookClassify,null,callBack);
    }
}
