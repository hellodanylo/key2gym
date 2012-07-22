/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.blocks;

import census.presentation.util.DateMidnightToStringConverter;
import census.business.AdSourcesService;
import census.business.dto.ClientProfileDTO;
import census.business.dto.ClientProfileDTO.FitnessExperience;
import census.persistence.AdSource;
import census.business.dto.ClientProfileDTO.Sex;
import census.presentation.util.CensusBindingListener;
import census.presentation.util.FitnessExperienceListCellRenderer;
import census.presentation.util.SexListCellRenderer;
import java.awt.Component;
import java.beans.Beans;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author daniel
 */
public class ClientProfilePanel extends javax.swing.JPanel {
    /*
     * Business
     */
    private ClientProfileDTO clientProfile;
    private AdSourcesService adSourcesService;
    /*
     * Presentation
     */
    private List<AdSource> adSources;
    private BindingGroup bindingGroup;
    private CensusBindingListener censusBindingListener;
    private ResourceBundle bundle = ResourceBundle.getBundle("census/presentation/resources/Strings");

    /**
     * Creates new form ClientProfilePanel
     */
    public ClientProfilePanel() {
        if (!Beans.isDesignTime()) {
            adSourcesService = AdSourcesService.getInstance();
            adSources = adSourcesService.getAdSources();
        }

        initComponents();

        if (!Beans.isDesignTime()) {
            adSourceComboBox.setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(
                        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof AdSource) {
                        AdSource adSource = (AdSource) value;
                        setText(adSource.getTitle());
                    }
                    return this;
                }
            });
            
            adSourceComboBox.setModel(new DefaultComboBoxModel(adSources.toArray()));
            adSourceComboBox.setSelectedIndex(0);
        }
    }

    private void save() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
    }

    public Boolean isFormValid() {
        save();
        return censusBindingListener.getInvalidTargets().isEmpty();
    }

    /**
     * Sets current profile.
     *
     * @param profile New profile
     */
    public void setClientProfile(ClientProfileDTO clientProfile) {
        this.clientProfile = clientProfile;

        if (bindingGroup == null) {
            bindingGroup = new BindingGroup();
            censusBindingListener = new CensusBindingListener();

            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("address"), addressTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "address"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("telephone"), telephoneTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "telephone"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("sex"), sexComboBox, BeanProperty.create("selectedItem"), "sex"); //NOI18N
            binding.setSourceNullValue(2);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("birthday"), birthdayTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "birthday"); //NOI18N
            binding.setConverter(new DateMidnightToStringConverter("date", "dd-MM-yyyy")); //NOI18N
            binding.addBindingListener(censusBindingListener);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("goal"), goalTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "goal"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("possibleAttendanceRate"), possibleAttendanceRateTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "possibleAttendanceRate"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("healthRestrictions"), healthRestrictionsTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "healthRestrictions"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("specialWishes"), specialWishesTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "specialWishes"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("favouriteSport"), favouriteSportTextField, BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "favouriteSport"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("fitnessExperience"), fintessExperienceComboBox, BeanProperty.create("selectedItem"), "fitnessExperience"); //NOI18N
            binding.setSourceNullValue(0);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("height"), heightSpinner, BeanProperty.create("value"), "height"); //NOI18N
            binding.setSourceNullValue((short) 175);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("weight"), weightSpinner, BeanProperty.create("value"), "weight"); //NOI18N
            binding.setSourceNullValue((short) 70);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, clientProfile,
                    BeanProperty.create("adSourceId"), adSourceComboBox, BeanProperty.create("selectedItem"), "adSource"); //NOI18N
            binding.setSourceNullValue(0);
            binding.setConverter(new Converter<Short, AdSource>() {

                @Override
                public AdSource convertForward(Short value) {
                    for(AdSource adSource : adSources) {
                        if(adSource.getId().equals(value)) {
                            return adSource;
                        }
                    }
                    
                    return null;
                }

                @Override
                public Short convertReverse(AdSource value) {
                    return value.getId();
                }
            });

            bindingGroup.addBinding(binding);

            bindingGroup.bind();
        } else {

            censusBindingListener.getInvalidTargets().clear();

            /*
             * We take each binding and set the source object.
             */
            for (Binding binding : bindingGroup.getBindings()) {
                binding.unbind();
                binding.setSourceObject(clientProfile);
                binding.bind();
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sexButtonGroup = new javax.swing.ButtonGroup();
        fitnessExperienceButtonGroup = new javax.swing.ButtonGroup();
        birthdayTextField = new javax.swing.JTextField();
        specialWishesLabel = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        heightSpinner = new javax.swing.JSpinner();
        fintessExperienceLabel = new javax.swing.JLabel();
        weightSpinner = new javax.swing.JSpinner();
        weightLabel = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        telephoneTextField = new javax.swing.JTextField();
        goalTextField = new javax.swing.JTextField();
        possibleAttendanceRateTextField = new javax.swing.JTextField();
        healthRestrictionsTextField = new javax.swing.JTextField();
        favouriteSportTextField = new javax.swing.JTextField();
        specialWishesTextField = new javax.swing.JTextField();
        telephone = new javax.swing.JLabel();
        goalLabel = new javax.swing.JLabel();
        favoriteSportLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        birthdayLabel = new javax.swing.JLabel();
        possibleAttendanceRateLabel = new javax.swing.JLabel();
        healthRestrictionsLabel = new javax.swing.JLabel();
        adSourceComboBox = new javax.swing.JComboBox();
        adSourceLabel = new javax.swing.JLabel();
        sexLabel = new javax.swing.JLabel();
        sexComboBox = new javax.swing.JComboBox();
        fintessExperienceComboBox = new javax.swing.JComboBox();

        specialWishesLabel.setText(bundle.getString("Label.SpecialWishes")); // NOI18N

        heightLabel.setText(bundle.getString("Label.Height")); // NOI18N

        heightSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)175), Short.valueOf((short)0), Short.valueOf((short)400), Short.valueOf((short)5)));

        fintessExperienceLabel.setText(bundle.getString("Label.FitnessExperience")); // NOI18N

        weightSpinner.setModel(new javax.swing.SpinnerNumberModel(Short.valueOf((short)70), Short.valueOf((short)0), Short.valueOf((short)400), Short.valueOf((short)5)));

        weightLabel.setText(bundle.getString("Label.Weight")); // NOI18N

        telephone.setText(bundle.getString("Label.Telephone")); // NOI18N

        goalLabel.setText(bundle.getString("Label.Goal")); // NOI18N

        favoriteSportLabel.setText(bundle.getString("Label.FavouriteSport")); // NOI18N

        addressLabel.setText(bundle.getString("Label.Address")); // NOI18N

        birthdayLabel.setText(bundle.getString("Label.Birthday")); // NOI18N

        possibleAttendanceRateLabel.setText(bundle.getString("Label.PossibleAttendanceRate")); // NOI18N

        healthRestrictionsLabel.setText(bundle.getString("Label.HealthRestrictions")); // NOI18N

        adSourceLabel.setText(bundle.getString("Label.AdSource")); // NOI18N

        sexLabel.setText(bundle.getString("Label.Sex")); // NOI18N

        sexComboBox.setModel(new DefaultComboBoxModel(new Sex[]{Sex.UNKNOWN, Sex.MALE, Sex.FEMALE}));
        sexComboBox.setRenderer(new SexListCellRenderer());

        fintessExperienceComboBox.setModel(new javax.swing.DefaultComboBoxModel(new FitnessExperience[]{FitnessExperience.NO, FitnessExperience.YES, FitnessExperience.UNKNOWN}));
        fintessExperienceComboBox.setRenderer(new FitnessExperienceListCellRenderer());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(telephone)
                    .addComponent(goalLabel)
                    .addComponent(favoriteSportLabel)
                    .addComponent(addressLabel)
                    .addComponent(birthdayLabel)
                    .addComponent(possibleAttendanceRateLabel)
                    .addComponent(healthRestrictionsLabel)
                    .addComponent(specialWishesLabel)
                    .addComponent(heightLabel)
                    .addComponent(fintessExperienceLabel)
                    .addComponent(weightLabel)
                    .addComponent(adSourceLabel)
                    .addComponent(sexLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(healthRestrictionsTextField)
                    .addComponent(possibleAttendanceRateTextField)
                    .addComponent(goalTextField)
                    .addComponent(telephoneTextField)
                    .addComponent(addressTextField)
                    .addComponent(heightSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(birthdayTextField)
                    .addComponent(favouriteSportTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(weightSpinner)
                    .addComponent(specialWishesTextField)
                    .addComponent(adSourceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sexComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fintessExperienceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sexLabel)
                    .addComponent(sexComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(birthdayLabel)
                    .addComponent(birthdayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressLabel)
                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telephone)
                    .addComponent(telephoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goalLabel)
                    .addComponent(goalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(possibleAttendanceRateLabel)
                    .addComponent(possibleAttendanceRateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(healthRestrictionsLabel)
                    .addComponent(healthRestrictionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(favoriteSportLabel)
                    .addComponent(favouriteSportTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fintessExperienceLabel)
                    .addComponent(fintessExperienceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(specialWishesLabel)
                    .addComponent(specialWishesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightLabel)
                    .addComponent(heightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weightLabel)
                    .addComponent(weightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adSourceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adSourceLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox adSourceComboBox;
    private javax.swing.JLabel adSourceLabel;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JLabel birthdayLabel;
    private javax.swing.JTextField birthdayTextField;
    private javax.swing.JLabel favoriteSportLabel;
    private javax.swing.JTextField favouriteSportTextField;
    private javax.swing.JComboBox fintessExperienceComboBox;
    private javax.swing.JLabel fintessExperienceLabel;
    private javax.swing.ButtonGroup fitnessExperienceButtonGroup;
    private javax.swing.JLabel goalLabel;
    private javax.swing.JTextField goalTextField;
    private javax.swing.JLabel healthRestrictionsLabel;
    private javax.swing.JTextField healthRestrictionsTextField;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JSpinner heightSpinner;
    private javax.swing.JLabel possibleAttendanceRateLabel;
    private javax.swing.JTextField possibleAttendanceRateTextField;
    private javax.swing.ButtonGroup sexButtonGroup;
    private javax.swing.JComboBox sexComboBox;
    private javax.swing.JLabel sexLabel;
    private javax.swing.JLabel specialWishesLabel;
    private javax.swing.JTextField specialWishesTextField;
    private javax.swing.JLabel telephone;
    private javax.swing.JTextField telephoneTextField;
    private javax.swing.JLabel weightLabel;
    private javax.swing.JSpinner weightSpinner;
    // End of variables declaration//GEN-END:variables
}
