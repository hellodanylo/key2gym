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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.key2gym.business.api.ValidationException;
import org.key2gym.business.api.dtos.ReportDTO;
import org.key2gym.client.UserExceptionHandler;
import org.key2gym.client.report.spi.ReportHandler;
import org.key2gym.client.resources.ResourcesManager;

/**
 *
 * @author Danylo Vashchilenko
 */
public class BrowserHandler implements ReportHandler {
    @Override
    public boolean isSupported(String format) {
        return format.equalsIgnoreCase("html") || format.equalsIgnoreCase("xml");
    }

    @Override
    public void handle(ReportDTO report, byte[] reportBody, String format) {
	
	File file = null;
	/* Writes the report to the temporary file. */
	try {
	    file = File.createTempFile("key2gym-report-"+report.getId()+"-"+new java.util.Random().nextInt(), ".tmp");
	    OutputStream stream = new FileOutputStream(file);
	   
	    stream.write(reportBody);

	    stream.close();

	    if(Desktop.isDesktopSupported()) {
		try {
		    Desktop.getDesktop().browse(file.toURI());
		} catch (IOException ex) {
		    throw new RuntimeException(ex);
		}
	    } else {
		UserExceptionHandler.getInstance().processException(new ValidationException(ResourcesManager.getStrings()
											    .getString("Message.ReportHandlerNotSupported")));
	    }
	} catch(IOException ex) {
	    throw new RuntimeException(ex);
	}
    }	

    @Override
    public String getTitle() {
	return ResourcesManager.getStrings().getString("Text.BrowserHandler");
    }
}
