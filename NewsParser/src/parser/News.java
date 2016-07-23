package parser;

public class News {
	
	private String crawling_id;
	private String link;
	private String title;
	private String text;
	private String fileName;
	private Integer place;
	
	public News(String crawling_id, String link, String title, String text, String fileName, Integer place) {
		super();
		this.crawling_id = crawling_id;
		this.link = link;
		this.title = title;
		this.text = text;
		this.fileName = fileName;
		this.place = place;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCrawling_id() {
		return crawling_id;
	}
	public void setCrawling_id(String crawling_id) {
		this.crawling_id = crawling_id;
	}
	public Integer getPlace() {
		return place;
	}
	public void setPlace(Integer place) {
		this.place = place;
	}
	
	
	
	

}
