package ca.manitoulin.mtd.dto.misc;

import java.util.Locale;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * POJO of PMLANG table
 * 
 * @author Bob Yu
 *
 */
public class UserDefinedMessage extends AbstractDTO {
	
	private String id;
	private String content;
	//private String language;
	private String translatedContent;
	private String uniqueKey;
	
	/**
	 * If locale is French, return translatedContent;
	 * @param locale
	 * @return
	 */
	public String getLocaleContent(Locale locale){
		if(locale == null) {return content;}
		if(ContextConstants.LOCALE_CANADA_FRENCH.equals(locale)) 
			return translatedContent;
		else
			return content;
			
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	/*public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}*/
	public String getTranslatedContent() {
		return translatedContent;
	}
	public void setTranslatedContent(String translatedContent) {
		this.translatedContent = translatedContent;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	@Override
	public String toString() {
		return "UserDefinedMessage [id=" + id + ", content=" + content //+ ", language=" + language
				+ ", translatedContent=" + translatedContent + ", uniqueKey=" + uniqueKey + "]";
	}
	
	
	
	

}
