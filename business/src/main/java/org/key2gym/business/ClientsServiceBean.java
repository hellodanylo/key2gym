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


import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.joda.time.DateMidnight;
import org.key2gym.business.api.BusinessException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.Validator;
import org.key2gym.business.api.dtos.ClientDTO;
import org.key2gym.business.api.remote.ClientsServiceRemote;
import org.key2gym.business.entities.Client;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(ClientsServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class ClientsServiceBean extends BasicBean implements ClientsServiceRemote {

    @Override
    public ClientDTO getTemplateClient() {
        ClientDTO client = new ClientDTO();

        client.setId(getNextId());
        client.setAttendancesBalance(0);
        client.setExpirationDate(new DateMidnight());
        client.setMoneyBalance(BigDecimal.ZERO.setScale(2));
        client.setRegistrationDate(new DateMidnight());

        return client;
    }

    @Override
    public Integer registerClient(ClientDTO client, Boolean useSecuredProperties) throws BusinessException, ValidationException, SecurityException {
        
        if (client == null) {
            throw new NullPointerException("The client is null."); //NOI18N
        }

        if (useSecuredProperties == null) {
            throw new NullPointerException("The useSecuredPropeties is null."); //NOI18N
        }

        Client newClient = new Client();

        /*
         * Validation
         */
        getFullNameValidator().validate(client.getFullName());
        newClient.setFullName(client.getFullName());

        getCardValidator().validate(client.getCard());
        newClient.setCard(client.getCard());

        getNoteValidator().validate(client.getNote());
        newClient.setNote(client.getNote());

        if (useSecuredProperties && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityException(getString("Security.Client.UpdateSecuredProperties.Denied"));
        }

        if (useSecuredProperties) {
            if (client.getAttendancesBalance() == null) {
                throw new NullPointerException("The client.getAttendancesBalance() is null."); //NOI18N
            }
            newClient.setAttendancesBalance(client.getAttendancesBalance());
            if (client.getMoneyBalance() == null) {
                throw new NullPointerException("The client.getMoneyBalance() is null."); //NOI18N
            }
            newClient.setMoneyBalance(BigDecimal.ZERO);
            if (client.getRegistrationDate() == null) {
                throw new NullPointerException("The client.getRegistrationDate() is null."); //NOI18N
            }
            newClient.setRegistrationDate(new Date());
            if (client.getExpirationDate() == null) {
                throw new NullPointerException("The client.getExpirationDate() is null."); //NOI18N
            }
            newClient.setExpirationDate(client.getExpirationDate().toDate());
        } else {
            newClient.setAttendancesBalance(0);
            newClient.setMoneyBalance(BigDecimal.ZERO);
            newClient.setRegistrationDate(new Date());
            newClient.setExpirationDate(client.getRegistrationDate().toDate());
        }

        entityManager.persist(newClient);
        entityManager.flush();

        return newClient.getId();
    }

    @Override
    public ClientDTO getById(Integer clientId) throws ValidationException {


        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        Client client = entityManager.find(Client.class, clientId);

        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientId);
        clientDTO.setFullName(client.getFullName());
        clientDTO.setAttendancesBalance(client.getAttendancesBalance());
        clientDTO.setMoneyBalance(client.getMoneyBalance());
        clientDTO.setRegistrationDate(new DateMidnight(client.getRegistrationDate()));
        clientDTO.setExpirationDate(new DateMidnight(client.getExpirationDate()));
        clientDTO.setNote(client.getNote());
        clientDTO.setCard(client.getCard());

        return clientDTO;
    }

    @Override
    public Integer findByCard(Integer card) {


        if (card == null) {
            throw new NullPointerException("The card is null."); //NOI18N
        }

        try {
            Client client = (Client) entityManager.createNamedQuery("Client.findByCard") //NOI18N
                    .setParameter("card", card) //NOI18N
                    .setMaxResults(1)
                    .getSingleResult();
            return client.getId();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<ClientDTO> findByFullName(String fullName, Boolean exactMatch) throws IllegalArgumentException {


        if (fullName == null) {
            throw new IllegalArgumentException("The fullName is null."); //NOI18N
        }

        if (exactMatch == null) {
            throw new IllegalArgumentException("The exactMatch is null."); //NOI18N
        }

        List<Client> clients;

        if (exactMatch == true) {
            clients = entityManager.createNamedQuery("Client.findByFullNameExact") //NOI18N
                    .setParameter("fullName", fullName) //NOI18N
                    .getResultList();
        } else {
            clients = entityManager.createNamedQuery("Client.findByFullNameNotExact") //NOI18N
                    .setParameter("fullName", fullName) //NOI18N
                    .getResultList(); //NOI18N
        }

        List<ClientDTO> result = new LinkedList<ClientDTO>();

        for (Client client : clients) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(client.getId());
            clientDTO.setFullName(client.getFullName());
            clientDTO.setAttendancesBalance(client.getAttendancesBalance());
            clientDTO.setMoneyBalance(client.getMoneyBalance());
            clientDTO.setRegistrationDate(new DateMidnight(client.getRegistrationDate()));
            clientDTO.setExpirationDate(new DateMidnight(client.getExpirationDate()));
            clientDTO.setNote(client.getNote());
            clientDTO.setCard(client.getCard());
            result.add(clientDTO);
        }

        return result;
    }

    @Override
    public void updateClient(ClientDTO client, Boolean useSecuredProperties) throws SecurityException, ValidationException {
        


        if (client == null) {
            throw new NullPointerException("The client is null."); //NOI18N
        }

        if (client.getId() == null) {
            throw new NullPointerException("The client's ID is null."); //NOI18N
        }

        if (useSecuredProperties == null) {
            throw new NullPointerException("The useSecuredProperties is null."); //NOI18N
        }

        Client originalClient = entityManager.find(Client.class, client.getId());

        if (originalClient == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        if (useSecuredProperties && !callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityException(getString("Security.Client.UpdateSecuredProperties.Denied"));
        }

        if (!originalClient.getFullName().equals(client.getFullName())) {
            getFullNameValidator().validate(client.getFullName());
            originalClient.setFullName(client.getFullName());
        }

        /*
         * Validates the card, only if it was changed. Otherwise, the validator
         * will throw ValidationException for the card is already assigned to
         * this client.
         */
        if ((originalClient.getCard() != null && !originalClient.getCard().equals(client.getCard()))
                || (client.getCard() != null && !client.getCard().equals(originalClient.getCard()))) {
            getCardValidator().validate(client.getCard());
            originalClient.setCard(client.getCard());
        }

        /*
         * It's faster just to revalidate the note and merge it.
         */
        getNoteValidator().validate(client.getNote());
        originalClient.setNote(client.getNote());

        if (client.getAttendancesBalance() == null) {
            throw new NullPointerException("The client.getAttendancesBalance() is null"); //NOI18N
        }
        originalClient.setAttendancesBalance(client.getAttendancesBalance());

        if (client.getMoneyBalance() == null) {
            throw new NullPointerException("The client.getMoneyBalance() is null."); //NOI18N
        }

        /*
         * Normalizes the scale, and throws an exception, if the scale is 
         * to big.
         */
        if (client.getMoneyBalance().scale() > 2) {
            throw new ValidationException(getString("Invalid.Money.TwoDigitsAfterDecimalPointMax"));
        }
        client.setMoneyBalance(client.getMoneyBalance().setScale(2));

        if (client.getMoneyBalance().precision() > 5) {
            throw new ValidationException(getString("Invalid.Client.MoneyBalance.LimitReached"));
        }
        originalClient.setMoneyBalance(client.getMoneyBalance());

        if (originalClient.getRegistrationDate() == null) {
            throw new NullPointerException("The client.getRegistrationDate() is null."); //NOI18N
        }
        originalClient.setRegistrationDate(client.getRegistrationDate().toDate());

        if (originalClient.getExpirationDate() == null) {
            throw new NullPointerException("The client.getExpirationDate() is null."); //NOI18N
        }
        originalClient.setExpirationDate(client.getExpirationDate().toDate());

        entityManager.flush();
    }

    @Override
    public Boolean hasDebt(Integer clientId) throws ValidationException {


        if (clientId == null) {
            throw new NullPointerException("The clientId is null."); //NOI18N
        }

        Client client = entityManager.find(Client.class, clientId);
        if (client == null) {
            throw new ValidationException(getString("Invalid.Client.ID"));
        }

        return client.getMoneyBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public Integer getNextId() {


        try {
            return 1 + (Integer) entityManager
                    .createNamedQuery("Client.findAllIdsOrderByIdDesc") //NOI18N
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 1;
        }
    }

    public Validator getFullNameValidator() {
        return new Validator<String>() {
            @Override
            public void validate(String value) throws ValidationException {
                if (value == null) {
                    throw new NullPointerException("The full name is null."); //NOI18N
                }
                if (value.trim().isEmpty()) {
                    throw new ValidationException(getString("Invalid.Client.FullName.CanNotBeEmpty"));
                }
            }
        };
    }

    public Validator getCardValidator() {
        return new Validator<Integer>() {
            @Override
            public void validate(Integer value) throws ValidationException {
                if (value != null && (value > 99999999 || value < 10000000)) {
                    throw new ValidationException(getString("Invalid.Client.Card")); //NOI18N
                }

                // If a card is to be assigned,
                // let's make sure no one else uses it.
                if (value != null) {
                    List<Client> clients = entityManager
                            .createNamedQuery("Client.findByCard") //NOI18N
                            .setParameter("card", value) //NOI18N
                            .getResultList();

                    if (!clients.isEmpty()) {
                        String message = MessageFormat.format(
                                getString("Invalid.Client.Card.AlreadyInUse.withClientFullNameAndID"),
                                new Object[]{
                                    clients.get(0).getFullName(),
                                    clients.get(0).getId()});
                        throw new ValidationException(message);
                    }
                }
            }
        };
    }

    public Validator getNoteValidator() {
        return new Validator<String>() {
            @Override
            public void validate(String value) throws ValidationException, IllegalArgumentException {
                if (value == null) {
                    throw new NullPointerException("The note is null."); //NOI18N
                }
            }
        };
    }
    
    @PersistenceContext(unitName = "PU")
    private EntityManager entityManager;
}
