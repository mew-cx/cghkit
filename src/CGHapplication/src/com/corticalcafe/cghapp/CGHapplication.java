/*
 *
 * The CorticalCafe Computer Generated Hologram (CGH) Construction Kit
 * (C)2010 Alan Stein
 * www.corticalcafe.com
 *
 * Code released under the GPL v3.
 * http://www.gnu.org/licenses/gpl.html
 *
 * Software freedom is about:
 *    - the freedom to use software for any purpose,
 *    - the freedom to change software to suit your needs,
 *    - the freedom to share software with your friends and neighbors, and
 *    - the freedom to share the changes you make.
 *
 * Take the time to learn about, use, and support free software.
 * If you don't, who will?
 *
 */

package com.corticalcafe.cghapp;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CGHapplication extends SingleFrameApplication {

    static String clArgs[];

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        CGHapplicationView cghAppView=new CGHapplicationView(this);
//        cghAppView.setFile(clArgs[1]);        //can pass a file from the command line

        if(clArgs.length>0)
            cghAppView.setFile(clArgs[0]);

        if(clArgs.length>1)
        {
            cghAppView.setScaleObject(true);
            cghAppView.setCenterObject(true);
            cghAppView.startBatchAndFTPprocessing();
        }

        show(cghAppView);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of CGHapplication
     */
    public static CGHapplication getApplication() {
        return Application.getInstance(CGHapplication.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

        clArgs=args;

        for (int i = 0; i < args.length; i++)
        {
            System.out.println("args[" + i + "]: "
            + args[i]);
        }
        
        launch(CGHapplication.class, args);
    }
}
