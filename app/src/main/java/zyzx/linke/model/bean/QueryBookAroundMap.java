package zyzx.linke.model.bean;

import java.util.List;

/**
 * Created by austin on 2017/3/2.
 * 查询指定中心点指定范围为半径内的图书的返回jsonBean（高德返回）
 */

public class QueryBookAroundMap {


    /**
     * count : 7
     * datas : [{"_address":"北京市东城区东华门街道故宫博物院","_city":"北京市","_createtime":"2017-03-02 00:57:11","_distance":"7035","_district":"东城区","_id":"12","_image":[],"_location":"116.401125,39.922503","_name":"故宫角楼故事","_province":"北京市","_updatetime":"2017-03-02 00:57:11","bookIds":"","book_image_url":"http://www.wbaidu.com","uid":1016},{"_address":"北京市东城区东华门街道故宫博物院","_city":"北京市","_createtime":"2017-03-01 18:14:17","_distance":"7035","_district":"东城区","_id":"3","_image":[{"_id":"58b6b8627bbf195ae8d2ae21","_preurl":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!thumb","_url":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!orig"},{"_id":"58b77fee7bbf195ae8dbfa36","_preurl":"http://img.yuntu.amap.com/58b77fee7bbf195ae8dbfa36@!thumb","_url":"http://img.yuntu.amap.com/58b77fee7bbf195ae8dbfa36@!orig"},{"_id":"58b77ff67bbf195ae8dbfa7f","_preurl":"http://img.yuntu.amap.com/58b77ff67bbf195ae8dbfa7f@!thumb","_url":"http://img.yuntu.amap.com/58b77ff67bbf195ae8dbfa7f@!orig"}],"_location":"116.401125,39.922503","_name":"故宫角楼故事","_province":"北京市","_updatetime":"2017-03-01 18:14:17","bookIds":"","book_image_url":"http://www.wbaidu.com","uid":1016}]
     * info : OK
     * infocode : 10000
     * status : 1
     */

    private String count;
    private String info;
    private String infocode;
    private int status;
    private List<DatasEntity> datas;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public static class DatasEntity {
        /**
         * _address : 北京市东城区东华门街道故宫博物院
         * _city : 北京市
         * _createtime : 2017-03-02 00:57:11
         * _distance : 7035
         * _district : 东城区
         * _id : 12
         * _image : []
         * _location : 116.401125,39.922503
         * _name : 故宫角楼故事
         * _province : 北京市
         * _updatetime : 2017-03-02 00:57:11
         * bookIds :
         * book_image_url : http://www.wbaidu.com
         * uid : 1016
         */

        private String _address;
        private String _city;
        private String _createtime;
        private String _distance;
        private String _district;
        private String _id;
        private String _location;
        private String _name;
        private String _province;
        private String _updatetime;
        private String bookIds;
        private String book_image_url;
        private int uid;
        private List<?> _image;

        public String get_address() {
            return _address;
        }

        public void set_address(String _address) {
            this._address = _address;
        }

        public String get_city() {
            return _city;
        }

        public void set_city(String _city) {
            this._city = _city;
        }

        public String get_createtime() {
            return _createtime;
        }

        public void set_createtime(String _createtime) {
            this._createtime = _createtime;
        }

        public String get_distance() {
            return _distance;
        }

        public void set_distance(String _distance) {
            this._distance = _distance;
        }

        public String get_district() {
            return _district;
        }

        public void set_district(String _district) {
            this._district = _district;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String get_location() {
            return _location;
        }

        public void set_location(String _location) {
            this._location = _location;
        }

        public String get_name() {
            return _name;
        }

        public void set_name(String _name) {
            this._name = _name;
        }

        public String get_province() {
            return _province;
        }

        public void set_province(String _province) {
            this._province = _province;
        }

        public String get_updatetime() {
            return _updatetime;
        }

        public void set_updatetime(String _updatetime) {
            this._updatetime = _updatetime;
        }

        public String getBookIds() {
            return bookIds;
        }

        public void setBookIds(String bookIds) {
            this.bookIds = bookIds;
        }

        public String getBook_image_url() {
            return book_image_url;
        }

        public void setBook_image_url(String book_image_url) {
            this.book_image_url = book_image_url;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public List<?> get_image() {
            return _image;
        }

        public void set_image(List<?> _image) {
            this._image = _image;
        }
    }
}
