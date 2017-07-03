package zyzx.linke.model.bean;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by austin on 2017/6/27.
 */

public class ResponseJson {
    public JSONArray data;
    public Integer errorCode;
    public String errorMsg;

    public ResponseJson(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        errorCode = jsonObject.getInteger("errorCode");
        errorMsg = jsonObject.getString("errorMsg");
        data = jsonObject.getJSONArray("data");
    }
}
