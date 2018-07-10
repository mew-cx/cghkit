package com.corticalcafe.primitives.physics;

import bsh.EvalError;
import bsh.Interpreter;
import com.corticalcafe.cghapp.ComplexPhotoPlate;
import java.util.*;

import com.corticalcafe.primitives.DebugMessages;
import com.corticalcafe.primitives.database.XMLIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.jdom.*;

/**
 * Short class to keep track of a group of point sources (eg, an object, or the
 * elements of the plate.
 * <p>Title: Computer Generated Hologram Maker</p>
 * <p>Description:Simulates  Hologram Simulator</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */
public class PointSourceArray
{
  Vector pointVector=new Vector();
  int inputFileType=FILETYPE_UNKNOWN;

  public PointSourceArray()
  {
  }

  /**
   * add another point source to the vector
   * @param ps
   */
  public void addPointSource(PointSource ps)
  {
    pointVector.add(ps);
  }

  /**
   * get the vector of pointsources as an array
   * @return
   */
  public PointSource[] getArray()
  {
    PointSource p[]=new PointSource[getNumberOfPoints()];

    for (int i = 0; i < p.length; i++)
    {
      p[i]=getPointSource(i);
    }

    return p;
  }


    public double getMinX()
    {
       double retVal=getPointSource(0).getPoint3D().getX();

        for (int i = 0; i < getNumberOfPoints(); i++)
        {
            if(getPointSource(i).getPoint3D().getX()<retVal)
                retVal=getPointSource(i).getPoint3D().getX();
        }

        return retVal;
    }

    public double getMaxX()
    {
       double retVal=getPointSource(0).getPoint3D().getX();

        for (int i = 0; i < getNumberOfPoints(); i++)
        {
            if(getPointSource(i).getPoint3D().getX()>retVal)
                retVal=getPointSource(i).getPoint3D().getX();
        }

        return retVal;
    }

    public double getMinY()
    {
       double retVal=getPointSource(0).getPoint3D().getY();

        for (int i = 0; i < getNumberOfPoints(); i++)
        {
            if(getPointSource(i).getPoint3D().getY()<retVal)
                retVal=getPointSource(i).getPoint3D().getY();
        }

        return retVal;
    }

    public double getMaxY()
    {
       double retVal=getPointSource(0).getPoint3D().getY();

        for (int i = 0; i < getNumberOfPoints(); i++)
        {
            if(getPointSource(i).getPoint3D().getY()>retVal)
                retVal=getPointSource(i).getPoint3D().getY();
        }

        return retVal;
    }



  public void setPointSourceArray(PointSourceArray psa)
  {
      clearPointSourceArray();
      int i;

      for (i = 0; i < psa.getNumberOfPoints(); i++) {
          addPointSource(psa.getPointSource(i));
      }

      DebugMessages.inform("Added "+i+" pointsource array.");
  }


  public void clearPointSourceArray()
  {
    pointVector.clear();
    inputFileType=FILETYPE_UNKNOWN;
  }


  public int getFileType()
  {
      return inputFileType;
  }


  /**
   * get a particular point source
   * @param idx index of pt source to be retrieved
   * @return
   */
  public PointSource getPointSource(int idx)
  {
    return (PointSource)pointVector.elementAt(idx);
  }

  /**
   * get number of point sources in vector
   * @return
   */
  public int getNumberOfPoints()
  {
    return pointVector.size();
  }

  public void debug()
  {
    for (int i = 0; i < getNumberOfPoints(); i++)
    {
      getPointSource(i).debug();
    }
  }

  public void debug(String str)
  {
    DebugMessages.inform(str);
    debug();
  }


  public void debugSummary(String str)
  {
      DebugMessages.inform(str);
      debugSummary();
  }

  public void debugSummary()
  {
      DebugMessages.inform("# Pts: "+getNumberOfPoints()+"\tMinX: "+getMinX()+"\tMaxX: "
              +getMaxX()+"\tMinY: "+getMinY()+"\tMaxY: "+getMaxY());
  }

