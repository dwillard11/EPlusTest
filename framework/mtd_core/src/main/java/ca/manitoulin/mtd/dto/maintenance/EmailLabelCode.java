package ca.manitoulin.mtd.dto.maintenance;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class EmailLabelCode extends AbstractDTO {
    private String id;
    private String name;
    private String backColor; // cd_description: #FFFFFF, #FF00000
    private String fontColor; // cd_description: #FFFFFF, #FF00000

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
}
