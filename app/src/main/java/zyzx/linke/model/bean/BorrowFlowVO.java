package zyzx.linke.model.bean;


/**
 * Created by austin on 2017/7/19.
 */
public class BorrowFlowVO {
    private BorrowFlow borrowFlow;
    private String bookName;
    private String relUserLoginName;
    private String relUid;

    public BorrowFlow getBorrowFlow() {
        return borrowFlow;
    }

    public void setBorrowFlow(BorrowFlow borrowFlow) {
        this.borrowFlow = borrowFlow;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getRelUserLoginName() {
        return relUserLoginName;
    }

    public void setRelUserLoginName(String relUserLoginName) {
        this.relUserLoginName = relUserLoginName;
    }

    public String getRelUid() {
        return relUid;
    }

    public void setRelUid(String relUid) {
        this.relUid = relUid;
    }
}
