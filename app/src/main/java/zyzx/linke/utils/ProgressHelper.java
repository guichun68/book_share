package zyzx.linke.utils;



import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 进度回调辅助类
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 17:33
 */
public class ProgressHelper {
    /**
     * 包装OkHttpClient，用于下载文件的回调
     * @param client 待包装的OkHttpClient
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient，使用clone方法返回
     */
    public static OkHttpClient addProgressResponseListener(OkHttpClient client, final ProgressResponseBody.ProgressResponseListener progressListener){
        //TODO 新҉接҉口҉待҉实҉现҉,前提学习Rxjava 参考： http://www.jb51.net/article/104456.htm

        /*
        //克隆
        OkHttpClient clone = client.clone();

        //增加拦截器
        clone.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return clone;
         */
        return null;
    }


}