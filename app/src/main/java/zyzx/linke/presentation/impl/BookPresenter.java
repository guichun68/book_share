package zyzx.linke.presentation.impl;

import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.BorrowedInVO;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.Page;
import zyzx.linke.model.bean.ResponseJson;
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
        getDataWithPost(new CallBack() {
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
                        },
                GlobalParams.urlISBNAPI+isbn,"访问出错",
                new String[]{},
                new Object[]{});
    }

    @Override
    public void addBook2MyLib(BookDetail2 mBook, Integer userId, CallBack viewCallBack) {
        getDataWithPost(viewCallBack,
                GlobalParams.urlAddBook2MyLib,"发生错误,请重试",
                new String[]{"book","bindName","userId"},
                JSON.toJSONString(mBook),mBook.getBinding(),userId);
    }

    @Override
    public void getUserBooks(String uid, final int pageNum,final CallBack viewCallBack) {
        getDataWithPost(new CallBack() {
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
                        },
                GlobalParams.urlGetUserBooks,"未能获取数据",
                new String[]{"uid","pageNum"},
                uid,pageNum);
    }


    @Override
    public void uploadBook(ArrayMap<String,Object> params, CallBack viewCallBack) {
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
                    DefindResponseJson drj = new DefindResponseJson(json);
                    if(DefindResponseJson.NO_DATA == drj.errorCode){
                        if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                        return;
                    }

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
    public void getMySharedBooks(String uid, int pageNum, final CallBack viewCallBack) {
        String url = GlobalParams.urlGetMyShareBooks.replace("{uid}",uid);
        url = url.replace("{pageSize}",String.valueOf(Const.PAGE_SIZE_MYBOOKS));
        url = url.replace("{curPage}",String.valueOf(pageNum));
        try {
            getModel().get(url, null, new CallBack() {
                @Override
                public void onSuccess(Object obj, int... code) {
                    String json = (String) obj;
                    DefindResponseJson drj = new DefindResponseJson(json);
                    if(DefindResponseJson.NO_DATA == drj.errorCode){
                        if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                        return;
                    }

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
        getDataWithPost(callBack,GlobalParams.urlDeleteUserBooks,"操作失败",new String[]{"uid","bid","userBookId"},
                userid,b_id,userBookId);
    }

    @Override
    public void cancelShare(String userBookId,CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCancelShare,"未能成功取消",new String[]{"userBookId"},userBookId);
    }

    @Override
    public void cancelShareAndDelBook(Integer userBookId, Integer mapId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlCancelShareAndDelBook,"操作失败",new String[]{"user_book_id","map_id"},userBookId,mapId);
    }

    @Override
    public void getMyBorrowedInBooks(Integer userid, int pageNum,final CallBack viewCallBack) {
        getDataWithPost( new CallBack() {
                             @Override
                             public void onSuccess(Object obj, int... code) {
                                 String json = (String) obj;
                                 DefindResponseJson rj = new DefindResponseJson(json);
                                 if(DefindResponseJson.NO_DATA == rj.errorCode){
                                     UIUtil.showToastSafe("未能获取数据");
                                     return;
                                 }
                                 List<BorrowedInVO> borrowedInVOs = new ArrayList<BorrowedInVO>();
                                 switch (rj.errorCode){
                                     case 2:
                                         borrowedInVOs = AppUtil.getBorrowedBooks(rj.data.getItems());
                                         if(viewCallBack!=null){
                                             viewCallBack.onSuccess(borrowedInVOs);
                                         }
                                         break;
                                     case 3:
                                         if(viewCallBack!=null){
                                             viewCallBack.onSuccess(borrowedInVOs);
                                         }
                                         break;
                                     default:
                                         if(viewCallBack!=null)viewCallBack.onFailure("未能获取数据");
                                         break;
                                 }
                             }

                             @Override
                             public void onFailure(Object obj, int... code) {
                                 if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                             }
                         },GlobalParams.urlGetMyBorrowedInBooks,"未能成功获取书籍信息",
                new String[]{"uid","pageNum"},
                userid,pageNum);
    }

    @Override
    public void checkUpdate(int currVersionCode, CallBack callBack, boolean forceUpdate) {
        getDataWithPost(callBack,GlobalParams.urlCheckUpdate,"检查更新失败",new String[]{"version_code"},currVersionCode);
    }

    @Override
    public void getBookClassify(CallBack callBack) {
        getModel().get(GlobalParams.urlGetBookClassify,null,callBack);
    }

    @Override
    public void getSwapBooks(int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSwapBooks,"未能获取数据",new String[]{"pageNum"},pageNum);
    }

    @Override
    public void getSwapBookInfo(String userBookId, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSwapBookInfo,"未能获取数据",new String[]{"userBookId"},userBookId);
    }

    @Override
    public void getSwapSkills(int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetSwapSkills,"未能获取数据",new String[]{"pageNo"},pageNum);
    }

    @Override
    public void getMySwapSkills(int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetMySwapSkills,"未能获取数据",new String[]{"pageNo"},pageNum);
    }

    @Override
    public void searchBooks(String keyword,int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSearchBooks,"未能获取数据",new String[]{"keyword","pageNo"},keyword,pageNum);
    }

    @Override
    public void searchSwapBooks(String keyword, int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSearchSwapBooks,"未能获取数据",new String[]{"keyword","pageNo"},keyword,pageNum);
    }

    @Override
    public void searchSwapSkills(String keyWord, int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSearchSwapSkills,"未能获取数据",new String[]{"keyword","pageNo"},keyWord,pageNum);
    }

    @Override
    public void searchSwapWantBooks(String keyWord, int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSearchSwapWantBooks,"未能获取数据",new String[]{"keyword","pageNo"},keyWord,pageNum);
    }

    @Override
    public void searchSwapWantSkills(String keyWord, int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlSearchSwapWantSkills,"未能获取数据",new String[]{"keyword","pageNo"},keyWord,pageNum);
    }

    @Override
    public void getAttentions(int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetAttentions,"未能拉取我的关注",new String[]{"pageNo"},pageNum);
    }

    @Override
    public void getMyBlackList(int pageNum, CallBack callBack) {
        getDataWithPost(callBack,GlobalParams.urlGetMyBlackList,"未能获取列表",new String[]{"pageNo"},pageNum);
    }

    private void getDataWithPost(CallBack callBack, String url, String failureDesc, String[] argNames, Object ...values){
        if(argNames.length != values.length){
            throw new RuntimeException("参数个数不匹配--自定义异常");
        }
        if(argNames.length!=0){
            ArrayMap<String,Object> param = new ArrayMap<>();
            for(int i=0;i<argNames.length;i++){
                if(values[i] instanceof String){
                    param.put(argNames[i],values[i]);
                }else if(values[i] instanceof Integer){
                    param.put(argNames[i],String.valueOf(values[i]));
                }
            }
            post(callBack,url,param,failureDesc);
        }else{
            post(callBack,url,null,failureDesc);
        }
    }

    private void post(CallBack callBack,String url,ArrayMap<String,Object> params,String failureDesc){
        try {
            getModel().post(url, params, callBack);
        }catch(Exception e){
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure(failureDesc);
            }
        }
    }


}
