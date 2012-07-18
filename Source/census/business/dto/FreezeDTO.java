/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FreezeDTO {
    
    public FreezeDTO() {
        
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }
     
    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public DateMidnight getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(DateMidnight dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getAdministratorFullName() {
        return administratorFullName;
    }

    public void setAdministratorFullName(String administratorFullName) {
        this.administratorFullName = administratorFullName;
    }

    public Short getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Short administratorId) {
        this.administratorId = administratorId;
    }

    public Short getDays() {
        return days;
    }

    public void setDays(Short days) {
        this.days = days;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }
    
    private Short id;
    private Short clientId;
    private String clientFullName;
    private DateMidnight dateIssued;
    private Short administratorId;
    private String administratorFullName;
    private Short days;
    private String note;
}
