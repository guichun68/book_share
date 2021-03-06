package zyzx.linke.presentation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.AMapQueryResult;
import zyzx.linke.model.bean.BookDetail;
import zyzx.linke.model.bean.QueryBookAroundMap;
import zyzx.linke.presentation.IBookPresenter;
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

    @Override
    public void addBook2Map(final BookDetail bookDetail, Integer userid, boolean isSameBookAdded2Map, final double latitude, final double longitude, final CallBack viewCallBack) {
        // 首先查询该点用户是否已经分享过图书了
        final HashMap<String,String> param = new HashMap<>();
        param.put("key",GlobalParams.key);
        param.put("mTableID",GlobalParams.mTableID);
        param.put("keywords","");
        param.put("center",longitude+","+latitude);
        param.put("radius","0");
        param.put("filter","uid:"+userid+"");
        GlobalParams.getgModel().get(GlobalParams.urlQueryBookFromMapAround, param, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                String json = (String) obj;
                AMapQueryResult resultBean = JSON.parseObject(json, AMapQueryResult.class);
                if(resultBean.getStatus()==0){
                        if(viewCallBack!=null){
                            viewCallBack.onFailure("地图访问出错");
                        }
                }
                else if(Integer.parseInt(resultBean.getCount())>0){
                    //该用户在该点分享过图书，进一步核实是否包含此书
                    List<AMapQueryResult.DatasEntity> datas = resultBean.getDatas();
                    for (AMapQueryResult.DatasEntity book:datas) {
                        if(book.getBookIds().contains(bookDetail.getB_id())){
                            //包含此书
                            if(viewCallBack!=null){
                                viewCallBack.onSuccess(400);
                            }
                        }else{
                            //不包含此书，可以继续在该坐标添加本次书籍
                            //1 首先得到之前的所有书籍的id
                            String newBookIds = resultBean.getDatas().get(0).getBookIds()+"#"+bookDetail.getB_id();
                            //2得到此次要更新的记录的id
                            String id = resultBean.getDatas().get(0).get_id();


                            HashMap<String,String> param2 = new HashMap<String, String>();
                            param2.put("key",GlobalParams.key);
                            param2.put("mTableID",GlobalParams.mTableID);
                            param2.put("data","{     \"_id\": \""+id+"\",   \"bookIds\":\""+newBookIds+"\" }");
                            try {
                                GlobalParams.getgModel().post(GlobalParams.urlGaodeBookUpdate, param2, new CallBack() {
                                    @Override
                                    public void onSuccess(Object obj) {
                                        String json = (String) obj;
                                        JSONObject jsonObject = JSON.parseObject(json);
                                        /**
                                         * {
                                         "info": "OK",
                                         "infocode": "10000",
                                         "status": 1,
                                         "_id": "4"
                                         }
                                         */
                                        int status = jsonObject.getInteger("status");
                                        if(status==1){
                                            if(viewCallBack!=null){
                                                viewCallBack.onSuccess(200);
                                            }
                                        }else{
                                            if(viewCallBack!=null){
                                                viewCallBack.onSuccess(500);
                                                UIUtil.showTestLog("zyzx","分享失败，插入高德云存储表单条数据失败，错误码:"+status);
                                            }
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
                    }
                    /*if(viewCallBack!=null){
                        viewCallBack.onSuccess(400);
                    }*/
                }else {
                    //该点该用户未曾放置过任何书籍，可以插入数据，调用高德api插入数据
                    HashMap<String,String> param2 = new HashMap<>();
                    param2.put("key",GlobalParams.key);
                    param2.put("mTableID",GlobalParams.mTableID);
                    param2.put("data","{     \"_location\": \""+longitude+","+latitude+"\",     \"_name\": \""+GlobalParams.gUser.getLogin_name()+"\",     \"book_image_url\": \""+ bookDetail.getImage()+"\",  \"bookIds\":\""+bookDetail.getB_id()+"\",   \"uid\": \""+GlobalParams.gUser.getUserid()+"\" }");
                    try {
                        GlobalParams.getgModel().post(GlobalParams.urlAddbook2Gaode, param2, new CallBack() {
                            @Override
                            public void onSuccess(Object obj) {
                                String json = (String) obj;
                                JSONObject jsonObject = JSON.parseObject(json);
                                /**
                                 * {
                                 "info": "OK",
                                 "infocode": "10000",
                                 "status": 1,
                                 "_id": "4"
                                 }
                                 */
                                int status = jsonObject.getInteger("status");
                                if(status==1){
                                    if(viewCallBack!=null){
                                        viewCallBack.onSuccess(200);
                                    }
                                }else{
                                    if(viewCallBack!=null){
                                        viewCallBack.onSuccess(500);
                                        UIUtil.showTestLog("zyzx","分享失败，插入高德云存储表单条数据失败，错误码:"+status);
                                    }
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


            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showTestLog("zyzx","添加书籍失败");
                    if(viewCallBack!=null){
                        viewCallBack.onFailure(obj);
                    }
            }
        });
    }

    @Override
    public void getMapBookAllAround(double mLongti, double mLati, Integer around, final CallBack callBack) {
        HashMap<String,String> param = new HashMap<>();
        param.put("key",GlobalParams.key);
        param.put("mTableID",GlobalParams.mTableID);
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
}
