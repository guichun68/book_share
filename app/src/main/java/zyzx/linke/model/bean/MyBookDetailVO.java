package zyzx.linke.model.bean;

import java.io.Serializable;

/**
 * 我登记的书籍
 * @author Austin
 *
 */
public class MyBookDetailVO implements Serializable{

	private BookDetail2 book;

	private String bookStatusId;//书籍当前状态,从常量表取值（已记录到常量表）
	private String userBookId;//在zyzx_usr_books表记录的id(主键)
	private String relatedUserId;//关联到的用户，如图书状态为借入，则此处为从谁借入；如为借出，则表示借出给谁

	public BookDetail2 getBook() {
		return book;
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
}
