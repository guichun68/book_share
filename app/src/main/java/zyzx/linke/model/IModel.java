package zyzx.linke.model;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by austin on 2017/2/17.
 * Desc: 访问网络
 */

public interface IModel {
    void post(String url, HashMap<String,String> param,CallBack callBack)throws IOException;
    void get(String url,HashMap<String,String> param,CallBack callBack);
}
