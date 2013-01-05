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
package org.key2gym.client.dialogs;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.*;
import org.key2gym.client.resources.ResourcesManager;

/**
 * A dialog with support of i18n, common actions and results.
 * <p/>
 * Results tell the dialog's called what action the users chose. There are several
 * self-explanatory results: OK, CANCEL, CLOSE.
 * <p/>
 * The dialog sets the result to Result.CANCEL, when the close button (on the frame) is pressed.
 *
 * @author Danylo Vashchilenko
 */
public abstract class AbstractDialog extends JDialog {

    /**
     * Constructs with the specified parent and modality.
     *
     * @param parent the parent frame of this dialog
     * @param modal if true, the dialog will be modal
     */
    public AbstractDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        strings = ResourcesManager.getStrings();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cancelAction = new CancelAction();
        okAction = new OkAction();
        closeAction = new CloseAction();

        /*
         * Binds the escape key to the cancel action.
         */
        addHotKey(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelAction);

        result = null;
    }

    /**
     * Performs pre-closing routine.
     *
     * @param evt an optional WindowEvent
     * @see AbstractDialog
     */
    protected void formWindowClosing(java.awt.event.WindowEvent evt) {
        setResult(Result.CANCEL);
    }

    /**
     * Gets the l15d string for the key.
     *
     * @param key the key to look up the string
     * @return the l15d value
     */
    protected String getString(String key, Object... parameters) {
        return MessageFormat.format(strings.getString(key), parameters);
    }

    /**
     * Gets the dialog's strings bundle.
     *
     * @return the strings bundle
     */
    protected ResourceBundle getStrings() {
        return strings;
    }
    
    /**
     * Gets the dialog's result code.
     *
     * @return the result code
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the dialog's result code.
     *
     * @param result the result code
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Returns the dialog's cancel action.
     *
     * @return the action used to cancel the changes and close the dialog.
     */
    protected Action getCancelAction() {
        return cancelAction;
    }

    /**
     * Returns the dialog's OK action.
     *
     * @return the action used to confirm the changes and close the dialog.
     */
    protected Action getOkAction() {
        return okAction;
    }

    /**
     * Returns the dialog's close action.
     *
     * @return the action used to close the dialog.
     */
    protected Action getCloseAction() {
        return closeAction;
    }

    /**
     * Adds a dialog hot key.
     *
     * <p>
     *
     * As long as the dialog or its subcomponents has the focus, the triggering
     * of the key stroke will cause the action to be performed.
     *
     * @param keyStroke the key stroke to hook
     * @param action the action to perform when the key stroke is triggered
     */
    protected final void addHotKey(KeyStroke keyStroke, Action action) {
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, action.getClass().getName());
        getRootPane().getActionMap().put(action.getClass().getName(), action);
    }

    protected void onOkActionPerformed(ActionEvent evt) {
        setResult(Result.OK);
        dispose();
    }

    protected void onCancelActionPerformed(ActionEvent evt) {
        setResult(Result.CANCEL);
        dispose();
    }

    protected void onCloseActionPerformed(ActionEvent evt) {
        setResult(Result.CLOSE);
        dispose();
    }

    public class CancelAction extends AbstractAction {

        public CancelAction(AbstractDialog dialog) {
            putValue(NAME, getString("Button.Cancel"));
            putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/client/resources/cancel16.png")));
            this.dialog = dialog;
        }

        public CancelAction() {
            this(AbstractDialog.this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.onCancelActionPerformed(e);
        }
        private AbstractDialog dialog;
    }

    public class OkAction extends AbstractAction {

        public OkAction(AbstractDialog dialog) {
            putValue(NAME, getString("Button.Ok"));
            putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/client/resources/ok16.png")));
            this.dialog = dialog;
        }

        public OkAction() {
            this(AbstractDialog.this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.onOkActionPerformed(e);
        }
        private AbstractDialog dialog;
    }

    public class CloseAction extends AbstractAction {

        public CloseAction(AbstractDialog dialog) {
            putValue(NAME, getString("Button.Close"));
            putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/key2gym/client/resources/ok16.png")));
            this.dialog = dialog;
        }

        public CloseAction() {
            this(AbstractDialog.this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.onCloseActionPerformed(e);
        }
        private AbstractDialog dialog;
    }
    
    private Action cancelAction;
    private Action okAction;
    private Action closeAction;
    private Result result;
    private ResourceBundle strings;

    public enum Result {

        OK,
        CANCEL,
        CLOSE
    };
}
