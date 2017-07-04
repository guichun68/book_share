package zyzx.linke.model;

import com.hyphenate.EMValueCallBack;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by austin on 2017/2/17.
 * Desc: 访问网络
 */

public interface IModel {
    void post(String url, HashMap<String,Object> param,CallBack callBack)throws IOException;
    void post(String url, HashMap<String,Object> param,EMValueCallBack callBack)throws IOException;

    void get(String url,HashMap<String,String> param,CallBack callBack);
    //上传文件及参数
    void sendMultipart(String url, HashMap<String, Object> param, final CallBack callBack);

    void uploadMultiFile(String url, HashMap<String,Object> param,  CallBack callBack) throws IOException;
}
