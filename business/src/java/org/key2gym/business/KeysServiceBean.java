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
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import org.key2gym.business.api.SecurityViolationException;
import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.KeyDTO;
import org.key2gym.business.api.remote.KeysServiceRemote;
import org.key2gym.persistence.Key;

/**
 *
 * @author Danylo Vashchilenko
 */
@Stateless
@Remote(KeysServiceRemote.class)
@DeclareRoles({SecurityRoles.MANAGER, SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR})
public class KeysServiceBean extends BasicBean implements KeysServiceRemote {

    @Override
    public List<KeyDTO> getKeysAvailable() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<KeyDTO> result = new LinkedList();
        List<Key> keys;

        keys = getEntityManager().createNamedQuery("Key.findAvailable") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    @Override
    public List<KeyDTO> getKeysTaken() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<KeyDTO> result = new LinkedList<KeyDTO>();
        List<Key> keys;

        keys = getEntityManager().createNamedQuery("Key.findTaken") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    @Override
    public List<KeyDTO> getAllKeys() throws SecurityViolationException {

        if (!callerHasAnyRole(SecurityRoles.JUNIOR_ADMINISTRATOR, SecurityRoles.SENIOR_ADMINISTRATOR, SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Access.Denied"));
        }

        List<KeyDTO> result = new LinkedList<KeyDTO>();
        List<Key> keys;

        keys = getEntityManager().createNamedQuery("Key.findAll") //NOI18N
                .getResultList();

        for (Key key : keys) {
            result.add(new KeyDTO(key.getId(), key.getTitle()));
        }

        return result;
    }

    @Override
    public void addKey(KeyDTO key) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        Key keyEntity = new Key();

        String title = key.getTitle().trim();
        if (title.isEmpty()) {
            throw new ValidationException(getString("Invalid.Property.CanNotBeEmpty.withPropertyName", "Property.Title"));
        }
        keyEntity.setTitle(title);

        getEntityManager().persist(keyEntity);
        getEntityManager().flush();
    }

    @Override
    public void updateKey(KeyDTO key) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        Key keyEntity;

        try {
            keyEntity = getEntityManager().find(Key.class, key.getId());
        } catch (NoResultException ex) {
            throw new ValidationException(getString("Invalid.ID"));
        }

        String title = key.getTitle().trim();
        if (title.isEmpty()) {
            throw new ValidationException(getString("Invalid.Property.CanNotBeEmpty.withPropertyName", "Property.Title"));
        }
        keyEntity.setTitle(title);

        getEntityManager().merge(keyEntity);
        getEntityManager().flush();
    }

    @Override
    public void removeKey(Integer id) throws ValidationException, SecurityViolationException {

        if (!callerHasRole(SecurityRoles.MANAGER)) {
            throw new SecurityViolationException(getString("Security.Operation.Denied"));
        }

        Key keyEntity;

        try {
            keyEntity = getEntityManager().find(Key.class, id);
        } catch (NoResultException ex) {
            throw new ValidationException(getString("Invalid.ID"));
        }

        Long count = (Long) getEntityManager().createNamedQuery("Attendance.countWithKey")
                .setParameter("key", keyEntity)
                .getSingleResult();
        if (count > 0) {
            throw new ValidationException(getString("Invalid.Key.HasUnarchivedAttendances"));
        }

        getEntityManager().remove(keyEntity);
        getEntityManager().flush();
    }
}
