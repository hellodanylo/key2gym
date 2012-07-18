/**
 * This file is part of Census.
 *
 * Census is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Census is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Census. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright © 2011 Daniel Vashchilenko
 */
package census;

import census.presentation.CensusFrame;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author danny
 *
 */
public class CensusStarter {

    /**
     * @param args
     */
    public static void main(String[] args) {


        // logger
        logger_ = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Locale.setDefault(new Locale("ru", "RU"));

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CensusFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Creates and display a CensusFrame.
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                CensusFrame.getInstance().setVisible(true);
            }
        });
    }
    
    public static Logger getLogger() {
        return logger_;
    }
    

    private static Logger logger_;
}
