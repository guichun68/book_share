package zyzx.linke.model.bean;

/**
 * Created by austin on 2017/3/18.
 * 向云图中插入数据时返回的json对应的bean
 */

public class AMapCreateItemResultVO {

    /**
     * info : OK
     * status : 1
     * _id : 283
     */

    private String info;
    private int status;
    private String _id;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
