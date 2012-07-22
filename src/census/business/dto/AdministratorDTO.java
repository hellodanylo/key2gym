/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.business.dto;

/**
 *
 * @author Danylo Vashchilenko
 */
public class AdministratorDTO {

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Short getPermissionsLevel() {
        return permissionsLevel;
    }

    public void setPermissionsLevel(Short permissionsLevel) {
        this.permissionsLevel = permissionsLevel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    

    private Short id;
    private String userName;
    private String fullName;
    private Short permissionsLevel;
    private String note;    
}
