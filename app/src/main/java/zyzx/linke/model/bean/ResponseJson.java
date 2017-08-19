package zyzx.linke.model.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by austin on 2017/6/27.
 */

public class ResponseJson {
    public JSONArray data;
    public Integer errorCode;
    public String errorMsg;
    public static final int NO_DATA = -1;

    public ResponseJson(String json){
        if(TextUtils.isEmpty(json)){
            errorCode = NO_DATA;
            return;
        }
        JSONObject jsonObject = JSON.parseObject(json);
        errorCode = jsonObject.getInteger("errorCode");
        errorMsg = jsonObject.getString("errorMsg");
        data = jsonObject.getJSONArray("data");
    }
}
