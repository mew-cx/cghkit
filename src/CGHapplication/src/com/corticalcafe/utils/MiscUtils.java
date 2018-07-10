package com.corticalcafe.utils;

import com.corticalcafe.primitives.DebugMessages;
import org.jdom.*;
import java.util.*;

import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

import java.text.*;

import com.sun.image.codec.jpeg.*;

/**
 * Misc small routines which are useful
 * <p>Title: DataLogger</p>
 * <p>Description: Generic Data Logging Package</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */
public class MiscUtils
{

  /** Given a string, this method replaces all occurrences of
   *  '<' with '&lt;', all occurrences of '>' with
   *  '&gt;', and (to handle cases that occur inside attribute
   *  values), all occurrences of double quotes with
   *  '&quot;' and all occurrences of '&' with '&amp;'.
   *  Without such filtering, an arbitrary string
   *  could not safely be inserted in a Web page.
   * @param input 
   * @return
   */
  public static String filterEntities(String input) {
    StringBuffer filtered = new StringBuffer(input.length());
    char c;
    for(int i=0; i<input.length(); i++) {
      c = input.charAt(i);
      if (c == '<') {
        filtered.append("&lt;");
      } else if (c == '>') {
        filtered.append("&gt;");
      } else if (c == '"') {
        filtered.append("&quot;");
      } else if (c == '&') {
        filtered.append("&amp;");
      } else {
        filtered.append(c);
      }
    }
    return(filtered.toString());
  }

  /**
   *
   * @param rootElem
   * @return
   */
  public static Document createDocument(Element rootElem)
  {
    Document doc=new Document(rootElem);

    return doc;
  }


  /**
   *
   * @param line
   * @return
   */
  public static String[] parseCSVline(String line)
  {
    Vector retVals=new Vector();

    StringTokenizer st=new StringTokenizer(line, ",");

    while (st.hasMoreTokens())
    {
      retVals.add(st.nextToken());
    }

    String retStrArr[]=new String[retVals.size()];

    for (int i = 0; i < retStrArr.length; i++)
    {
      retStrArr[i]=(String)retVals.elementAt(i);
      retStrArr[i]=retStrArr[i].replace('\"',' '); //strip out any quote chars
      retStrArr[i]=retStrArr[i].trim();
    }
    return retStrArr;
  }



  /**
   * Pads a numeric output to a fixed number of digits
   * @param num
   * @param numDecPlaces
   * @return
   */
  public static String padNumber(int num, int numDecPlaces)
  {
      String str="";

      int numPads=numDecPlaces-String.valueOf(num).length();

      for (int i = 0; i < numPads; i++) {
          str+="0";

      }

      str+=String.valueOf(num);
      return str;
  }


  /**
   * left justify a string and wordwrap.  Useful for quickly formatting
   * text for a JOptionpane dialog
   * @param width - columns of text
   * @param st - string to format
   * @return - formatted text string
   */
  public static String leftJustifyWordWrap(int width, String st)
  {
      StringBuffer buf = new StringBuffer(st);
      int lastspace = -1;
      int linestart = 0;
      int i = 0;

      while (i < buf.length())
      {
         if ( buf.charAt(i) == ' ' ) lastspace = i;
         if ( buf.charAt(i) == '\n' )
          {
            lastspace = -1;
            linestart = i+1;
            }
         if (i > linestart + width - 1 )
         {
            if (lastspace != -1)
              {
               buf.setCharAt(lastspace,'\n');
               linestart = lastspace+1;
               lastspace = -1;
               }
            else
              {
               buf.insert(i,'\n');
               linestart = i+1;
              }
            }
          i++;
      }
      return buf.toString();
   }


  public static String formatGIF="GIF";
  public static String formatPNG="PNG";

