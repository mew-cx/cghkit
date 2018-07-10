/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.corticalcafe.utils;

import com.corticalcafe.primitives.DebugMessages;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author astein
 */
public class FTPsync {
    
    
    String ftpSite;
    String ftpUsername, ftpPassword;
    boolean isFTPrunning=false;
    String localDir;
    public static int FTPFREQ_DEFAULT=300;
    int ftpFreqSec=FTPFREQ_DEFAULT;

    public int getFtpFreqSec() {
        return ftpFreqSec;
    }

    public void setFtpFreqSec(int ftpFreqSec) {
        this.ftpFreqSec = ftpFreqSec;
    }

    public void setFtpSite(String ftpSite) {
        this.ftpSite = ftpSite;
    }

    public void setFtpUsername(String ftpUsername, String ftpPass) {
        this.ftpUsername = ftpUsername;
        this.ftpPassword= ftpPass;
    }

    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    boolean simulate=true;


    FTPClient client = new FTPClient();

    public void openFTPconnection()
    {
        try {
            client.connect(ftpSite);
            client.login(ftpUsername, ftpPassword);
        } catch (IllegalStateException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
 


    public void start()
    {
        class RunFTPsync extends Thread
        {
            RunFTPsync()
            {
            }

            @Override public void run()
            {
                openFTPconnection();

                do
                {
                  syncHologramFTPdirectory();

                  try
                  {
                      for (int i = 0; i < ftpFreqSec; i++)
                      {
                        Thread.sleep(1000);
                        if(!isFTPrunning)
                            break;
                      }

                  }
                  catch (InterruptedException ex)
                  {
                    ex.printStackTrace();
                  }
//                    }while(simulation.getCurrentRayNumber()<simulation.getTotalRayCount());
                }while(isFTPrunning());

                closeFTPconnection();
            }
        }

        setFTPrunning(true);
        RunFTPsync rFTP=new RunFTPsync();
        rFTP.start();
    }



    public boolean isFTPrunning() {
        return isFTPrunning;
    }


    public void setFTPrunning(boolean isFTPrunning) {
        this.isFTPrunning = isFTPrunning;
    }
    
    
    public void stop()
    {
        setFTPrunning(false);
    }


    public void closeFTPconnection()
    {
        if(client.isConnected())
        {
        try {
            client.disconnect(true);
        } catch (IllegalStateException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        }

        }
    }


    public void setLocalDir(String locDir)
    {
        File tmpFile=new File(locDir);

        if(!tmpFile.isDirectory())
        {
            locDir=MiscUtils.removeFilename(locDir);
        }

//        DebugMessages.inform("LocDir="+locDir);
        
        localDir=locDir;
    }



    //synchronizes the remote FTP directory and the local batch processing directory
    public void syncHologramFTPdirectory()
    {
        FTPFile[] remoteFiles=null;
        try {
            remoteFiles = client.list();
        } catch (IllegalStateException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPDataTransferException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPAbortedException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPListParseException ex) {
            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
        }

        File localFiles[]=new File(localDir).listFiles();


//        DebugMessages.inform("All Files");
//
//        for (int i = 0; i < localFiles.length; i++) {
//            if(localFiles[i]!=null)
//                DebugMessages.inform("LocalFile["+i+"]:  "+localFiles[i].toString());
//        }
//
//        for (int i = 0; i < remoteFiles.length; i++) {
//            if(remoteFiles[i]!=null)
//               DebugMessages.inform("RemoteFile["+i+"]:  "+remoteFiles[i].getName());
//        }


        for (int i = 0; i < localFiles.length; i++) {
            if(!localFiles[i].isFile() || (localFiles[i].getName().startsWith(".")
                && !localFiles[i].getName().contentEquals(".status") ) )
                    localFiles[i]=null;
        }


        for (int i = 0; i < remoteFiles.length; i++) {
            if(remoteFiles[i].getType()!=FTPFile.TYPE_FILE 
                || remoteFiles[i].getName().startsWith(".") )
                    remoteFiles[i]=null;
        }


        for (int i = 0; i < localFiles.length; i++)
        {
            for (int j = 0; j < remoteFiles.length; j++)
            {
                if(remoteFiles[j]!=null && localFiles[i]!=null)
                {
                    if(localFiles[i].getName().compareTo(remoteFiles[j].getName())==0
                            && localFiles[i].length()==remoteFiles[j].getSize() )
                    {
                        localFiles[i]=null;
                        remoteFiles[j]=null;
                    }
                }

            }
        }


//        DebugMessages.inform("Different Files");

        for (int i = 0; i < localFiles.length; i++)     //cp local to FTP
        {
            if(localFiles[i]!=null)
            {
                File src=new File(localFiles[i].toString());

                SimpleFileLock sfl=new SimpleFileLock(src);


                //only transfer files that are not locked
                if(sfl.getLock()==true)
                {

                    DebugMessages.informWithDate(src.toString()+" >>> "+src.toString());
                    if(!simulate)
                    {
                        try {
                            client.upload(src);
                            
                        } catch (IllegalStateException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPIllegalReplyException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPDataTransferException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPAbortedException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    sfl.unlock();
                }

                
//                File dst=new File(src.getName(), ftpSession);


            }
        }


        for (int i = 0; i < remoteFiles.length; i++)        //cp FTP to local
        {
            if(remoteFiles[i]!=null)
            {
                String src=remoteFiles[i].getName();

                File dst=new File(localDir+"/"+src);

                SimpleFileLock sfl=new SimpleFileLock(dst);

                if(sfl.getLock()==true)
                {
                    DebugMessages.informWithDate(dst.toString()+" <<< "+src.toString());
                    if(!simulate)
                    {
                        try {
                            client.download(src, dst);
                        } catch (IllegalStateException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPIllegalReplyException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPDataTransferException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FTPAbortedException ex) {
                            Logger.getLogger(FTPsync.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    sfl.unlock();
                }


            }
        }

    }
    

}