  /**
   * normalize all amplitudes in pt source
   */
  public void normalizeAmplitudes()
  {
    double ptSourceTotal=0;

    for (int i = 0; i < getNumberOfPoints(); i++)
    {
      ptSourceTotal+=getPointSource(i).getComplex().getAmplitude();
    }

    double tmpAmp;
    for (int i = 0; i < getNumberOfPoints(); i++)
    {
      tmpAmp=getPointSource(i).getComplex().getAmplitude();

      getPointSource(i).getComplex().assignPolar(tmpAmp/ptSourceTotal,
          getPointSource(i).getComplex().getPhase());
    }
  }

  public void randomizePhases()
  {
    for (int i = 0; i < getNumberOfPoints(); i++)
    {
      getPointSource(i).randomizePhase();
    }
  }

  /**
   * Shift a point-source array by an X/Y/Z offset
   * @param xOffset
   * @param yOffset
   * @param zOffset
   */
  public void shiftXYZ(double xOffset, double yOffset, double zOffset)
  {
    for (int i = 0; i < getNumberOfPoints(); i++)
    {
      double x=getPointSource(i).getPoint3D().getX();
      double y=getPointSource(i).getPoint3D().getY();
      double z=getPointSource(i).getPoint3D().getZ();

      getPointSource(i).getPoint3D().setPoint(x+xOffset, y+yOffset, z+zOffset);
    }
  }


  /**
   * Scale a point-source array by an X/Y/Z multiplier
   * @param xScale
   * @param yScale
   * @param zScale
   */
  public void scaleXYZ(double xScale, double yScale, double zScale)
  {
    for (int i = 0; i < pointVector.size(); i++)
    {
      double x=getPointSource(i).getPoint3D().getX();
      double y=getPointSource(i).getPoint3D().getY();
      double z=getPointSource(i).getPoint3D().getZ();

      getPointSource(i).getPoint3D().setPoint(x*xScale, y*yScale, z*zScale);
    }
  }




  public void changeZ(double newZ)
  {
    for (int i = 0; i < pointVector.size(); i++)
    {
      double x=getPointSource(i).getPoint3D().getX();
      double y=getPointSource(i).getPoint3D().getY();

      getPointSource(i).getPoint3D().setPoint(x, y, newZ);
    }
  }



  public void scaleXYtoPlate(ComplexPhotoPlate p)
  {
            double xScale, yScale, zScale=1, scaleFactor=1.0/2.54;

            //scales the object to the same size as the plate
                xScale=scaleFactor*(p.getMaxX()-p.getMinX())/
                        (getMaxX()-getMinX());

                yScale=scaleFactor*(p.getMaxY()-p.getMinY())/
                        (getMaxY()-getMinY());

                DebugMessages.inform("xScale="+xScale+" yScale="+yScale+" zScale="+zScale);

                scaleXYZ(xScale, yScale, zScale);

  }



  public void centerObjectXY()
  {
                double imageOffsetX=((getMaxX()-getMinX())/2.0)+getMinX();
                double imageOffsetY=((getMaxY()-getMinY())/2.0)+getMinY();
                shiftXYZ(-imageOffsetX, -imageOffsetY, 0);

  }


  /**
   * read XML for pointsourarray, something like:
   *   <PointSourceArray>
   *        <PointSource x="0" y="0" z="0" amp="1" wavelength="670e-9" phase="-3.1415"/>
   *        <PointSource x="0" y="335e-9" z="0" amp="1" wavelength="670e-9" phase="-1.25"/>
   *   </PointSourceArray>
   * @param doc JDOM Document
   * @return GraphData instance built from XML
   */
  public void fromXML(Element rootElem)
  {
    clearPointSourceArray();

    if(rootElem==null || rootElem.getName().compareTo("PointSourceArray")!=0)
    {
      DebugMessages.error("Error reading PointSourceArray stream!");
      return;
    }

    List pointSources=rootElem.getChildren("PointSource");

    Iterator i=pointSources.iterator();

    while(i.hasNext())
    {
      PointSource ps=new PointSource();
      ps.fromXML((Element)i.next());
      addPointSource(ps);
    }
  }



