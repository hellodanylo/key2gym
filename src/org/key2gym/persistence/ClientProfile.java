/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.key2gym.persistence;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "client_profile_cpf")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ClientProfile.findAll", query = "SELECT c FROM ClientProfile c"),
    @NamedQuery(name = "ClientProfile.findById", query = "SELECT c FROM ClientProfile c WHERE c.id = :id"),
    @NamedQuery(name = "ClientProfile.findByClient", query = "SELECT c FROM ClientProfile c WHERE c.client = :client"),
    @NamedQuery(name = "ClientProfile.findBySex", query = "SELECT c FROM ClientProfile c WHERE c.sex = :sex"),
    @NamedQuery(name = "ClientProfile.findByBirthday", query = "SELECT c FROM ClientProfile c WHERE c.birthday = :birthday"),
    @NamedQuery(name = "ClientProfile.findByFitnessExperience", query = "SELECT c FROM ClientProfile c WHERE c.fitnessExperience = :fitnessExperience"),
    @NamedQuery(name = "ClientProfile.findByHeight", query = "SELECT c FROM ClientProfile c WHERE c.height = :height"),
    @NamedQuery(name = "ClientProfile.findByWeight", query = "SELECT c FROM ClientProfile c WHERE c.weight = :weight"),
    @NamedQuery(name = "ClientProfile.findByAdSource", query = "SELECT c FROM ClientProfile c WHERE c.adSource = :adSource")})

public class ClientProfile implements Serializable {

    @Id
    @Basic(optional = false)
    @Column(name = "idcln_cpf")
    private Short id;
    
    @Basic(optional = false)
    @Column(name = "sex", nullable = false)
    private Sex sex;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "address", nullable = false)
    private String address;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "telephone", nullable = false)
    private String telephone;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "goal", nullable = false)
    private String goal;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "possible_attendance_rate", nullable = false)
    private String possibleAttendanceRate;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "health_restrictions", nullable = false)
    private String healthRestrictions;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "favourite_sport", nullable = false)
    private String favouriteSport;
    
    @Basic(optional = false)
    @Column(name = "fitness_experience", nullable = false)
    private FitnessExperience fitnessExperience;
    
    public enum FitnessExperience{NO, YES, UNKNOWN};
    
    @Basic(optional = false)
    @Lob
    @Column(name = "special_wishes", nullable = false)
    private String specialWishes;
    
    @Basic(optional = false)
    @Column(name = "height", nullable = false)
    private Short height;
    
    @Basic(optional = false)
    @Column(name = "weight", nullable = false)
    private Short weight;

    @Basic(optional = false)
    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthday;
    
    public static Date defaultBirthday; 
    static {
        try {
            defaultBirthday = new SimpleDateFormat("dd-MM-yyyy").parse("16-05-2096");
        } catch (ParseException ex) {
            Logger.getLogger(ClientProfile.class.getName()).error("Failed to create default birthday value!", ex);
            defaultBirthday = null;
        }
    }
    
    @Basic(optional=false)
    @Column(name="idads_cpf")
    private Short adSourceId;
     
    @JoinColumn(name = "idads_cpf", referencedColumnName = "id_ads", insertable=false, updatable=false)
    @ManyToOne(optional = false)
    private AdSource adSource;
    
    @JoinColumn(name = "idcln_cpf", referencedColumnName = "id_cln", insertable=false, updatable=false)
    @OneToOne(optional = false)
    private Client client;
    
    public enum Sex {FEMALE, MALE, UNKNOWN};

    public ClientProfile() {
    }
    
    public ClientProfile(Short id, Sex sex, Date birthday, String address, String telephone, String goal, String possibleAttendanceRate, String healthRestrictions, String favouriteSport, FitnessExperience fitnessExperience, String specialWishes, Short height, Short weight, Short adSourceId) {
        this.id = id;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.telephone = telephone;
        this.goal = goal;
        this.possibleAttendanceRate = possibleAttendanceRate;
        this.healthRestrictions = healthRestrictions;
        this.favouriteSport = favouriteSport;
        this.fitnessExperience = fitnessExperience;
        this.specialWishes = specialWishes;
        this.height = height;
        this.weight = weight;
        this.adSourceId = adSourceId;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getPossibleAttendanceRate() {
        return possibleAttendanceRate;
    }

    public void setPossibleAttendanceRate(String possibleAttendanceRate) {
        this.possibleAttendanceRate = possibleAttendanceRate;
    }

    public String getHealthRestrictions() {
        return healthRestrictions;
    }

    public void setHealthRestrictions(String healthRestrictions) {
        this.healthRestrictions = healthRestrictions;
    }

    public String getFavouriteSport() {
        return favouriteSport;
    }

    public void setFavouriteSport(String favouriteSport) {
        this.favouriteSport = favouriteSport;
    }

    public FitnessExperience getFitnessExperience() {
        return fitnessExperience;
    }

    public void setFitnessExperience(FitnessExperience fitnessExperience) {
        this.fitnessExperience = fitnessExperience;
    }

    public String getSpecialWishes() {
        return specialWishes;
    }

    public void setSpecialWishes(String specialWishes) {
        this.specialWishes = specialWishes;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(Short height) {
        this.height = height;
    }

    public Short getWeight() {
        return weight;
    }

    public void setWeight(Short weight) {
        this.weight = weight;
    }

    public AdSource getAdSource() {
        return adSource;
    }

    public void setAdSource(AdSource adSource) {
        this.adSource = adSource;
    }
    
    public void setAdSourceId(Short adSourceId) {
        this.adSourceId = adSourceId;
    }
    
    public Short getAdSourceId() {
        return adSourceId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClientProfile)) {
            return false;
        }
        ClientProfile other = (ClientProfile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.key2gym.persistence.ClientProfile[ id=" + id + " ]";
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
