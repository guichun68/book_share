package zyzx.linke.model.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by austin on 2017/6/30.
 */

public class UserInfoResult {

    private DataEntity data;
    private String errorCode;
    private String errorMsg;

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static class DataEntity {

        private int curPage;
        private int pageSize;
        private int startIndex;
        private int totalCount;
        private int totalPage;
        private List<ItemsEntity> items;

        public int getCurPage() {
            return curPage;
        }

        public void setCurPage(int curPage) {
            this.curPage = curPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<ItemsEntity> getItems() {
            return items;
        }

        public void setItems(List<ItemsEntity> items) {
            this.items = items;
        }

        public static class ItemsEntity {

            private long CREATE_DATE;
            private String department;
            private Integer diploma_id;
            private String soliloquy;
            private int credit_score;
            private int from_system;
            private String gender;
            private String head_icon;
            private String hobby;
            private String id;
            private String login_name;
            private String mobile_phone;
            private String password;
            private String signature;
            private String school;
            private int userid;
            private String address;
            private String email;
            private String real_name;
            private Integer city_id;
            private String bak4;
            private Date birthday;

            private String last_login_time;
            public Integer getCity_id() {
                return city_id;
            }

            public Integer getDiploma_id() {
                return diploma_id;
            }

            public String getSoliloquy() {
                return soliloquy;
            }

            public void setSoliloquy(String soliloquy) {
                this.soliloquy = soliloquy;
            }

            public void setDiploma_id(Integer diploma_id) {
                this.diploma_id = diploma_id;
            }

            public String getBak4() {
                return bak4;
            }

            public void setBak4(String bak4) {
                this.bak4 = bak4;
            }

            public String getLast_login_time() {
                return last_login_time;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public void setLast_login_time(String last_login_time) {
                this.last_login_time = last_login_time;
            }

            public void setCity_id(Integer city_id) {
                this.city_id = city_id;
            }

            public long getCREATE_DATE() {
                return CREATE_DATE;
            }

            public String getDepartment() {
                return department;
            }

            public void setDepartment(String department) {
                this.department = department;
            }

            public void setCREATE_DATE(long CREATE_DATE) {
                this.CREATE_DATE = CREATE_DATE;
            }

            public int getCredit_score() {
                return credit_score;
            }

            public void setCredit_score(int credit_score) {
                this.credit_score = credit_score;
            }

            public int getFrom_system() {
                return from_system;
            }

            public void setFrom_system(int from_system) {
                this.from_system = from_system;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getReal_name() {
                return real_name;
            }

            public void setReal_name(String real_name) {
                this.real_name = real_name;
            }

            public String getHead_icon() {
                return head_icon;
            }

            public void setHead_icon(String head_icon) {
                this.head_icon = head_icon;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLogin_name() {
                return login_name;
            }

            public void setLogin_name(String login_name) {
                this.login_name = login_name;
            }

            public String getMobile_phone() {
                return mobile_phone;
            }

            public Date getBirthday() {
                return birthday;
            }

            public void setBirthday(Date birthday) {
                this.birthday = birthday;
            }

            public void setMobile_phone(String mobile_phone) {
                this.mobile_phone = mobile_phone;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getUserid() {
                return userid;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getHobby() {
                return hobby;
            }

            public void setHobby(String hobby) {
                this.hobby = hobby;
            }

            public void setUserid(int userid) {
                this.userid = userid;
            }
        }
    }
}
