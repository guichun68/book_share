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

        for (Map.Entry<String, String> et : param.entrySet()) {
            fb.add(et.getKey(), et.getValue());
        }
        RequestBody body = fb.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                UIUtil.showTestLog("zyzx", "access Internet error,error msg as follows:");
                callBack.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callBack.onSuccess(response.body().string());
            }
        });
        /*if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }*/
    }

}
