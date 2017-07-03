package zyzx.linke.model.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;

/**
 * Created by austin on 2017/7/3.
 * Desc: 自定义sql返回来的标准json 解析
 */

public class DefindResponseJson {

    public Page data;
    public Integer errorCode;
    public String errorMsg;

    public DefindResponseJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        errorCode = jsonObject.getInteger("errorCode");
        errorMsg = jsonObject.getString("errorMsg");
        data = jsonObject.getObject("data",Page.class);
    }

    public Page getData() {
        return data;
    }

    public void setData(Page data) {
        this.data = data;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
