/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.business.api.dtos;

import java.io.Serializable;

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

    public String getPrimaryFormat() {
        return primaryFormat;
    }

    public void setPrimaryFormat(String primaryFormat) {
        this.primaryFormat = primaryFormat;
    }

    public String[] getSecondaryFormats() {
        return secondaryFormats;
    }

    public void setSecondaryFormats(String[] secondaryFormats) {
        this.secondaryFormats = secondaryFormats;
    }

    private String id;
    private String title;
    private String primaryFormat;
    private String[] secondaryFormats;
}
