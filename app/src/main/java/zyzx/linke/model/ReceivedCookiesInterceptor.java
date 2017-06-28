package zyzx.linke.model;

import android.app.Application;

import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Response;
import zyzx.linke.base.BaseApplication;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.utils.PreferenceManager;

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 * <p>
 * Created by tsuharesu on 4/1/15.
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (GlobalParams.BASE_URL.contains(originalResponse.request().url().host()) && !originalResponse.headers("Set-Cookie").isEmpty() && originalResponse.isSuccessful()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
              cookies.add(header);
            }
            PreferenceManager preUtil = PreferenceManager.getInstance();
            preUtil.saveCookie(cookies);
        }

        return originalResponse;
    }
}