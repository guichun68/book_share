package zyzx.linke.model.bean;

import java.util.Date;

/**
 * Created by austin on 2017/7/18.
 */
public class BorrowFlow implements Comparable<BorrowFlow>{

    private String id;
    private String flowId;
    private Integer uid;
    private Integer relUid;
    private String bid;
    private String status;//当前状态，从Const类中取值
    private String msg;
    private Date createDate;

    public BorrowFlow(){}

    public BorrowFlow(String id,Date createDate){
        this.id = id;
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRelUid() {
        return relUid;
    }

    public void setRelUid(Integer relUid) {
        this.relUid = relUid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "BorrowFlow{" +
                "id='" + id + '\'' +
                ", flowId='" + flowId + '\'' +
                ", uid=" + uid +
                ", relUid=" + relUid +
                ", bid='" + bid + '\'' +
                ", status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", createDate=" + createDate +
                '}';
    }

    @Override
    public int compareTo(BorrowFlow o) {
        return o.getCreateDate() .compareTo(this.getCreateDate());
    }
}
