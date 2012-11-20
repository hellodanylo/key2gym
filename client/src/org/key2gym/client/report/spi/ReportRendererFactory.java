/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.client.report.spi;

import java.awt.Component;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ReportRendererFactory {
    public boolean isSupported(String format);
    public Component create(byte[] report, String format);
}
