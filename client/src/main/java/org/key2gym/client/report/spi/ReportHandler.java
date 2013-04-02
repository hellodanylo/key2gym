/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.key2gym.client.report.spi;

import java.awt.Component;
import org.key2gym.business.api.dtos.ReportDTO;

/**
 *
 * @author Danylo Vashchilenko
 */
public interface ReportHandler {
    public boolean isSupported(String format);
    public void handle(ReportDTO report, byte[] reportBody, String format);
    public String getTitle();
}
