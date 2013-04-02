/*
 * Copyright 2012-2013 Danylo Vashchilenko
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

package org.key2gym.client.report.handlers;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import javax.swing.JTextArea;
import org.key2gym.client.MainFrame;
import org.key2gym.client.report.spi.ReportHandler;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.client.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class TextWorkspaceTabHandler implements ReportHandler {
    @Override
    public boolean isSupported(String format) {
        return format.equalsIgnoreCase("xml") || format.equalsIgnoreCase("html");
    }

    @Override
    public void handle(ReportDTO report, byte[] reportBody, String format) {

	JTextArea textArea;

        try {
            textArea = new JTextArea(new String(reportBody, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
	
	textArea.setLineWrap(true);

        MainFrame.getInstance().createTab(textArea, report.getTitle());
    }

    @Override
    public String getTitle() {
	return ResourcesManager.getStrings().getString("Text.TextHandler");
    }

}