    public static final int FILETYPE_UNKNOWN= -1;
    public static final int FILETYPE_XML=0;
    public static final int FILETYPE_IMAGE=1;
    public static final int FILETYPE_JAVA=2;

    public void loadPointSourceArrayFromFile(File inputFile, 
            double imageOffsetX, double imageOffsetY,  double imageDepth,
            double imageSampling, double imageWavelength)
    {
              clearPointSourceArray();

              if(inputFile.getAbsolutePath().toLowerCase().endsWith("gif"))
                inputFileType=FILETYPE_IMAGE;
              else
              if(inputFile.getAbsolutePath().toLowerCase().endsWith("png"))
                inputFileType=FILETYPE_IMAGE;
              else
              if(inputFile.getAbsolutePath().toLowerCase().endsWith("jpg"))
                inputFileType=FILETYPE_IMAGE;
              else
              if(inputFile.getAbsolutePath().toLowerCase().endsWith("xml"))
                inputFileType=FILETYPE_XML;
              else
              if(inputFile.getAbsolutePath().toLowerCase().endsWith("java"))
                inputFileType=FILETYPE_JAVA;
              else
                DebugMessages.error("Unknown input file type!");

              if(inputFile.exists()==false)
              {
//                JOptionPane.showMessageDialog(null, "The input file does not exist!",
//                  "Input File Error", JOptionPane.ERROR_MESSAGE);
                  DebugMessages.error("Can't load file:  "+inputFile.getName());
                return;
              }

              switch (inputFileType)
              {
                case FILETYPE_IMAGE:
                  try
                  {
                    BufferedImage inputImage = ImageIO.read(inputFile);

                    DebugMessages.debug("Reading "+inputImage.getWidth()+"x"+inputImage.getHeight()+" Image");

                    for (int i = 0; i < inputImage.getHeight(); i++)
                    {
                      for (int j = 0; j < inputImage.getWidth(); j++)
                      {
                      if((Math.abs(inputImage.getRGB(j, i))>>16) !=0)   //if nonzero count it
                        addPointSource(new PointSource((j-imageOffsetX) * imageSampling, (i-imageOffsetY) * imageSampling, imageDepth, 1, imageWavelength, 0));
                      }
                    }

                    DebugMessages.debug("Nonzero image pts="+getNumberOfPoints()+", Total image pts="+inputImage.getWidth()*inputImage.getHeight());
                  }
                  catch (IOException ex)
                  {
                    DebugMessages.error("Couldn't read input image file");
                  }
                  break;
                case FILETYPE_JAVA:
                  Interpreter i = null;
                  try
                  {
                    i = new Interpreter();


                    String shortFileName=inputFile.getName();
                    int idx=shortFileName.indexOf(".");
                    shortFileName=shortFileName.substring(0,idx);

                    DebugMessages.inform("Executing " + inputFile.getCanonicalFile());
                    i.source(inputFile.getAbsolutePath());
                    DebugMessages.inform("sourced " +inputFile.getCanonicalFile().getName());

                    String instantiate = shortFileName + " tmp=new " + shortFileName + "();";
                    DebugMessages.inform(instantiate);
                    i.eval(instantiate);
                    String execFn="PointSourceArray tmpPsa=tmp.createObject();";
                    DebugMessages.inform(execFn);
                    i.eval(execFn);
                    setPointSourceArray((PointSourceArray)i.get("tmpPsa"));
                  }
                    catch (EvalError ex)
                    {
                        Logger.getLogger(PointSourceArray.class.getName()).log(Level.SEVERE, null, ex);
                    }
                  catch (IOException ex3)
                  {
                    ex3.printStackTrace();
                  }
                  break;
                case FILETYPE_XML:
                  DebugMessages.inform("Reading XML");

                  Document doc=XMLIO.readXMLfile(inputFile);
                  fromXML(doc.getRootElement());
                  break;
              }


              return;
    }



}