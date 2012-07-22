/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.util;

import census.business.api.BusinessException;
import census.business.api.ValidationException;
import java.util.Map;

/**
 *
 * @author daniel
 */
public interface CensusActionListener {
    public void actionPerformed(String actionName, Map<String, Object> properties) throws BusinessException, ValidationException;
}
