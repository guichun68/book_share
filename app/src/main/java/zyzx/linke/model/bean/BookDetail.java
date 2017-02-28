package zyzx.linke.model.bean;

import java.util.List;

public class BookDetail {

	private String alt;

	private String alt_title;

	private List<String> author;

	private String author_intro;

	private String binding;

	private String catalog;
	/**
	 * 表示豆瓣中该书籍的id
	 */
	private String b_id;

	private String image;

	private Images images;

	private String isbn10;

	private String isbn13;

	private String origin_title;

	private String pages;

	private String price;

	private String pubdate;

	private String publisher;

	private Rating rating;

	private Series series;

	private String subtitle;

	private String summary;

	private List<Tags> tags;

	private String title;

	private List<String> translator;

	/**
	 * 豆瓣的获取该图书详情的json串的地址
	 */
	private String url;
	//标记是否是来自豆瓣的数据
	private boolean isFromDouban;

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getAlt() {
		return this.alt;
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

	public String getB_id() {
		return b_id;
	}

	public void setB_id(String b_id) {
		this.b_id = b_id;
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

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getPubdate() {
		return this.pubdate;
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

	public void setFromDouban(boolean fromDouban) {
		isFromDouban = fromDouban;
	}
}