   /**
    * write an image out to a file as a GIF
    * @param img image
    * @param formatStr output format (see MiscUtil fields)
    * @param gifFile output file
    */
   public static void writeImage(Image img, String formatStr, File gifFile)
   {
      try
      {
        BufferedOutputStream bos = new BufferedOutputStream(new
            FileOutputStream(gifFile));

        ImageIO.write((BufferedImage)img, formatStr, bos);

        bos.flush();

        bos.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
   }


   /**
    * write an image out to a file as a JPEG
    * @param img image
    * @param jpgFile output file
    */
   public static void writeImageToJPEG(Image img, File jpgFile)
   {
     writeImageToJPEG(img, jpgFile, 0.85);
   }


   /**
    * write an image out to a file as a JPEG
    * @param img image
    * @param jpgFile output file
    * @param quality quality of compressed image (0.0 [smallest poorest quality] to 1.0 [no compression])
    */
   public static void writeImageToJPEG(Image img, File jpgFile, double quality)
   {
     if(img==null || img.getHeight(null)==0 || img.getWidth(null)==0)
       return;

     try
     {
       BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(jpgFile));
//       JPEGImageEncoder jpgEnc=JPEGCodec.createJPEGEncoder(bos);
//
//       JPEGEncodeParam param=JPEGCodec.getDefaultJPEGEncodeParam((BufferedImage)img);
//       param.setQuality((float)quality, false);
//
//       jpgEnc.encode((BufferedImage)img, param);
     }
     catch (FileNotFoundException ex)
     {
       ex.printStackTrace();
     }
     catch (IOException ex)
     {
       ex.printStackTrace();
     }
   }


   /**
    * map byte (0-255) to grayscale RGB space (0 - 255^16 + 255^8 + 255)
    * @param byteValue
    * @return RGB pixel value
    */
   public static int mapByteToRGB(int byteValue)
   {
     return ((byteValue<<16) + (byteValue<<8) + byteValue);
   }

   /**
    * map byte (0-255) to grayscale RGB space (0 - 255^16 + 255^8 + 255),
    * assumes that pixel is completely opaque (alpha=255)
    * @param byteValue
    * @return RGB pixel value
    */
   public static int mapByteToARGB(int byteValue)
   {
     final int alpha=255;
     return ((alpha<<24)+(byteValue<<16) + (byteValue<<8) + byteValue);
   }

   /**
    * There is no one "correct" conversion from RGB to grayscale, since it depends on the sensitivity response curve of your detector to light as a function of wavelength. A common one in use is:
    *
    * Y = 0.3*R + 0.59*G + 0.11*B
    *
    * or assume all components equal value (perhaps already grayscale)
    *
    * map RGB space (0 - 255^16 + 255^8 + 255) to byte (0-255) grayscale
    * @param rgbValue
    * @return byte pixel value
    */
   public static int mapRGBtoByte(int rgbValue)
   {
     int b=rgbValue&0xFF;
     int g=(rgbValue>>8)&0xFF;
     int r=(rgbValue>>16)&0xFF;
//     int retVal=(int)(0.3*r+0.59*g+0.11*b);
     int retVal=(int)((r+g+b)/3.0);

//     System.out.println("rgb="+rgbValue+" r="+r+" g="+g+" b="+b+" byte="+retVal);
     return retVal;
   }


   /**
    * map byte to binary using threshold of 128
    * @param rgbIn
    * @return
    */
   public static int mapByteToBinary(int rgbIn)
   {
     return mapByteToBinary(rgbIn, 128);
   }


   /**
    * simple function to apply polynomial gamma curve to skew values of grayscale toward top or bottom.
    * f(x)=a*x+b*x^2
    * where a=gamma val
    * and b=1-a
    * @param byteVal byte-value in (0-255)
    * @param gamma effective gamma val (1=no skew, 0.5=skew down, 1.5=skew up)
    * @return gamma corrected byteVal
    */
   public static int applyGammaToByte(int byteVal, double gamma)
  {
    double a=gamma;
    double b=1-a;

    double normIn=byteVal/255.0;  //normalized input value
    double normOut=a*normIn+b*Math.pow(normIn, 2.0);

    normOut*=255.0;
    return clipIntToByte((int)normOut);
  }


