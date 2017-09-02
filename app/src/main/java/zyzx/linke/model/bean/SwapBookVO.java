package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by austin on 2017/8/15.
 * Desc: 图书交换 列表
 */
public class SwapBookVO implements Parcelable{
    private String userBookId;
    private String userId;//uuid
    private String bookTitle;//交换标题
    private String bookImageLarge;//拥有的图书封面
    private String swapId;
    private String swapBookTitle;//要交换的书的封面
    private String swapBookAuthor;//要交换书的作者
    private String swapMsg;//交换留言
    private String bookAuthor;//拥有的书的作者
    private String swapperName;//交换人的用户名

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
        swapperName = in.readString();
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
        dest.writeString(swapperName);
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

    public String getSwapperName() {
        return swapperName;
    }

    public void setSwapperName(String swapperName) {
        this.swapperName = swapperName;
    }
}
