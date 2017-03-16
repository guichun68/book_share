package zyzx.linke.model.bean;

import java.io.Serializable;

/**
 * 我登记的书籍
 * @author Austin
 *
 */
public class MyBookDetailVO implements Serializable{

	private BookDetail2 book;
	private Integer status;//书籍当前状态
	
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
	
	
	
}
