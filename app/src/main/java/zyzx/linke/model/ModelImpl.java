package zyzx.linke.model;

import android.util.Log;

import com.hyphenate.EMValueCallBack;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/17.
 * Desc: 访问网络(阻塞式访问，需开线程)
 */

public class ModelImpl implements IModel {

    private OkHttpClient client = new OkHttpClient();

    @Override
    public void post(String url, HashMap<String, Object> param, final CallBack callBack) throws IOException {
        FormEncodingBuilder fb = new FormEncodingBuilder();
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

        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                UIUtil.showTestLog("zyzx", "access Internet error,error msg as follows:"+e.getMessage());
                callBack.onFailure("Error accessing network!");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = new String(response.body().string());
                if(res.toLowerCase().contains("</html>")){
                    callBack.onFailure(res);
                    UIUtil.showToastSafe("网络或服务器故障，请检查");
                    return;
                }
                callBack.onSuccess(res);
            }
        });
        /*if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }*/
    }

    @Override
    public void post(String url, HashMap<String, Object> param, final EMValueCallBack callBack) throws IOException {
        FormEncodingBuilder fb = new FormEncodingBuilder();
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

        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                UIUtil.showTestLog("zyzx", "access Internet error,error msg as follows:");
                callBack.onError(500,e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = new String(response.body().string());
                if(res.toLowerCase().contains("</html>")){
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                UIUtil.showTestLog("zyzx","访问网络出错！");
                if(callBack!=null){
                    callBack.onFailure(e.getMessage());
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
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
        client.setWriteTimeout(50, TimeUnit.SECONDS);
        Call call = client.newCall(request);
//        final Call call = client.build(newCall(request)
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

}
