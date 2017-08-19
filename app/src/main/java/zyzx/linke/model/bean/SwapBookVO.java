package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by austin on 2017/8/15.
 * Desc: 图书交换 列表
 */
public class SwapBookVO implements Parcelable{
    private String userBookId;
    private String userId;
    private String bookTitle;
    private String bookImageLarge;
    private String swapId;
    private String swapBookTitle;
    private String swapBookAuthor;
    private String swapMsg;
    private String bookAuthor;

    public SwapBookVO(){

    }

    protected SwapBookVO(Parcel in) {
        userId = in.readString();
        bookTitle = in.readString();
        bookImageLarge = in.readString();
        swapId = in.readString();
        swapBookTitle = in.readString();
        swapBookAuthor = in.readString();
        swapMsg = in.readString();
        bookAuthor = in.readString();
        userBookId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(bookTitle);
        dest.writeString(bookImageLarge);
        dest.writeString(swapId);
        dest.writeString(swapBookTitle);
        dest.writeString(swapBookAuthor);
        dest.writeString(swapMsg);
        dest.writeString(bookAuthor);
        dest.writeString(userBookId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SwapBookVO> CREATOR = new Creator<SwapBookVO>() {
        @Override
        public SwapBookVO createFromParcel(Parcel in) {
            return new SwapBookVO(in);
        }

        @Override
        public SwapBookVO[] newArray(int size) {
            return new SwapBookVO[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookImageLarge() {
        return bookImageLarge;
    }

    public void setBookImageLarge(String bookImageLarge) {
        this.bookImageLarge = bookImageLarge;
    }

    public String getSwapId() {
        return swapId;
    }

    public void setSwapId(String swapId) {
        this.swapId = swapId;
    }

    public String getSwapBookTitle() {
        return swapBookTitle;
    }

    public void setSwapBookTitle(String swapBookTitle) {
        this.swapBookTitle = swapBookTitle;
    }

    public String getSwapBookAuthor() {
        return swapBookAuthor;
    }

    public void setSwapBookAuthor(String swapBookAuthor) {
        this.swapBookAuthor = swapBookAuthor;
    }

    public String getSwapMsg() {
        return swapMsg;
    }

    public void setSwapMsg(String swapMsg) {
        this.swapMsg = swapMsg;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getUserBookId() {
        return userBookId;
    }

    public void setUserBookId(String userBookId) {
        this.userBookId = userBookId;
    }
}
