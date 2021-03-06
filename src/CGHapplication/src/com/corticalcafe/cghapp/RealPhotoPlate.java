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

import com.corticalcafe.primitives.DebugMessages;
import com.corticalcafe.utils.MiscUtils;
import java.awt.image.BufferedImage;
import com.corticalcafe.primitives.math.Complex;
import java.io.File;

/**
 *
 * @author astein
 */
public class RealPhotoPlate {

    int min, max, median;


    ComplexPhotoPlate plate;
    BufferedImage im;
    int outputType;
    String descr;

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }



    public RealPhotoPlate(ComplexPhotoPlate plate) {
        this.plate = plate;
    }


    public BufferedImage getOutputImage()
    {
        return im;
    }


    public void computeOutputImage(int outputType, boolean normalizePlateValues,
            boolean clipPlateValues, int thresholdVal)
    {
        int pixVal;

        computeStatValue(outputType);

        this.outputType=outputType;

        im=new BufferedImage(plate.getXresolution(), plate.getYresolution(), BufferedImage.TYPE_INT_RGB);

        //for each pixel on plate, convert to final pixel value via desired output function
        for (int i = 0; i < plate.getYresolution(); i++)
        {
          for (int j = 0; j < plate.getXresolution(); j++)
          {
            if(normalizePlateValues)  //if regular (non-normalized) output desired
            {
              pixVal=outputFunction(outputType,
                  plate.getPlateElement(j,i).getComplex(), thresholdVal); //convert to byte val via desired functino

              if (clipPlateValues)
                pixVal = MiscUtils.clipIntToByte(pixVal); //else truncate to byte if desired
            }
            else    //else if normalized output desired
            {
              pixVal=outputFunction(getNonquantizedOutputFunction(outputType),
                  plate.getPlateElement(j,i).getComplex(), thresholdVal); //convert to byte val via desired unquantized function

    //          DebugMessages.debug("min="+min+" max="+max+" pix="+pixVal+" byte="
    //              +(int) (255 * ( (double) pixVal + min) / max));

              pixVal = (int) (255 * ( (double) pixVal - min) / max); //normalize to byte

              if(isOutputQuantized())           //if quantized (binarized) output desired
                pixVal=MiscUtils.mapByteToBinary(pixVal, thresholdVal);   //then apply threshold
            }

            im.setRGB(j, i, MiscUtils.mapByteToRGB(pixVal));    //map into RGB image
          }
        }

    }



    //define output types
    static final int OUTPUT_REAL=0;
    static final int OUTPUT_REAL_BINARY=1;
    static final int OUTPUT_IMAGINARY=2;
    static final int OUTPUT_IMAGINARY_BINARY=3;
    static final int OUTPUT_AMPLITUDE=4;
    static final int OUTPUT_AMPLITUDE_BINARY=5;
    static final int OUTPUT_PHASE=6;
    static final int OUTPUT_PHASE_BINARY=7;
    static final int OUTPUT_INTENSITY=8;
    static final int OUTPUT_INTENSITY_BINARY=9;

    static final int OUTPUT_DEFAULT=OUTPUT_REAL;


  /**
   * given the complex state of a pt in space, apply the desired output function
   * to map it into RGB (255^16+255^8+255) space
   * @param outputMode
   * @param cIn complex value of pt
   * @param thresh threshold (if any)
   * @return RGB value
   */
  int outputFunction(int outputType, Complex cIn, int thresh)
  {
    int retVal=0;

    switch (outputType)
    {
      case OUTPUT_PHASE:
        retVal=mapPhaseToByte(cIn.getPhase());
        break;
      case OUTPUT_PHASE_BINARY:
        retVal=MiscUtils.mapByteToBinary(mapPhaseToByte(cIn.getPhase()), thresh);
        break;
      case OUTPUT_AMPLITUDE:
        retVal=mapAmpToByte(cIn.getAmplitude());
        break;
      case OUTPUT_AMPLITUDE_BINARY:
        retVal=MiscUtils.mapByteToBinary(mapAmpToByte(cIn.getAmplitude()), thresh);
        break;
      case OUTPUT_IMAGINARY:
        retVal=mapPhaseToByte(cIn.getImaginary());
        break;
      case OUTPUT_IMAGINARY_BINARY:
        retVal=MiscUtils.mapByteToBinary(mapPhaseToByte(cIn.getImaginary()), thresh);
        break;
      case OUTPUT_REAL:
        retVal=mapPhaseToByte(cIn.getReal());
        break;
      case OUTPUT_REAL_BINARY:
        retVal=MiscUtils.mapByteToBinary(mapPhaseToByte(cIn.getReal()), thresh);
        break;
      case OUTPUT_INTENSITY:    //output=Wave*conj(Wave)=Amp^2;
        retVal=mapAmpToByte(cIn.times(cIn.getConjugate().getAmplitude()).getAmplitude());
        break;
      case OUTPUT_INTENSITY_BINARY:    //output=Wave*conj(Wave)=Amp^2;
        retVal=MiscUtils.mapByteToBinary(mapAmpToByte(cIn.times(cIn.getConjugate().getAmplitude()).getAmplitude()), thresh);
//        retVal=MiscUtils.mapByteToBinary(mapAmpToByte(cIn.times(cIn.getConjugate()).getAmplitude()));
//        retVal=MiscUtils.mapByteToBinary((cIn.getAmplitude()*cIn.getAmplitude()));
        break;
      default:
        break;
    }

//    if(retVal>255 || retVal<0)
//      DebugMessages.error("retVal="+retVal);

    return retVal;
  }


  /**
   * maps *normalized* amplitudes (0-1) to Byte (0-255)
   * @param amp
   * @return
   */
  int mapAmpToByte(double amp)
  {
    return (int)(amp*255);
  }


  /**
   * maps phase (-PI to + PI) to byte (0-255)
   * @param phase
   * @return
   */
  int mapPhaseToByte(double phase)
  {
    int byteVal=(int)((phase+Math.PI)*255./(2*Math.PI));  //convert phase (-PI to +PI) to 0-255
    return byteVal;  //form RGB grayscale val
  }



  int getNonquantizedOutputFunction(int outputFn)
  {
    int nqOutputFn=OUTPUT_AMPLITUDE;

    switch (outputFn)   //use unquantized output function
    {
      case OUTPUT_AMPLITUDE:
      case OUTPUT_AMPLITUDE_BINARY:
        nqOutputFn=OUTPUT_AMPLITUDE;
        break;
      case OUTPUT_IMAGINARY:
      case OUTPUT_IMAGINARY_BINARY:
        nqOutputFn=OUTPUT_IMAGINARY;
        break;
      case OUTPUT_INTENSITY:
      case OUTPUT_INTENSITY_BINARY:
        nqOutputFn=OUTPUT_INTENSITY;
        break;
      case OUTPUT_PHASE:
      case OUTPUT_PHASE_BINARY:
        nqOutputFn=OUTPUT_PHASE;
        break;
      case OUTPUT_REAL:
      case OUTPUT_REAL_BINARY:
        nqOutputFn=OUTPUT_REAL;
        break;
    }

    return nqOutputFn;
  }



  /**
   * Tell whether the output function is quantized (binarized) or
   * is analogue in nature.
   * @return
   */
  boolean isOutputQuantized()
  {
    return (getNonquantizedOutputFunction(outputType)!=outputType);
  }


  /**
   * Hack routine to get some stats on current plate.  Inefficient because
   * typically all stats are desired and this routine must be called multiple times.
   * Could define a class or add stats to plate, but then need to add flag for whether stats are valid...
   * @param statType see field types for MIN/MAX/MEDIAN
   * @return the desired statistic
   */
  void computeStatValue(int outputType)
  {
    int tmp;
    int outputFn=getNonquantizedOutputFunction(outputType);

    min=outputFunction(outputFn, plate.getPointSource(0).getComplex(), 0);
    max=min;
    for (int i = 0; i < plate.getNumberOfPoints()-1; i++)   //find min & max values in plate
    {
      tmp=outputFunction(outputFn, plate.getPointSource(i).getComplex(), 0);       //get plate value
      if(tmp<min)
        min=tmp;

      if(tmp>max)
        max=tmp;
    }

    int bins[]=new int[max-min+1];  //dimension histogram up to max value

    for (int i = 0; i < plate.getNumberOfPoints()-1; i++)   //build histogram of plate values
    {
      tmp=outputFunction(outputFn, plate.getPointSource(i).getComplex(), 0);       //get plate value
      bins[tmp-min]++;  //increment correct bin
   }

    int sum=0;
    int midpoint=plate.getNumberOfPoints()/2;

    for (int i = 0; i < (max-min-1); i++)     //loop to find median
    {
      sum+=bins[i]; //integrate until we find the midpoint of the histogram

      if (sum>=midpoint)
      {
        median=i+min;
        break;
      }
    }

    DebugMessages.inform("Real:  Min="+min+" Max="+max+" Median="+median);


  }

    public int getMax() {
        return max;
    }

    public int getMedian() {
        return median;
    }

    public int getMin() {
        return min;
    }

    public void saveAsGIF(File file)
    {
         MiscUtils.writeImage(getOutputImage(), MiscUtils.formatGIF, file);
    }


    public void saveAsPNG(File file)
    {
         MiscUtils.writeImage(getOutputImage(), MiscUtils.formatPNG, file);
    }



}
