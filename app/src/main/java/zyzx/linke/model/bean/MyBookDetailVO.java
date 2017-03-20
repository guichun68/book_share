package zyzx.linke.model.bean;

import java.io.Serializable;

/**
 * 我登记的书籍
 * @author Austin
 *
 */
public class MyBookDetailVO implements Serializable{

	private BookDetail2 book;

    /**
	 * 1：已添加到我的书库
	 * 2：已地图展示
	 * 3：已借出
	 * 4：已借入
 	 */
	private Integer status;//书籍当前状态,取值如上
	private Integer userBookId;//在zyzx_usr_books表记录的id(主键)
	private Integer relatedUserId;//关联到的用户，如图书状态为借入，则此处为从谁借入；如为借出，则表示借出给谁
	private Integer mapId;//在云图中的id
	
	public BookDetail2 getBook() {
		return book;
	}
	
	public void setBook(BookDetail2 book) {
		this.book = book;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUserBookId() {
		return userBookId;
	}

	public void setUserBookId(Integer userBookId) {
		this.userBookId = userBookId;
	}

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public Integer getRelatedUserId() {
		return relatedUserId;
	}

	public void setRelatedUserId(Integer relatedUserId) {
		this.relatedUserId = relatedUserId;
	}
}
