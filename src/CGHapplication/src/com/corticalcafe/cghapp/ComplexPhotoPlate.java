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

import com.corticalcafe.primitives.physics.*;

import java.util.Vector;

import com.corticalcafe.primitives.DebugMessages;


/**
 *
 * @author astein
 */
public class ComplexPhotoPlate extends PointSourceArray{
  int maxXres=0;
  int maxYres=0;
  double xSamplingRate=0;
  double ySamplingRate=0;
  int xOffset=0;
  int yOffset=0;


  /**
   *
   */
  public ComplexPhotoPlate()
  {
  }


  /**
   * get plate element by x and y indices
   * @param xIdx x index
   * @param yIdx y index
   * @return plate element pointsource
   */
  public PointSource getPlateElement(int xIdx, int yIdx)
  {
    return (PointSource)getPointSource(yIdx*getXresolution()+xIdx);
  }




   /**
   * Initialize a 2d film plate in XY plane
   * @param xResolution number of samples along X
   * @param yResolution number of samples along Y
   * @param spacing spacing between plate samples (in meters)
   *
   * For a sanity check:
   * a sample of x=67, y=1, spacing=10*1e-9 with a single point source
   * at 0,0,0 (wavelength = 670 nm; red) should yield 1 full sine wave over the 67 samples
   * @param center
   */
  public void initializePlate(int xResolution, int yResolution, double xSampling, double ySampling)
  {
    initializePlate(xResolution, yResolution, xOffset, yOffset, xSampling, ySampling, false);
  }



  /**
   * Initialize a 2d film plate in XY plane
   * @param xResolution number of samples along X
   * @param yResolution number of samples along Y
   * @param spacing spacing between plate samples (in meters)
   *
   * For a sanity check:
   * a sample of x=67, y=1, spacing=10*1e-9 with a single point source
   * at 0,0,0 (wavelength = 670 nm; red) should yield 1 full sine wave over the 67 samples
   * @param center
   */
  public void initializePlate(int xResolution, int yResolution, double xSampling, double ySampling, boolean center)
  {
    int xOffset=0, yOffset=0;

    if(center)
    {
      xOffset=(int)(maxXres*xSampling/2.0);
      yOffset=(int)(maxYres*ySampling/2.0);
    }

    initializePlate(xResolution, yResolution, xOffset, yOffset, xSampling, ySampling, center);
  }
  

  /**
   * 
   * @param xResolution
   * @param yResolution
   * @param xOffset
   * @param yOffset
   * @param spacing
   * @param center
   */
  public void initializePlate(int xResolution, int yResolution, int xOffset, int yOffset, 
          double xSampling, double ySampling, boolean center)
  {
    maxXres=xResolution;
    maxYres=yResolution;
    xSamplingRate=xSampling;
    ySamplingRate=ySampling;
    this.xOffset=xOffset;
    this.yOffset=yOffset;

    clearPointSourceArray();

    for (int i = 0; i < maxYres; i++)
    {
      for (int j = 0; j < maxXres; j++)
      {
        PointSource p=new PointSource((j+xOffset)*xSamplingRate, (i+yOffset)*ySamplingRate, 0, 0, 0, 0);
        addPointSource(p);
      }
    }
  }


  /**
   * Create a new smaller plate which is a subset of the current plate (used for dividing plate calculation up into multiple threads)
   * @param subPlateNum
   * @param maxSubPlates
   * @return
   */
  public ComplexPhotoPlate createSubPlate(int subPlateNum, int maxSubPlates)
  {
      ComplexPhotoPlate retPlate=new ComplexPhotoPlate();

      int xRes=maxXres;
      int yRes=maxYres/(maxSubPlates);
      int xOffset= -0;
      int yOffset= (maxYres/maxSubPlates)*subPlateNum;

      retPlate.initializePlate(xRes, yRes, xOffset, yOffset, xSamplingRate, ySamplingRate, false);

      return retPlate;
  }



    public void addSubPlate(ComplexPhotoPlate subPlate)
    {
        for (int i = 0; i <subPlate.getNumberOfPoints(); i++) {
            addPointSource(subPlate.getPointSource(i));
        }

    }

    public void debug(String str)
    {
        DebugMessages.inform(str+" Size=["+getXresolution()+","+getYresolution()+"] Offset=["
                +getXoffset()+","+getYoffset()+"] xSampRate="+getXsamplingRate()+
                 " ySampRate="+getYsamplingRate()+" NumPts="+getNumberOfPoints());
    }

  /**
   * create a diffraction grating on the plate, discards phases of anything previously imaged
   * Use:  createDiffractionGrating(1)  to create the finest white-black-white... pattern possible
   * @param order desired order of grating (1=highest order (diffraction lines are closest
   * possible (eg, highest spatial frequency sampling possible), 2=sampled at 4 samples per sine, etc.)
   */
  void createDiffractionGrating(int order)
  {
    for (int i = 0; i < maxYres; i++)
    {
      for (int j = 0; j < maxXres; j++)
      {
        getPlateElement(j, i).getComplex().assignPolar(1, Math.PI*j/order);
      }
    }
  }

  /**
   *
   * @return
   */
  public int getXresolution()
  {
    return maxXres;
  }

  /**
   *
   * @return
   */
  public int getYresolution()
  {
    return maxYres;
  }

  /**
   *
   * @return
   */
  public double getXsamplingRate()
  {
    return xSamplingRate;
  }


  /**
   *
   * @return
   */
  public double getYsamplingRate()
  {
    return ySamplingRate;
  }


  public int getXoffset()
  {
      return xOffset;
  }

  public int getYoffset()
  {
      return yOffset;
  }


}
