package sec03.brd08;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

public class ArticleVO {

	private int level;
	private int parentNO;
	private int articleNO;
	private String title;
	private String content;
	private String imageFileName;
	private String id;
	private Date writeDate;
	
	public ArticleVO() {}

	public ArticleVO(int level, int parentNO, int articleNO, String title, String content, String imageFileName,
			String id) {
		this.level = level;
		this.parentNO = parentNO;
		this.articleNO = articleNO;
		this.title = title;
		this.content = content;
		this.imageFileName = imageFileName;
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getParentNO() {
		return parentNO;
	}

	public void setParentNO(int parentNO) {
		this.parentNO = parentNO;
	}

	public int getArticleNO() {
		return articleNO;
	}

	public void setArticleNO(int articleNO) {
		this.articleNO = articleNO;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageFileName() {
		try {
			if (imageFileName != null && imageFileName.length() != 0) {
				imageFileName = URLDecoder.decode(imageFileName, "UTF-8"); //utf-8로 디코드
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		if(imageFileName!=null) {
		try {
			this.imageFileName = URLEncoder.encode(imageFileName, "UTF-8"); //파일이름에 특수문자가 있을 경우 인코딩합니다.
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(Date writeDate) {
		this.writeDate = writeDate;
	}
	
	
	
}
