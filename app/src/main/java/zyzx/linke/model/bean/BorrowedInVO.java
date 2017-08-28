package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Austin on 2017-08-24.
 * Desc: 借入的书籍 条目bean
 */

public class BorrowedInVO implements Parcelable{
    private String borrowFlowId;//借阅流程表主键
    private String flowId;//单一借阅流程id
    private String uid;//
    private String relUid;
    private String bookTitle;
    private String bookId;
    private String ownerName;// 图书所有者
    private String bookImage;
    private String bookAuthor;

    public BorrowedInVO(){}

    protected BorrowedInVO(Parcel in) {
        borrowFlowId = in.readString();
        flowId = in.readString();
        uid = in.readString();
        relUid = in.readString();
        bookTitle = in.readString();
        bookId = in.readString();
        ownerName = in.readString();
        bookImage = in.readString();
        bookAuthor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(borrowFlowId);
        dest.writeString(flowId);
        dest.writeString(uid);
        dest.writeString(relUid);
        dest.writeString(bookTitle);
        dest.writeString(bookId);
        dest.writeString(ownerName);
        dest.writeString(bookImage);
        dest.writeString(bookAuthor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BorrowedInVO> CREATOR = new Creator<BorrowedInVO>() {
        @Override
        public BorrowedInVO createFromParcel(Parcel in) {
            return new BorrowedInVO(in);
        }

        @Override
        public BorrowedInVO[] newArray(int size) {
            return new BorrowedInVO[size];
        }
    };

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
