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
package org.key2gym.presentation.highlighters;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Danylo Vashchilenko
 */
public abstract class AbstractHighlighter {

    public AbstractHighlighter(JTextField textField) {
        this.textField = textField;

        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateHighlight();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateHighlight();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void updateHighlight() {
        ColorScheme highlightModel = getHighlightModelFor(textField.getText());

        textField.setBackground(highlightModel.getBackground());
        textField.setForeground(highlightModel.getForeground());
    }

    protected abstract ColorScheme getHighlightModelFor(String text);

    protected static final class ColorScheme {

        public ColorScheme(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }

        public Color getBackground() {
            return background;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public Color getForeground() {
            return foreground;
        }

        public void setForeground(Color foreground) {
            this.foreground = foreground;
        }
        private Color background;
        private Color foreground;
    }
    protected static final ColorScheme NULL_SCHEME = new ColorScheme(
            Color.white,
            Color.black);
    private JTextField textField;
}
