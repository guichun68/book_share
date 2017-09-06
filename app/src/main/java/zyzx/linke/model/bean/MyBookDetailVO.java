package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import zyzx.linke.model.Area;
import zyzx.linke.utils.StringUtil;

/**
 * 我登记的书籍
 * @author Austin
 *
 */
public class MyBookDetailVO implements Parcelable{
	private BookDetail2 book;

	private String uid;//
	private String bookStatusId;//书籍当前状态,从常量表取值（已记录到常量表）
	private String userBookId;//在zyzx_usr_books表记录的id(主键)
	private String relatedUserId;//关联到的用户，如图书状态为借入，则此处为从谁借入；如为借出，则表示借出给谁
	private Integer shareAreaId;//分享者所在城市
	private String shareMsg;//分享留言
	private Integer shareType;//分享类型 （user_book表字段）
	private String swapId;
	private String relLoginName;

	public void setUserBook(UserBooks ub){
		if(ub != null){
			if(!StringUtil.isEmpty(ub.getUserId()))
				this.uid = ub.getUserId();
			if(!StringUtil.isEmpty(ub.getBookStatusId()))
				this.bookStatusId = ub.getBookStatusId();
			if(!StringUtil.isEmpty(ub.getBookId()))
				this.userBookId = ub.getBookId();
			if(!StringUtil.isEmpty(ub.getRelatedUserId()))
				this.relatedUserId = ub.getRelatedUserId();
			if(ub.getShareAreaId() != null)
				this.shareAreaId = ub.getShareAreaId();
			if(!StringUtil.isEmpty(ub.getShareMsg()))
				this.shareMsg = ub.getShareMsg();
			if(ub.getShareType() != null){
				this.shareType = ub.getShareType();
			}
		}
	}

	public MyBookDetailVO(){}

	protected MyBookDetailVO(Parcel in) {
		book = in.readParcelable(BookDetail2.class.getClassLoader());
		bookStatusId = in.readString();
		userBookId = in.readString();
		relatedUserId = in.readString();
		shareAreaId = in.readInt();
		shareMsg = in.readString();
		shareType = in.readInt();
		uid = in.readString();
		swapId = in.readString();
		relLoginName = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(book, flags);
		dest.writeString(bookStatusId);
		dest.writeString(userBookId);
		dest.writeString(relatedUserId);
		dest.writeInt(shareAreaId==null?-1:shareAreaId);
		dest.writeString(shareMsg);
		dest.writeInt(shareType==null?-1:shareType);
		dest.writeString(uid);
		dest.writeString(swapId);
		dest.writeString(relLoginName);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<MyBookDetailVO> CREATOR = new Creator<MyBookDetailVO>() {
		@Override
		public MyBookDetailVO createFromParcel(Parcel in) {
			return new MyBookDetailVO(in);
		}

		@Override
		public MyBookDetailVO[] newArray(int size) {
			return new MyBookDetailVO[size];
		}
	};

	public BookDetail2 getBook() {
		return book;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setBook(BookDetail2 book) {
		this.book = book;
	}

	public String getBookStatusId() {
		return bookStatusId;
	}

	public void setBookStatusId(String bookStatusId) {
		this.bookStatusId = bookStatusId;
	}

	public String getUserBookId() {
		return userBookId;
	}

	public void setUserBookId(String userBookId) {
		this.userBookId = userBookId;
	}

	public String getRelatedUserId() {
		return relatedUserId;
	}

	public void setRelatedUserId(String relatedUserId) {
		this.relatedUserId = relatedUserId;
	}

	public Integer getShareAreaId() {
		return shareAreaId;
	}

	public void setShareAreaId(Integer shareAreaId) {
		this.shareAreaId = shareAreaId;
	}

	public String getShareMsg() {
		return shareMsg;
	}

	public void setShareMsg(String shareMsg) {
		this.shareMsg = shareMsg;
	}

	public Integer getShareType() {
		return shareType;
	}

	public void setShareType(Integer shareType) {
		this.shareType = shareType;
	}

	public String getSwapId() {
		return swapId;
	}

	public void setSwapId(String swapId) {
		this.swapId = swapId;
	}

	public String getRelLoginName() {
		return relLoginName;
	}

	public void setRelLoginName(String relLoginName) {
		this.relLoginName = relLoginName;
	}
}
