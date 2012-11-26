package org.key2gym.business;

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


import java.text.MessageFormat;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.Validator;
import org.key2gym.persistence.AdSource;
import org.key2gym.persistence.Client;
import org.key2gym.persistence.ClientProfile;
import org.joda.time.DateMidnight;
import org.joda.time.Instant;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.dtos.ClientProfileDTO;
import org.key2gym.business.api.remote.ClientProfilesServiceRemote;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(ClientProfilesServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
@RolesAllowed({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class ClientProfilesServiceBean extends BasicBean implements ClientProfilesServiceRemote {

    @Override
    public void updateClientProfile(ClientProfileDTO clientProfile) throws BusinessException, ValidationException {

        if (clientProfile == null) {
            throw new NullPointerException("The clientProfile is null."); //NOI18N
        } else if (clientProfile.getClientId() == null) {
            throw new NullPointerException("The clientProfile.getClientId() is null."); //NOI18N
        } else if (clientProfile.getAddress() == null) {
            throw new NullPointerException("The clientProfile.getAddress() is null."); //NOI18N
        } else if (clientProfile.getTelephone() == null) {
            throw new NullPointerException("The clientProfile.getTelephone() is null."); //NOI18N
        } else if (clientProfile.getFavouriteSport() == null) {
            throw new NullPointerException("The clientProfile.getFavouriteSport() is null."); //NOI18N
        } else if (clientProfile.getGoal() == null) {
            throw new NullPointerException("The clientProfile.getGoal() is null."); //NOI18N
        } else if (clientProfile.getHealthRestrictions() == null) {
            throw new NullPointerException("The clientProfile.getHealthRestrictions() is null."); //NOI18N
        } else if (clientProfile.getPossibleAttendanceRate() == null) {
            throw new NullPointerException("The clientProfile.getPossibleAttendanceRate() is null."); //NOI18N
        } else if (clientProfile.getSpecialWishes() == null) {
            throw new NullPointerException("The clientProfile.getSpecialWishes() is null."); //NOI18N
        }

        getHeightValidator().validate(clientProfile.getHeight());
        getWeightValidator().validate(clientProfile.getWeight());

        /*
         * Birthday
         */
        getBirthdayValidator().validate(clientProfile.getBirthday());
        if (clientProfile.getBirthday() == null) {
            clientProfile.setBirthday(new DateMidnight(ClientProfile.DATE_BIRTHDAY_UNKNOWN));
        }

        AdSource adSource = null;

        if (clientProfile.getAdSourceId() != null) {
            adSource = entityManager.find(AdSource.class, clientProfile.getAdSourceId());

            if (adSource == null) {
                throw new ValidationException(getString("Invalid.AdSource.ID"));
            }
        }

        /*
         * Builds an exact copy of the entity, because it's not a good 
         * practive to make entites instances used as DTO managed.
         */
        ClientProfile entityClientProfile =
                new ClientProfile(
                clientProfile.getClientId(),
                ClientProfile.Sex.values()[clientProfile.getSex().ordinal()],
                clientProfile.getBirthday().toDate(),
                clientProfile.getAddress(),
                clientProfile.getTelephone(),
                clientProfile.getGoal(),
                clientProfile.getPossibleAttendanceRate(),
                clientProfile.getHealthRestrictions(),
                clientProfile.getFavouriteSport(),
                ClientProfile.FitnessExperience.values()[clientProfile.getFitnessExperience().ordinal()],
                clientProfile.getSpecialWishes(),
                clientProfile.getHeight(),
                clientProfile.getWeight(),
                adSource);

        if (entityManager.find(ClientProfile.class, clientProfile.getClientId()) == null) {
            entityManager.persist(entityClientProfile);
        } else {
            entityManager.merge(entityClientProfile);
        }

        entityManager.flush();
    }

    /**
     * Detaches the profile from the client by its ID.
     * <p>
     * 
     * <ul>
     * <li> The permissions level has to be PL_ALL </li>
     * <li> The client has to have a profile attached </li>
     * </ul>
     * 
     * @param id the client's ID whose profile to detach
     * @throws IllegalStateException if the transaction is not active; if no session is open
     * @throws SecurityException if current security rules restrict this operation
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the id is invalid
     * @throws BusinessException if current business rules restrict this operation
     */
    @Override
    public void detachClientProfile(Integer id) throws SecurityException, ValidationException, BusinessException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        Client client = entityManager.find(Client.class, id);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        if (client.getClientProfile() == null) {
            throw new BusinessException(getString("BusinessRule.Client.HasNoProfile"));
        }

        entityManager.remove(client.getClientProfile());
        entityManager.flush();
    }

    @Override
    public ClientProfileDTO getById(Integer id) throws ValidationException {

        if (id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }

        ClientProfile clientProfile = entityManager.find(ClientProfile.class, id);

        if (clientProfile == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        ClientProfileDTO clientProfileDTO = new ClientProfileDTO(
                clientProfile.getId(),
                ClientProfileDTO.Sex.values()[clientProfile.getSex().ordinal()],
                clientProfile.getBirthday().equals(ClientProfile.DATE_BIRTHDAY_UNKNOWN) ? null : new DateMidnight(clientProfile.getBirthday()),
                clientProfile.getAddress(),
                clientProfile.getTelephone(),
                clientProfile.getGoal(),
                clientProfile.getPossibleAttendanceRate(),
                clientProfile.getHealthRestrictions(),
                clientProfile.getFavouriteSport(),
                ClientProfileDTO.FitnessExperience.values()[clientProfile.getFitnessExperience().ordinal()],
                clientProfile.getSpecialWishes(),
                clientProfile.getHeight(),
                clientProfile.getWeight(),
                clientProfile.getAdSource() == null ? null : clientProfile.getAdSource().getId());

        return clientProfileDTO;
    }

    private Validator getBirthdayValidator() {
        return new Validator<DateMidnight>() {

            @Override
            public void validate(DateMidnight value) throws ValidationException {
                if (value != null && value.isAfter(new Instant())) {
                    throw new ValidationException(getString("Invalid.ClientProfile.Birthday.MustBeInPast"));
                }
            }
        };
    }

    private Validator getHeightValidator() {
        return new Validator<Integer>() {

            @Override
            public void validate(Integer value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The height is null."); //NOI18N
                }

                if (value < 0) {
                    String message = MessageFormat.format(
                            getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                            getString("Property.Height"));

                    throw new ValidationException(message);
                }
            }
        };
    }

    private Validator getWeightValidator() {
        return new Validator<Integer>() {

            @Override
            public void validate(Integer value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The weight is null."); //NOI18N
                }

                if (value < 0) {
                    String message = MessageFormat.format(
                            getString("Invalid.Property.CanNotBeNegative.withPropertyName"),
                            getString("Property.Weight"));

                    throw new ValidationException(message);
                }
            }
        };
    }
    
    @PersistenceContext(unitName = "PU")
    private EntityManager entityManager;
}
