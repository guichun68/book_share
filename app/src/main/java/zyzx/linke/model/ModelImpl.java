package zyzx.linke.model;

import android.util.Log;

import com.hyphenate.EMValueCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 访问网络(阻塞式访问，需开线程)
 */

public class ModelImpl implements IModel {

    private OkHttpClient mClient = new OkHttpClient();

/*
    @Override
    public void post(String url, HashMap<String, Object> param, final CallBack callBack) throws IOException {
        FormEncodingBuilder fb = new FormEncodingBuilder();
        Request request;
        if(param != null){
            for (Map.Entry<String, Object> et : param.entrySet()) {
                String value = "";
                if(et.getValue() instanceof Integer){
                    value= et.getValue()+"";
                }else if(et.getValue() instanceof Boolean){
                    value= ((Boolean)et.getValue()).toString();
                }else if(et.getValue() instanceof String){
                    value=(String)et.getValue();
                }
                fb.add(et.getKey(),value);
            }
            RequestBody body = fb.build();

            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                .url(url)
                .build();
        }

        mClient.setConnectTimeout(10, TimeUnit.SECONDS);
        mClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mClient.setReadTimeout(30, TimeUnit.SECONDS);
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                UIUtil.showTestLog("zyzx", "access Internet error,error msg as follows:"+e.getMessage());
                callBack.onFailure("Error accessing network!");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = new String(response.body().string());
                if(res.toLowerCase().contains("</html>")||res.toLowerCase().contains("<html>")){
                    callBack.onFailure(res);
                    UIUtil.showToastSafe("网络或服务器故障，请检查");
                    return;
                }
                callBack.onSuccess(res);
            }
        });
        */
/*if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }*//*

    }
*/


    @Override
    public void post(String url, HashMap<String, Object> param, final EMValueCallBack callBack) throws IOException {
        FormBody.Builder fb = new FormBody.Builder();
        Request request;
        if(param != null){
            for (Map.Entry<String, Object> et : param.entrySet()) {
                fb.add(et.getKey(), (String)et.getValue());
            }
            RequestBody body = fb.build();

            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .build();
        }
        mClient = new OkHttpClient.Builder()
                .connectTimeout(10,TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtil.showTestLog("zyzx", "access Internet error,error msg as follows:");
                callBack.onError(500,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = new String(response.body().string());
                if(res.toLowerCase().contains("</html>")||res.toLowerCase().contains("<html>")){
                    callBack.onError(500,"网络或服务器故障，请检查");
                    UIUtil.showToastSafe("网络或服务器故障，请检查");
                    return;
                }
                callBack.onSuccess(res);
            }

        });
    }

    @Override
    public void get(String url, HashMap<String, String> param, final CallBack callBack) {

        Request.Builder builder = new Request.Builder();
        StringBuilder sb = new StringBuilder("?");
        for (Map.Entry<String,String> entry:param.entrySet()) {
           sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if(sb.length()>0)
        {
            sb.deleteCharAt(sb.length()-1);
        }
        final Request request = builder
                .url(url+sb.toString())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtil.showTestLog("zyzx","访问网络出错！");
                if(callBack!=null){
                    callBack.onFailure(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJson;
                //NOT UI Thread
                if(response.isSuccessful()){
                    responseJson= new String(response.body().string());
                    if(callBack!=null){
                        callBack.onSuccess(responseJson);
                    }
                }else{
                    if(callBack!=null){
                        callBack.onFailure(response.body().string());
                    }
                }
            }
        });
    }

/*
    @Override
    public void post2(String url, HashMap<String, Object> param, final CallBack callBack) throws IOException {
        //补全请求地址
        MultipartBuilder builder = new MultipartBuilder();
//        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.type(MediaType.parse("multipart/form-data"));
//        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : param.keySet()) {
            Object object = param.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            }
        }
        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(url).post(body).build();
        //单独设置参数 比如读取超时时间
        mClient.setWriteTimeout(50, TimeUnit.SECONDS);
        Call call = mClient.newCall(request);
//        final Call call = mClient.build(newCall(request)
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("zyzx", e.toString());
                if(callBack!=null){
                    callBack.onFailure("上传失败");
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    UIUtil.showTestLog("zyzx", "response ----->" + string);
                    if(callBack!=null){
                        callBack.onSuccess(string);
                    }
                } else {
                    if(callBack!=null){
                        callBack.onFailure("上传失败");
                    }
                }
            }
        });

    }
*/


    /**
     * 上传文件及参数
     */
    public void sendMultipart(String url, HashMap<String, Object> param, final CallBack callBack){
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File imageFile = null;
        MultipartBody.Builder mbody=new MultipartBody.Builder().setType(MultipartBody.FORM);
        //追加参数
        for (String key : param.keySet()) {
            Object object = param.get(key);
            if (!(object instanceof File)) {
                mbody.addFormDataPart(key, object.toString());
            } else {
                imageFile = (File) object;
                mbody.addFormDataPart(key,imageFile.getName(),RequestBody.create(MEDIA_TYPE_PNG,imageFile));
            }
        }
        //设置超时时间及缓存
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor())
                .addInterceptor(new AddCookiesInterceptor());

        OkHttpClient mOkHttpClient=builder.build();

        RequestBody requestBody =mbody.build();
        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + "...")
                .url(url)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error",e!=null?e.getMessage()+"":"An Error has occurred");
                if(callBack!=null){
                    callBack.onFailure(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    UIUtil.showTestLog("zyzx", "response ----->" + string);
                    if(callBack!=null){
                        callBack.onSuccess(string);
                    }
                } else {
                    if(callBack!=null){
                        callBack.onFailure("上传失败");
                    }
                }
            }
        });
    }


    public void post(String url, HashMap<String,Object> param, final CallBack callBack) {

        mClient = new OkHttpClient.Builder()
                .addInterceptor(new ReceivedCookiesInterceptor())
                .addInterceptor(new AddCookiesInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, Object> et : param.entrySet()) {
            builder.add(et.getKey(), (String)et.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error",e!=null?e.getMessage()+"":"An Error has occurred");
                if(callBack!=null){
                    callBack.onFailure(e.getMessage());
                }
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                final String res = new String(response.body().string());
                if(res.toLowerCase().contains("</html>")||res.toLowerCase().contains("<html>")){
                    callBack.onFailure(res);
                    Log.e("error","网络或服务器故障，请检查");
                    return;
                }
                if(callBack!=null){
                    callBack.onSuccess(res);
                }
            }

        });
    }
}
