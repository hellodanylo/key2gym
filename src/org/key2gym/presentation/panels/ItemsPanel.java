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
package org.key2gym.presentation.panels;

import org.key2gym.business.ItemsService;
import org.key2gym.business.SessionsService;
import org.key2gym.business.StorageService;
import org.key2gym.business.dto.ItemDTO;
import org.key2gym.presentation.util.ItemsTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ItemsPanel extends javax.swing.JPanel {

    /**
     * Creates new ItemsPanel
     */
    public ItemsPanel() {
        observer = new ItemsObserver();
        itemsService = ItemsService.getInstance();
        
        initComponents();
        buildPanel();
    }

    /**
     * Initializes the panel's components.
     */
    private void initComponents() {
        
        itemsTable = new javax.swing.JTable();

        /*
         * Columns of the items table
         */
        ItemsTableModel.Column[] itemsTableColumns =
                new ItemsTableModel.Column[]{
            ItemsTableModel.Column.TITLE,
            ItemsTableModel.Column.QUANTITY,
            ItemsTableModel.Column.PRICE,
            ItemsTableModel.Column.BARCODE};
        
        itemsTableModel = new ItemsTableModel(itemsTableColumns);
        itemsTable.setModel(itemsTableModel);
        itemsTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        itemsScrollPane = new javax.swing.JScrollPane();
        itemsScrollPane.setViewportView(itemsTable);
    }
    
    /**
     * Builds the panel by placing the components on it.
     */
    private void buildPanel() {
        setLayout(new BorderLayout());
        add(itemsScrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Sets the items group to display.
     * @param items the new items group
     */
    public void setItems(ItemsGroup items) {
        this.itemsGroup = items;
        
        updatePanel();
    }
    
    private class ItemsObserver implements Observer {
        
        public ItemsObserver() {
            registerSelf();
        }        
        
        public void registerSelf() {
            StorageService.getInstance().addObserver(this);
        }
        
        @Override
        public void update(Observable o, Object arg) {
            if (SessionsService.getInstance().hasOpenSession()) {
                updatePanel();
            }
        }
    }
    
    private void updatePanel() {
        List<ItemDTO> items;
        
        if (itemsGroup.equals(ItemsGroup.ALL)) {
            items = itemsService.getAllItems();
        } else if (itemsGroup.equals(ItemsGroup.AVAILABLE)) {
            items = itemsService.getItemsAvailable();
        } else if (itemsGroup.equals(ItemsGroup.PURE)) {
            items = itemsService.getPureItems();
        } else {
            throw new IllegalStateException("This ItemsGroup has not been yet implemented.");
        }
        
        itemsTableModel.setItems(items);
    }
    
    /*
     * Business
     */
    private ItemsService itemsService;
    private ItemsObserver observer;
    /*
     * Presentation
     */
    private ItemsGroup itemsGroup;
    private ItemsTableModel itemsTableModel;
    private JScrollPane itemsScrollPane;
    private JTable itemsTable;
    
   public enum ItemsGroup {ALL, AVAILABLE, PURE};
}
