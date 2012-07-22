/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.dto;

import census.persistence.Attendance;
import org.joda.time.DateTime;
import org.joda.time.DateTime;

/**
 *
 * @author daniel
 */
public class AttendanceDTO {

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public Short getClientId() {
        return clientId;
    }

    public void setClientId(Short clientId) {
        this.clientId = clientId;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public DateTime getDateTimeBegin() {
        return DateTimeBegin;
    }

    public void setDateTimeBegin(DateTime DateTimeBegin) {
        this.DateTimeBegin = DateTimeBegin;
    }

    public DateTime getDateTimeEnd() {
        return DateTimeEnd;
    }

    public void setDateTimeEnd(DateTime DateTimeEnd) {
        this.DateTimeEnd = DateTimeEnd;
    }

    public Short getKeyId() {
        return keyId;
    }

    public void setKeyId(Short keyId) {
        this.keyId = keyId;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyString) {
        this.keyTitle = keyString;
    }
    
    private Short id;
    private DateTime DateTimeBegin;
    private String clientFullName;
    private Short clientId;
    private String keyTitle;
    private Short keyId;
    private DateTime DateTimeEnd;
    
    // The DateTime is set to 2004-04-04 09:00:01
    //public static DateTime DateTime_END_UNKNOWN = new DateTime(Attendance.DATETIME_END_UNKNOWN);
    
}
