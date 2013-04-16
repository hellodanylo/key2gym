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

package org.key2gym.business.api.dtos;

import java.io.Serializable;
import org.joda.time.DateMidnight;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientProfileDTO implements Serializable {
    
    public ClientProfileDTO() {
        
    }

    public ClientProfileDTO(Integer clientId, Sex sex, DateMidnight birthday, String address, String telephone, String goal, String possibleAttendanceRate, String healthRestrictions, String favouriteSport, FitnessExperience fitnessExperience, String specialWishes, Integer height, Integer weight, Integer adSourceId) {
        this.clientId = clientId;
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
    
    public DateMidnight getBirthday() {
        return birthday;
    }

    public void setBirthday(DateMidnight birthday) {
        this.birthday = birthday;
    }

    public Integer getAdSourceId() {
        return adSourceId;
    }

    public void setAdSourceId(Integer adSourceId) {
        this.adSourceId = adSourceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getHealthRestrictions() {
        return healthRestrictions;
    }

    public void setHealthRestrictions(String healthRestrictions) {
        this.healthRestrictions = healthRestrictions;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getPossibleAttendanceRate() {
        return possibleAttendanceRate;
    }

    public void setPossibleAttendanceRate(String possibleAttendanceRate) {
        this.possibleAttendanceRate = possibleAttendanceRate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getSpecialWishes() {
        return specialWishes;
    }

    public void setSpecialWishes(String specialWishes) {
        this.specialWishes = specialWishes;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    private Sex sex;
    private String address;
    private String telephone;
    private String goal;
    private String possibleAttendanceRate;
    private String healthRestrictions;
    private String favouriteSport;
    private FitnessExperience fitnessExperience;
    private String specialWishes;
    private Integer height;
    private Integer weight;
    private DateMidnight birthday;
    private Integer adSourceId;
    private Integer clientId;
    
    public enum Sex {FEMALE, MALE, UNKNOWN};
    public enum FitnessExperience{NO, YES, UNKNOWN};
}
