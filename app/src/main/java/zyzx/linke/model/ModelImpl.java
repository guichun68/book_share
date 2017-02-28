package zyzx.linke.model;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
    public void post(String url, HashMap<String, String> param, final CallBack callBack) throws IOException {
        FormEncodingBuilder fb = new FormEncodingBuilder();
        Request request;
        if(param != null){
            for (Map.Entry<String, String> et : param.entrySet()) {
                fb.add(et.getKey(), et.getValue());
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
                callBack.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = new String(response.body().string());
                if(res.toLowerCase().contains("<html>")){
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

}
