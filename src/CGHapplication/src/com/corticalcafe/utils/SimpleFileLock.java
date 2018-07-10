/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.corticalcafe.utils;

import com.corticalcafe.primitives.DebugMessages;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *The most basic of File Locking Routines
 * @author astein
 */
public class SimpleFileLock {

        FileChannel channel;
        FileLock fl;
        String fname;


        /**
         * Init file lock
         * @param file file to lock
         */
        public SimpleFileLock(File file)
        {
            fname=file.getName();
//            DebugMessages.inform("SimpleFilelock:  "+fname);
            
            try {
                channel = new RandomAccessFile(file, "rw").getChannel();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimpleFileLock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



        /**
         * check if locked or get a lock
         * @return lock successfully obtained
         */
        public boolean getLock()
        {
//            DebugMessages.inform("Lock:  "+fname);

            try {
                fl = channel.tryLock();
            } catch (IOException ex) {
                Logger.getLogger(SimpleFileLock.class.getName()).log(Level.SEVERE, null, ex);
            } catch (OverlappingFileLockException ex) {
                DebugMessages.inform("Overlapping Lock:  "+fname);
            }

            return(fl!=null);
        }



        /**
         * check if locked
         * @return is locked
         */
        public boolean isLocked()
        {
//            DebugMessages.inform("IsLocked:  "+fname);

            boolean retVal=getLock();

            unlock();

            return(retVal);
        }


        /**
         * unlock file
         */
        public void unlock()
        {
//            DebugMessages.inform("Unlock:  "+fname);

            try {
                fl.release();
                channel.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleFileLock.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

}

