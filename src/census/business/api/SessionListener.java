/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census.business.api;

/**
 *
 * @author daniel
 */
public interface SessionListener {
    public void sessionOpened();
    public void sessionClosed();
    public void sessionUpdated();
}
