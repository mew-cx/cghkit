package com.corticalcafe.primitives;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Primitive but useful class for outputting runtime messages at various levels, controlling debug output, etc.
 * Can be called statically, for example:<br>
 * <br>
 * <b>
 *    DebugMessages.error("Trying to remove root node");<br>
 * </b><br>
 * Can change current verbosity level at any time during run time, as in:<br>
 * <br>
 * <b>
 *    DebugMessages.setMessageLevel(DebugMessages.MESSAGES_OFF);<br>
 * </b><br>
 * <p>Title: DataLogger</p>
 * <p>Description: Generic Data Logging Package</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */

public class DebugMessages
{

  /**
   * INFORM message level (most verbose)
   */
  public final static int MESSAGES_INFORM=1;
  /**
   * DEBUG message level (medium verbosity)
   */
  public final static int MESSAGES_DEBUG=2;
  /**
   * ERROR message level (minimumally verbose)
   */
  public final static int MESSAGES_ERROR=3;
  /**
   * silence all messages
   */
  public final static int MESSAGES_OFF=4;

  static int currentDBGlevel=MESSAGES_INFORM;

  public DebugMessages()
  {
  }

  /**
   * sets message level to the lowest level message to get printed
   * @param msgLev message level (use see class field defs)
   */
  public static void setMessageLevel(int msgLev)
  {
    currentDBGlevel=msgLev;
  }

  /**
   * get current message level
   * @return
   */
  public static int getMessageLevel()
  {
    return currentDBGlevel;
  }

  /**
   * output a string at INFORM message level
   * @param str
   */
  public static void inform(String str)
  {
    printMessage(str,MESSAGES_INFORM);
  }


  /**
   * output a string at INFORM message level
   * @param str
   */
  public static void informWithDate(String str)
  {
    printMessage(getTimestamp()+":  "+str,MESSAGES_INFORM);
  }

  


  public static String getTimestamp() {
    final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());
  }


  /**
   * output a string at DEBUG message level
   * @param str
   */
  public static void debug(String str)
  {
    printMessage(str,MESSAGES_DEBUG);
  }

  /**
   * output a string at ERROR message level
   * @param str
   */
  public static void error(String str)
  {
    printMessage(str,MESSAGES_ERROR);
  }

  /**
   * actual output to System.out device
   * @param str
   * @param msgDbgLevel
   */
  static void printMessage(String str, int msgDbgLevel)
  {
    if(msgDbgLevel>=currentDBGlevel)
      System.out.print(str+"\n");
  }

  /**
   * returns calling method
   * @return
   */
  public static String getCallingMethod()
  {
    return new Exception().getStackTrace()[1].getMethodName();
  }

  /**
   * returns calling class (includes inheritance tree)
   * @return
   */
  public static String getCallingClass()
  {
    return new Exception().getStackTrace()[1].getClassName();
  }

  /**
   * returns fully qualified methodname (class + method)
   * @return
   */
  public static String getFullyQualifiedCallingMethod()
  {
    Exception e=new Exception();
    StackTraceElement s=e.getStackTrace()[1];

    return s.getClassName() +"." + s.getMethodName();
  }

}