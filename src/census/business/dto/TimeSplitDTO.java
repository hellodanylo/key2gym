/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

import org.joda.time.LocalTime;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TimeSplitDTO {

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }
    
    Short id;
    LocalTime time;
    String title;
}
