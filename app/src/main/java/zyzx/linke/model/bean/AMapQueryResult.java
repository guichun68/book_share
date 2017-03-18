package zyzx.linke.model.bean;


import java.util.List;

/**
 * Created by austin on 2017/3/1.
 * Desc: 高德地图周边检索结果JSON
 * 在指定tableid的数据表内，搜索指定中心点和半径范围内，符合筛选条件的位置数据。
 * 具体字段含义参照@see <a href="http://google.com">http://lbs.amap.com/yuntu/reference/cloudsearch</a>中"周边检索部分内容
 */

public class AMapQueryResult {

    /**
     * count : 1
     * info : OK
     * infocode : 10000
     * status : 1
     * datas : [{"_id":"3","_location":"116.401125,39.922503","_name":"故宫角楼故事","_address":"北京市东城区东华门街道故宫博物院","uid":1016,"bookIds":"","book_image_url":"http://www.wbaidu.com","_createtime":"2017-03-01 18:14:17","_updatetime":"2017-03-01 18:14:17","_province":"北京市","_city":"北京市","_district":"东城区","_distance":"0","_image":[{"_id":"58b6b8627bbf195ae8d2ae21","_preurl":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!thumb","_url":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!orig"}]}]
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
         * _id : 3
         * _location : 116.401125,39.922503
         * _name : 故宫角楼故事
         * _address : 北京市东城区东华门街道故宫博物院
         * uid : 1016
         * bookIds :
         * book_image_url : http://www.wbaidu.com
         * head_icon_url:http://www.baid.cn/dsja92039091212.jpg
         * _createtime : 2017-03-01 18:14:17
         * _updatetime : 2017-03-01 18:14:17
         * _province : 北京市
         * _city : 北京市
         * _district : 东城区
         * _distance : 0
         * _image : [{"_id":"58b6b8627bbf195ae8d2ae21","_preurl":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!thumb","_url":"http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!orig"}]
         */

        private String _id;
        private String _location;
        private String _name;
        private String _address;
        private int uid;
        private String bookIds;
        private String book_image_url;
        private String head_icon_url;
        private String _createtime;
        private String _updatetime;
        private String _province;
        private String _city;
        private String _district;
        private String _distance;
        private List<ImageEntity> _image;

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

        public String get_address() {
            return _address;
        }

        public void set_address(String _address) {
            this._address = _address;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
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


        public String getHead_icon_url() {
            return head_icon_url;
        }

        public void setHead_icon_url(String head_icon_url) {
            this.head_icon_url = head_icon_url;
        }

        public String get_createtime() {
            return _createtime;
        }

        public void set_createtime(String _createtime) {
            this._createtime = _createtime;
        }

        public String get_updatetime() {
            return _updatetime;
        }

        public void set_updatetime(String _updatetime) {
            this._updatetime = _updatetime;
        }

        public String get_province() {
            return _province;
        }

        public void set_province(String _province) {
            this._province = _province;
        }

        public String get_city() {
            return _city;
        }

        public void set_city(String _city) {
            this._city = _city;
        }

        public String get_district() {
            return _district;
        }

        public void set_district(String _district) {
            this._district = _district;
        }

        public String get_distance() {
            return _distance;
        }

        public void set_distance(String _distance) {
            this._distance = _distance;
        }

        public List<ImageEntity> get_image() {
            return _image;
        }

        public void set_image(List<ImageEntity> _image) {
            this._image = _image;
        }

        public static class ImageEntity {
            /**
             * _id : 58b6b8627bbf195ae8d2ae21
             * _preurl : http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!thumb
             * _url : http://img.yuntu.amap.com/58b6b8627bbf195ae8d2ae21@!orig
             */

            private String _id;
            private String _preurl;
            private String _url;

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public String get_preurl() {
                return _preurl;
            }

            public void set_preurl(String _preurl) {
                this._preurl = _preurl;
            }

            public String get_url() {
                return _url;
            }

            public void set_url(String _url) {
                this._url = _url;
            }
        }
    }
}
