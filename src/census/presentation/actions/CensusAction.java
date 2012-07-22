/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.presentation.actions;

import census.presentation.CensusFrame;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Custom generic class for actions
 *
 * @author Danylo Vashchilenko
 *
 */
public abstract class CensusAction extends AbstractAction implements Observer {
    
    public CensusAction() {
        
    }
    
    public void setText(String text) {
        putValue(AbstractAction.NAME, text);
    }
    
    public void setIcon(Icon icon) {
        putValue(AbstractAction.LARGE_ICON_KEY, icon);
    }
    
    public void setAccelerationKey(KeyStroke key) {
        putValue(AbstractAction.ACCELERATOR_KEY, key);
    }
    
    public void setSelected(Boolean selected) {
        putValue(AbstractAction.SELECTED_KEY, selected);
    }
    
    protected CensusFrame getFrame() {
        return CensusFrame.getInstance();
    }
    
    public static final String ACTION_GLOBAL = "ACTION_GLOBAL";
    public static final String ACTION_CONTEXT = "ACTION_CONTEXT";
}
