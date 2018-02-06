package ca.manitoulin.mtd.web.utils;

import com.lowagie.text.Phrase;

public class Field {
	
	private String label;
	private Phrase value;
	
	public Field(String label, Phrase value) {
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Phrase getValue() {
		return value;
	}
	public void setValue(Phrase value) {
		this.value = value;
	}

}
