/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
package org.key2gym.business.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import org.key2gym.business.entities.Client;

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
@SequenceGenerator(name="id_cpf_seq", allocationSize = 1)
public class ClientProfile implements Serializable {

    @Id
    @Basic(optional = false)
    @Column(name = "idcln_cpf", columnDefinition="SMALLINT UNSIGNED")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "sex", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private Sex sex;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "address", columnDefinition="TINYTEXT NOT NULL")
    private String address;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "telephone", columnDefinition="TINYTEXT NOT NULL")
    private String telephone;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "goal", columnDefinition="TINYTEXT NOT NULL")
    private String goal;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "possible_attendance_rate", columnDefinition="TINYTEXT NOT NULL")
    private String possibleAttendanceRate;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "health_restrictions", columnDefinition="TINYTEXT NOT NULL")
    private String healthRestrictions;
    
    @Basic(optional = false)
    @Lob
    @Column(name = "favourite_sport", columnDefinition="TINYTEXT NOT NULL")
    private String favouriteSport;
    
    @Basic(optional = false)
    @Column(name = "fitness_experience", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private FitnessExperience fitnessExperience;
    
    public enum FitnessExperience{NO, YES, UNKNOWN};
    
    @Basic(optional = false)
    @Lob
    @Column(name = "special_wishes", columnDefinition="TINYTEXT NOT NULL")
    private String specialWishes;
    
    @Basic(optional = false)
    @Column(name = "height", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private Integer height;
    
    @Basic(optional = false)
    @Column(name = "weight", columnDefinition="TINYINT UNSIGNED NOT NULL")
    private Integer weight;

    @Basic(optional = false)
    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthday;
    
    public static Date DATE_BIRTHDAY_UNKNOWN = new Date();

    @JoinColumn(name = "idads_cpf", referencedColumnName = "id_ads")
    @ManyToOne(optional = false)
    private AdSource adSource;
    
    @JoinColumn(name = "idcln_cpf", referencedColumnName = "id_cln", insertable=false, updatable=false)
    @OneToOne(optional = false)
    private Client client;
    
    public enum Sex {FEMALE, MALE, UNKNOWN};

    public ClientProfile() {
    }
    
    public ClientProfile(Integer id, 
            Sex sex, 
            Date birthday, 
            String address, 
            String telephone, 
            String goal, 
            String possibleAttendanceRate, 
            String healthRestrictions, 
            String favouriteSport,
            FitnessExperience fitnessExperience, 
            String specialWishes, 
            Integer height, 
            Integer weight, 
            AdSource adSource) {
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
        this.adSource = adSource;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public AdSource getAdSource() {
        return adSource;
    }

    public void setAdSource(AdSource adSource) {
        this.adSource = adSource;
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
        return "org.key2gym.business.entities.ClientProfile[ id=" + id + " ]";
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
