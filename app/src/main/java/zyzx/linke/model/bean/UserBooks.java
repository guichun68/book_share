package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Austin on 2017/5/21.
 * Desc:
 */
public class UserBooks implements Parcelable {
    private String id;
    private String userId;
    private String bookId;
    private String bookStatusId;

    private String relatedUserId;
    private String shareMsg;
    private Integer shareType;
    private Integer shareAreaId;
    private Date createDate;

    public UserBooks(){}

    protected UserBooks(Parcel in) {
        id = in.readString();
        userId = in.readString();
        bookId = in.readString();
        bookStatusId = in.readString();

        relatedUserId = in.readString();
        shareMsg = in.readString();
        shareType = in.readInt();
        shareAreaId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(bookId);
        dest.writeString(bookStatusId);
        dest.writeString(relatedUserId);
        dest.writeString(shareMsg);
        dest.writeInt(shareType!=null?shareType:-1);
        dest.writeInt(shareAreaId!=null?shareAreaId:-1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserBooks> CREATOR = new Creator<UserBooks>() {
        @Override
        public UserBooks createFromParcel(Parcel in) {
            return new UserBooks(in);
        }

        @Override
        public UserBooks[] newArray(int size) {
            return new UserBooks[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookStatusId() {
        return bookStatusId;
    }

    public void setBookStatusId(String bookStatusId) {
        this.bookStatusId = bookStatusId;
    }

    public String getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(String relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getShareMsg() {
        return shareMsg;
    }

    public void setShareMsg(String shareMsg) {
        this.shareMsg = shareMsg;
    }

    public String getBookId() {
        return bookId;
    }

    public Integer getShareType() {
        return shareType;
    }

    public Integer getShareAreaId() {
        return shareAreaId;
    }

    public void setShareAreaId(Integer shareAreaId) {
        this.shareAreaId = shareAreaId;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

}
