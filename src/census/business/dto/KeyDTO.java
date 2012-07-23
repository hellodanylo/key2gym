/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.dto;

import census.persistence.Key;

/**
 *
 * @author daniel
 */
public class KeyDTO {

    public KeyDTO(Short id, String title) {
        this.id = id;
        this.title = title;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    private Short id;
    private String title;
}
