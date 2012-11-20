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

package org.key2gym.client.report.renderers;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import javax.swing.JTextArea;
import org.key2gym.client.report.spi.ReportRendererFactory;

/**
 *
 * @author Danylo Vashchilenko
 */
public class XMLRendererFactory implements ReportRendererFactory {
    @Override
    public boolean isSupported(String format) {
        return format.equalsIgnoreCase("xml");
    }

    @Override
    public Component create(byte[] report, String format) {
        try {
            JTextArea textArea = new JTextArea(new String(report, "UTF-8"));
            textArea.setLineWrap(true);
            return textArea;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
