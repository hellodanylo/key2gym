/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.business.api.dtos;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ReportGeneratorDTO implements Serializable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public List<String> getFormats() {
        return formats;
    }

    private String id;
    private String title;
    private List<String> formats;
}
