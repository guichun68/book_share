package zyzx.linke.model.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BookDetail2 implements Serializable{
	private String id;
	private String alt;
	private String alt_title;

	private List<String> author;

	private String author_intro;

	private String binding;

	private String catalog;

	private String image;

	private Images images;
	private String image_medium;

	private String isbn10;

	private String isbn13;

	private String origin_title;

	private String pages;

	private String price;

	private String pubdate;
	private Date pubdateDateType;

	private String publisher;

	private Rating rating;

	private Series series;

	private String subtitle;

	private String summary;

	private List<Tags> tags;

	private String title;

	private List<String> translator;
	private String douban_url;

	private String url;
	//标记是否是来自豆瓣的数据
	private boolean isFromDouban;

	private String bookClassify;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getAlt() {
		return this.alt;
	}


	public String getImage_medium() {
		return image_medium;
	}

	public void setImage_medium(String image_medium) {
		this.image_medium = image_medium;
	}

	public void setAlt_title(String alt_title) {
		this.alt_title = alt_title;
	}

	public String getAlt_title() {
		return this.alt_title;
	}

	public List<String> getAuthor() {
		return author;
	}

	public String getDouban_url() {
		return douban_url;
	}

	public void setDouban_url(String douban_url) {
		this.douban_url = douban_url;
	}

	public void setAuthor(List<String> author) {
		this.author = author;
	}

	public void setAuthor_intro(String author_intro) {
		this.author_intro = author_intro;
	}

	public String getAuthor_intro() {
		return this.author_intro;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public String getBinding() {
		return this.binding;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getCatalog() {
		return this.catalog;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return this.image;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public Images getImages() {
		return this.images;
	}

	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}

	public String getIsbn10() {
		return this.isbn10;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public String getIsbn13() {
		return this.isbn13;
	}

	public void setOrigin_title(String origin_title) {
		this.origin_title = origin_title;
	}

	public String getOrigin_title() {
		return this.origin_title;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getPages() {
		return this.pages;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPrice() {
		return this.price;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public Date getPubdateDateType() {
		return pubdateDateType;
	}

	public void setPubdateDateType(Date pubdateDateType) {
		this.pubdateDateType = pubdateDateType;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublisher() {
		return this.publisher;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Rating getRating() {
		return this.rating;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public Series getSeries() {
		return this.series;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getSubtitle() {
		return this.subtitle;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public List<Tags> getTags() {
		return this.tags;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTranslator(List<String> translator) {
		this.translator = translator;
	}

	public List<String> getTranslator() {
		return this.translator;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public boolean isFromDouban() {
		return isFromDouban;
	}

	public void setFromDouban(boolean isFromDouban) {
		this.isFromDouban = isFromDouban;
	}

	public String getBookClassify() {
		return bookClassify;
	}

	public void setBookClassify(String bookClassify) {
		this.bookClassify = bookClassify;
	}
}
