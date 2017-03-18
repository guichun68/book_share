package zyzx.linke.presentation.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.constant.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.AMapQueryResult;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.QueryBookAroundMap;
import zyzx.linke.model.bean.RequestParamGetBookInfos;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.StringUtil;
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
                    if(response.toLowerCase().contains("</html>")){
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
                    BookDetail2 bookDetail = JSON.parseObject(response, BookDetail2.class);
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
    public void addBook2MyLib(BookDetail2 mBook, Integer userId, CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
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

    @Override
    public void addBook2Map(final BookDetail2 bookDetail, final Integer userid, boolean isSameBookAdded2Map, final double latitude, final double longitude, final CallBack viewCallBack) {
        // 首先查询该点用户是否已经分享过图书了
        final HashMap<String,Object> param = new HashMap<>();
        param.put("key", Const.key);
//        param.put("tableid",Const.mTableID);
//        param.put("keywords","");
        param.put("center",longitude+","+latitude);
//        param.put("radius","0");
//        param.put("filter","uid:"+userid+"");
        param.put("user_id",String.valueOf(userid));
        param.put("book_id",bookDetail.getB_id());
        param.put("user_name",GlobalParams.gUser.getLogin_name());
        param.put("head_url",GlobalParams.gUser.getHead_icon());

        try {
            GlobalParams.getgModel().post(GlobalParams.urlShareBook,param,viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null){
                viewCallBack.onFailure("访问服务器出错");
            }
        }
    }

    @Override
    public void getMapBookAllAround(double mLongti, double mLati, Integer around, final CallBack callBack) {
        HashMap<String,String> param = new HashMap<>();
        param.put("key",Const.key);
        param.put("tableid",Const.mTableID);
        param.put("center",mLongti+","+mLati);
        param.put("radius",GlobalParams.AROUND+"");

        GlobalParams.getgModel().get(GlobalParams.urlQueryBookFromMapAround, param, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                String json = (String) obj;
                UIUtil.showTestLog("zyzx",json);
                QueryBookAroundMap queriedBooks = JSON.parseObject(json, QueryBookAroundMap.class);
                if(callBack!=null){
                    callBack.onSuccess(queriedBooks);
                }
            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showTestLog("zyzx_failure",obj.toString());
            }
        });
    }

    @Override
    public void getUserBooks(String uid, final int pageNum,final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("uid",uid);
        param.put("pageNum",pageNum+"");

        try {
            GlobalParams.getgModel().post(GlobalParams.urlGetUserBooks, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    String jsonResult = (String) obj;
                    List<BookDetail2> bookDetails = JSONObject.parseArray(jsonResult, BookDetail2.class);
                    if(viewCallBack!=null){
                        viewCallBack.onSuccess(bookDetails);
                    }
                }

                @Override
                public void onFailure(Object obj) {
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
    public void getBookInfosByBookIds(List<RequestParamGetBookInfos> requestParamJson, final CallBack viewCallBack) {
        String json = JSON.toJSONString(requestParamJson);
        HashMap<String,Object> param = new HashMap<>();
        param.put("ids",json);
        try {
            GlobalParams.getgModel().post(GlobalParams.urlGetBooksByIds, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
                    if(obj.toString().toLowerCase().contains("</html>")){
                        UIUtil.showToastSafe(R.string.network_error);
                        onFailure(obj);
                    }else if(viewCallBack!=null){
                        viewCallBack.onSuccess(obj);
                    }
                }

                @Override
                public void onFailure(Object obj) {
                        UIUtil.showTestLog("zyzx","access book interface failure.");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            UIUtil.showToastSafe("网络错误，请重试");
        }
    }

    @Override
    public void uploadBook(HashMap<String,Object> params, CallBack viewCallBack) {
        try {
            GlobalParams.getgModel().post2(GlobalParams.urlUploadBook,params,viewCallBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null) {
                viewCallBack.onFailure("上传失败");
            }
        }
    }

    @Override
    public void getMyBooks(Integer userid, int pageNum, final CallBack viewCallBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_id",String.valueOf(userid));
        param.put("page_num",String.valueOf(pageNum));
        try {
            GlobalParams.getgModel().post(GlobalParams.urlGetMyBooks, param, new CallBack() {
                @Override
                public void onSuccess(Object obj) {
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
                public void onFailure(Object obj) {
                    if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            if(viewCallBack!=null)viewCallBack.onFailure("未能成功获取书籍信息");
        }
    }

    @Override
    public void deleteUserBook(Integer userid, String b_id,Integer mapItemId, CallBack callBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_id",String.valueOf(userid));
        param.put("book_id",b_id);
        param.put("map_id",String.valueOf(mapItemId));
        try {
            GlobalParams.getgModel().post(GlobalParams.urlDeleteUserBooks, param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelShare(Integer userBookId,Integer mapId,CallBack callBack) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("user_book_id",String.valueOf(userBookId));
        param.put("map_id",String.valueOf(mapId));
        try {
            GlobalParams.getgModel().post(GlobalParams.urlCancelShare,param,callBack);
        } catch (IOException e) {
            e.printStackTrace();
            if(callBack!=null){
                callBack.onFailure("未能成功取消");
            }
        }
    }
}
