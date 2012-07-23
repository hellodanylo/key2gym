/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business;

import census.business.api.BusinessException;
import census.business.api.SecurityException;
import census.business.api.ValidationException;
import census.business.api.Validator;
import census.business.dto.ClientProfileDTO;
import census.persistence.AdSource;
import census.persistence.Client;
import census.persistence.ClientProfile;
import org.joda.time.DateMidnight;
import org.joda.time.Instant;

/**
 *
 * @author daniel
 */
public class ClientProfilesService extends BusinessService {

    private static ClientProfilesService instance;

    /**
     * Attaches a client profile to the client. 
     * 
     * <ul>
     * 
     * <li> The client profile's ID is also the client's ID.
     * 
     * <li> If the client already has a profile, it will be updated.
     * 
     * </ul>
     *
     * @param clientProfile the Client Profile
     * @throws NullPointerException if any of arguments or required properties
     * is null
     * @throws IllegalStateException if the transaction or the session is not active
     * @throws BusinessException if current business rules restrict this
     * operation
     * @throws ValidationException if any of the required properties is invalid
     */
    public void attachClientProfile(ClientProfileDTO clientProfile) throws BusinessException, ValidationException {
        assertTransactionActive();
        assertSessionActive();
        
        if (clientProfile == null) {
            throw new NullPointerException("The clientProfile is null."); //NOI18N
        } else if(clientProfile.getId() == null) {
            throw new NullPointerException("The clientProfile.getId() is null."); //NOI18N
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
        if(clientProfile.getBirthday() == null) {
            clientProfile.setBirthday(new DateMidnight(ClientProfile.defaultBirthday));
        }
        
        getAdSourceIdValidator().validate(clientProfile.getAdSourceId());
        
        /*
         * Builds an exact copy of the entity, because it's not a good 
         * practive to make entites instances used as DTO managed.
         */
        ClientProfile entityClientProfile = 
                new ClientProfile(
                clientProfile.getId(), 
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
                clientProfile.getAdSourceId());
        
        // TODO: note change
        if(entityManager.find(ClientProfile.class, clientProfile.getId()) == null) {
            entityManager.persist(entityClientProfile);
        } else {
            entityManager.merge(entityClientProfile);
        }
        entityManager.flush();
    }
    
    /**
     * Detaches the profile from the client by its id.
     * 
     * <ul>
     * 
     * <li> The permissions level has to be PL_ALL
     * 
     * <li> The client has to have a profile attached
     * 
     * </ul>
     * 
     * @param id the client's ID whose profile to detach
     * @throws IllegalStateException if the session or the transaction is not active
     * @throws SecurityException if current security rules restrict this operation
     * @throws NullPointerException if the id is null
     * @throws ValidationException if the id is invalid
     * @throws BusinessException if current business rules restrict this operation
     */
    public void detachClientProfile(Short id) throws SecurityException, ValidationException, BusinessException {
        assertSessionActive();
        assertTransactionActive();
        
        if(!sessionService.getPermissionsLevel().equals(SessionsService.PL_ALL)) {
            throw new SecurityException(bundle.getString("OperationDenied"));
        }
        
        if(id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }
        
        Client client = entityManager.find(Client.class, id);
        
        if(client == null) {
            throw new ValidationException(bundle.getString("IDInvalid"));
        }
        
        if(client.getClientProfile() == null) {
            throw new BusinessException(bundle.getString("ClientHasNoProfile"));
        }
        
        // TODO: note change
        entityManager.remove(client.getClientProfile());
        entityManager.flush();
    }
    
    /**
     * Gets a client profile by ID.
     * 
     * @param id the client profile's ID
     * @return the client profile
     * @throws ValidationException the ID is invalid 
     * @throws IllegalStateException if the session is not active
     */
    public ClientProfileDTO getById(Short id) throws ValidationException {
        assertSessionActive();
        
        if(id == null) {
            throw new NullPointerException("The id is null."); //NOI18N
        }
        
        ClientProfile clientProfile = entityManager.find(ClientProfile.class, id);
        
        if(clientProfile == null) {
            throw new ValidationException(bundle.getString("ClientProfileIDInvalid"));
        }
        
        ClientProfileDTO clientProfileDTO = new ClientProfileDTO(
                clientProfile.getId(), 
                ClientProfileDTO.Sex.values()[clientProfile.getSex().ordinal()], 
                clientProfile.getBirthday().equals(ClientProfile.defaultBirthday) ? null : new DateMidnight(clientProfile.getBirthday()), 
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
                clientProfile.getAdSourceId());
        
        return clientProfileDTO;
    }
    
    private Validator getBirthdayValidator() {
        return new Validator<DateMidnight>() {

            @Override
            public void validate(DateMidnight value) throws ValidationException {
                if (value != null && value.isAfter(new Instant())) {
                    throw new ValidationException(bundle.getString("ClientProfileBirthdayMustBeInPast"));
                }
            }
        };
    }

    private Validator getIdValidator() {
        return new Validator<Short>() {

            @Override
            public void validate(Short value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The clientProfile's ID is null."); //NOI18N
                }

                ClientProfile clientProfile = entityManager.find(ClientProfile.class, value);

                if (clientProfile != null) {
                    throw new ValidationException(bundle.getString("ClientProfileAlreadyAttached"));
                }
            }
        };
    }

    private Validator getAdSourceIdValidator() {
        return new Validator<Short>() {

            @Override
            public void validate(Short value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The ad source's ID is null."); //NOI18N
                }

                AdSource adSource = entityManager.find(AdSource.class, value);

                if (adSource == null) {
                    throw new ValidationException(bundle.getString("AdSourceIDInvalid"));
                }
            }
        };
    }

    private Validator getHeightValidator() {
        return new Validator<Short>() {

            @Override
            public void validate(Short value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The height is null."); //NOI18N
                }

                if (value < 0) {
                    throw new ValidationException(bundle.getString("ClientProfileHeightCanNotBeNegative"));
                }
            }
        };
    }

    private Validator getWeightValidator() {
        return new Validator<Short>() {

            @Override
            public void validate(Short value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The weight is null."); //NOI18N
                }

                if (value < 0) {
                    throw new ValidationException(bundle.getString("ClientProfileWeightCanNotBeNegative"));
                }
            }
        };
    }

    /**
     * Gets an instance of this class.
     * 
     * @return an instance of this class 
     */
    public static ClientProfilesService getInstance() {
        if (instance == null) {
            instance = new ClientProfilesService();
        }
        return instance;
    }
}
