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
package census.presentation.forms;

import census.business.AdSourcesService;
import census.business.dto.ClientProfileDTO;
import census.business.dto.ClientProfileDTO.FitnessExperience;
import census.business.dto.ClientProfileDTO.Sex;
import census.persistence.AdSource;
import census.presentation.util.CensusBindingListener;
import census.presentation.util.DateMidnightToStringConverter;
import census.presentation.util.FitnessExperienceListCellRenderer;
import census.presentation.util.SexListCellRenderer;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jdesktop.beansbinding.*;

/**
 *
 * @author Danylo Vashchilenko
 */
public class ClientProfileForm extends JPanel {

    /**
     * Creates new form ClientProfileForm
     */
    public ClientProfileForm() {
        adSourcesService = AdSourcesService.getInstance();
        adSources = adSourcesService.getAdSources();

        initComponents();
        buildForm();
    }

    /**
     * Initializes the form's components.
     */
    private void initComponents() {

        birthdayTextField = new JTextField();

        /*
         * Height
         */
        heightSpinner = new JSpinner();
        heightSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 175), Short.valueOf((short) 0), Short.valueOf((short) 400), Short.valueOf((short) 5)));

        /*
         * Weight
         */
        weightSpinner = new JSpinner();
        weightSpinner.setModel(new SpinnerNumberModel(Short.valueOf((short) 70), Short.valueOf((short) 0), Short.valueOf((short) 400), Short.valueOf((short) 5)));

        addressTextField = new JTextField();
        telephoneTextField = new JTextField();
        goalTextField = new JTextField();
        possibleAttendanceRateTextField = new JTextField();
        healthRestrictionsTextField = new JTextField();
        favouriteSportTextField = new JTextField();
        specialWishesTextField = new JTextField();

        /*
         * Ad sources.
         */
        adSourceComboBox = new JComboBox();
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

        /*
         * Sex
         */
        sexComboBox = new JComboBox();
        sexComboBox.setModel(new DefaultComboBoxModel(new Sex[]{Sex.UNKNOWN, Sex.MALE, Sex.FEMALE}));
        sexComboBox.setRenderer(new SexListCellRenderer());

        /*
         * Fitness experience
         */
        fitnessExperienceComboBox = new JComboBox();
        fitnessExperienceComboBox.setModel(new DefaultComboBoxModel(new FitnessExperience[]{FitnessExperience.NO, FitnessExperience.YES, FitnessExperience.UNKNOWN}));
        fitnessExperienceComboBox.setRenderer(new FitnessExperienceListCellRenderer());

    }

    /**
     * Builds the form by placing the components on it.
     */
    private void buildForm() {
        FormLayout layout = new FormLayout("right:default, 4dlu, default:grow", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, bundle, this);

        builder.appendI15d("Label.Sex", sexComboBox);
        builder.nextLine();

        builder.appendI15d("Label.Birthday", birthdayTextField);
        builder.nextLine();

        builder.appendI15d("Label.Address", addressTextField);
        builder.nextLine();

        builder.appendI15d("Label.Telephone", telephoneTextField);
        builder.nextLine();

        builder.appendI15d("Label.Goal", goalTextField);
        builder.nextLine();

        builder.appendI15d("Label.PossibleAttendanceRate", possibleAttendanceRateTextField);
        builder.nextLine();

        builder.appendI15d("Label.HealthRestrictions", healthRestrictionsTextField);
        builder.nextLine();

        builder.appendI15d("Label.FitnessExperience", fitnessExperienceComboBox);
        builder.nextLine();

        builder.appendI15d("Label.SpecialWishes", specialWishesTextField);
        builder.nextLine();

        builder.appendI15d("Label.Weight", weightSpinner);
        builder.nextLine();

        builder.appendI15d("Label.Height", heightSpinner);
        builder.nextLine();

        builder.appendI15d("Label.AdSource", adSourceComboBox);
        builder.nextLine();
    }

    /**
     * Tries to save the form to the current profile.
     *
     * @return true, if the form is valid and has been saved
     */
    public Boolean trySave() {
        for (Binding binding : bindingGroup.getBindings()) {
            binding.saveAndNotify();
        }
        return censusBindingListener.getInvalidTargets().isEmpty();
    }

    /**
     * Sets current profile.
     *
     * @param profile the new profile
     */
    public void setClientProfile(ClientProfileDTO clientProfile) {
        this.clientProfile = clientProfile;

        if (bindingGroup == null) {
            bindingGroup = new BindingGroup();
            censusBindingListener = new CensusBindingListener();

            Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("address"), addressTextField, BeanProperty.create("text"), "address"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("telephone"), telephoneTextField, BeanProperty.create("text"), "telephone"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("sex"), sexComboBox, BeanProperty.create("selectedItem"), "sex"); //NOI18N
            binding.setSourceNullValue(2);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("birthday"), birthdayTextField, BeanProperty.create("text"), "birthday"); //NOI18N
            binding.setConverter(new DateMidnightToStringConverter("date", "dd-MM-yyyy")); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("goal"), goalTextField, BeanProperty.create("text"), "goal"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("possibleAttendanceRate"), possibleAttendanceRateTextField, BeanProperty.create("text"), "possibleAttendanceRate"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("healthRestrictions"), healthRestrictionsTextField, BeanProperty.create("text"), "healthRestrictions"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("specialWishes"), specialWishesTextField, BeanProperty.create("text"), "specialWishes"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("favouriteSport"), favouriteSportTextField, BeanProperty.create("text"), "favouriteSport"); //NOI18N
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("fitnessExperience"), fitnessExperienceComboBox, BeanProperty.create("selectedItem"), "fitnessExperience"); //NOI18N
            binding.setSourceNullValue(0);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("height"), heightSpinner, BeanProperty.create("value"), "height"); //NOI18N
            binding.setSourceNullValue((short) 175);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("weight"), weightSpinner, BeanProperty.create("value"), "weight"); //NOI18N
            binding.setSourceNullValue((short) 70);
            bindingGroup.addBinding(binding);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, clientProfile,
                    BeanProperty.create("adSourceId"), adSourceComboBox, BeanProperty.create("selectedItem"), "adSource"); //NOI18N
            binding.setSourceNullValue(0);
            binding.setConverter(new Converter<Short, AdSource>() {

                @Override
                public AdSource convertForward(Short value) {
                    for (AdSource adSource : adSources) {
                        if (adSource.getId().equals(value)) {
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

            bindingGroup.addBindingListener(censusBindingListener);
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
    private JComboBox adSourceComboBox;
    private JTextField addressTextField;
    private JTextField birthdayTextField;
    private JTextField favouriteSportTextField;
    private JComboBox fitnessExperienceComboBox;
    private JTextField goalTextField;
    private JTextField healthRestrictionsTextField;
    private JSpinner heightSpinner;
    private JTextField possibleAttendanceRateTextField;
    private JComboBox sexComboBox;
    private JTextField specialWishesTextField;
    private JTextField telephoneTextField;
    private JSpinner weightSpinner;
}