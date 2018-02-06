package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Created by zhuiz on 2017/4/21.
 *
 * @Description
 */
public class TripEventNotify extends AbstractDTO {
    private Integer id;
    private Integer eventId;
    private String name;
    private String email;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
