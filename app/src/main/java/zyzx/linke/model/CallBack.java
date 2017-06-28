package zyzx.linke.model;

/**
 * Created by austin on 2017/2/17.
 * Desc: 网络访问回调
 */

public interface CallBack {
    void onSuccess(Object obj, int... code);
    void onFailure(Object obj, int... code);
}
