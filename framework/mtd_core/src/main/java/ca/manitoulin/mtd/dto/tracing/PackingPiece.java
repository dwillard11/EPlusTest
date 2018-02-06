package ca.manitoulin.mtd.dto.tracing;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class PackingPiece extends AbstractDTO {
	
	private String pieces;
	private String description;
	private String weight;
	
	public String getPieces() {
		return pieces;
	}
	public void setPieces(String pieces) {
		this.pieces = pieces;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}

}
