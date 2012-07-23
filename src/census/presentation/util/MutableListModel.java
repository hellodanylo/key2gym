/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package census.presentation.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Danylo Vashchilenko
 */
public class MutableListModel<T> extends AbstractListModel<T> {
    
    public MutableListModel() {
        list = new ArrayList<>();
    }
    
    public void set(List<T> list) {
        this.list = list;
        fireContentsChanged(this, 0, list.size()-1);
    }
    
    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T getElementAt(int index) {
        return list.get(index);
    }
    
    private List<T> list;

}
