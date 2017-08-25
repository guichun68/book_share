package zyzx.linke.model.bean;

/**
 * Created by Austin on 2017-08-24.
 * Desc: 借入的书籍 条目bean
 */

public class BorrowedInVO {
    private String borrowFlowId;//借阅流程表主键
    private String flowId;//单一借阅流程id
    private String uid;//
    private String relUid;
    private String bookTitle;
    private String bookId;
    private String ownerName;// 图书所有者
    private String bookImage;
    private String bookAuthor;

    public String getBorrowFlowId() {
        return borrowFlowId;
    }

    public void setBorrowFlowId(String borrowFlowId) {
        this.borrowFlowId = borrowFlowId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRelUid() {
        return relUid;
    }

    public void setRelUid(String relUid) {
        this.relUid = relUid;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
}