  /**
   * clip int val to byte range
   * @param intVal
   * @return
   */
  public static int clipIntToByte(int intVal)
  {
    int retVal;
    retVal=intVal;

    if(retVal>255)
      retVal=255;
    else
    if(retVal<0)
      retVal=0;

    return retVal;
  }


   /**
    * maps byte to binary output using a given threshold
    * @param rgbIn
    * @param thresh
    * @return
    */
   public static int mapByteToBinary(int rgbIn, int thresh)
   {
     if(rgbIn < thresh)
       return 0;
     else
       return 255;
   }


   /**
    * converts an Image to type BufferedImage using Image.imageType
    * @param image
    * @param imageType type of image desired, see fields of BufferedImage for choices
    * eg, BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_BYTE_GRAY, etc.
    * @return
    */
   public static BufferedImage imageToBufferedImage(Image image, int imageType)
  {
    BufferedImage bi=new BufferedImage(image.getWidth(null), image.getHeight(null),
        imageType);  //create new buffered image

    Graphics2D big2d = bi.createGraphics(); //so create graphic on bufIm
    big2d.drawImage(image,0,0,null);     //and redraw the Image as a BufImage
    return bi;
  }


  /**
   * Places an image at a specific location within a larger image...
   * @param image image to be placed
   * @param width size (pixels) of desired output
   * @param height size (pixels) of desired output
   * @param xLocation offset of image in output
   * @param yLocation offset of image in output
   * @return
   */
  public static BufferedImage padImage(Image image, int width, int height, int xLocation, int yLocation)
  {
    if(width<image.getWidth(null) || height<image.getHeight(null))
    {
      System.out.println("Error, desired padImage result is smaller than unpadded image!");
      return null;
    }
    else
    if(width<image.getWidth(null)+xLocation || height<image.getHeight(null)+yLocation)
    {
      System.out.println("Error, desired padImage result is too small for padded image");
      return null;
    }

    BufferedImage bi=new BufferedImage(width,  height, BufferedImage.TYPE_INT_RGB);  //create new buffered image

    Graphics2D big2d = bi.createGraphics(); //so create graphic on bufIm
    big2d.drawImage(image,xLocation,yLocation,null);     //and redraw the Image as a BufImage
    return bi;
  }



  /**
   * Removes a filename to leave only the path
   * @param fileName
   * @return
   */
  public static String removeFilename(String fileName)
    {
          String tmpStr=fileName;

          int whereChar = tmpStr.lastIndexOf('/');

//          DebugMessages.inform("char="+whereChar);

          if (0 < whereChar && whereChar <= tmpStr.length() - 2 )
          {
                return tmpStr.substring(0, whereChar);
              //filename = filename.substring(whereDot+1);
          }

          return "";
    }



  /**
   * Removes the extension from a filename
   * @param fileName
   * @return
   */
  public static String removeExtension(String fileName)
    {
          String tmpStr=fileName;

          int whereChar = tmpStr.lastIndexOf('.');

          if (0 < whereChar && whereChar <= tmpStr.length() - 2 )
          {
                return tmpStr.substring(0, whereChar);
              //extension = filename.substring(whereDot+1);
          }

          return "";
    }


    /**
     * Returns a filename without the path
     * @param fileName
     * @return
     */
    public static String getOnlyFilename(String fileName)
    {
          String tmpStr=fileName;

          int whereChar = tmpStr.lastIndexOf('/');

          DebugMessages.inform("char="+whereChar);

          if (0 < whereChar && whereChar <= tmpStr.length() - 2 )
          {
                return fileName.substring(whereChar+1);

          }

          return "";
    }



    public static void forceGarbageCollection()
    {
        Runtime rt = Runtime.getRuntime();
        long total = rt.freeMemory();
        long free = rt.freeMemory();

        int minFree=1000000;

        if(total-free < minFree) { //if it is less than min allowed
                                   //release refs to some objects here
                                   //the systems that create cache will release
                                   //LRU objects here
           rt.gc();
        }

    }


}



