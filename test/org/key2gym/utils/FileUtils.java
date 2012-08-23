/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.key2gym.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import org.apache.log4j.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class FileUtils {

    public static String readFile(String path) {
        try (FileInputStream stream = new FileInputStream(new File(path))) {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /*
             * Instead of using default, pass in a decoder.
             */
            return Charset.defaultCharset().decode(bb).toString();
        } catch(IOException ex) {
            Logger.getLogger(FileUtils.class).fatal("IOException: " + System.getProperty("user.dir"), ex);
            return null;
        }
    }
}
