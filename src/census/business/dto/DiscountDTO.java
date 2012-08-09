/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DiscountDTO {
    private Short id;
    private String title;
    private Short percent;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Short getPercent() {
        return percent;
    }

    public void setPercent(Short percent) {
        this.percent = percent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
