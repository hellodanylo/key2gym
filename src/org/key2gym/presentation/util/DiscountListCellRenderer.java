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
package org.key2gym.presentation.util;

import org.key2gym.business.dto.DiscountDTO;
import org.key2gym.business.dto.ItemDTO;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DiscountListCellRenderer implements ListCellRenderer {
    
    private ListCellRenderer renderer;
    
    public DiscountListCellRenderer() {
        renderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof DiscountDTO) {
            value = ((DiscountDTO)value).getTitle();
        } else if(value == null) {
            value = ResourceBundle.getBundle("org/key2gym/presentation/resources/Strings").getString("Text.Discount.None");
        }
        return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
