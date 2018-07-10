/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.corticalcafe.primitives.math;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author astein
 */
public class ImageFFT {

    public static Image computeFFT(Image im)
    {
        BufferedImage retIm = null;

        BufferedImage input=(BufferedImage)im;

        Complex output2d[][]=new Complex[input.getHeight()][input.getWidth()];

        // copy elements to 2d array
        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
                int tmp=Math.abs(input.getRGB(j, i))>>16;
                output2d[i][j]=new Complex(tmp,0);
            }
        }


        // FFT in 1 direction
        for (int i = 0; i < input.getHeight(); i++) {
             output2d[i]=FFT.fftPower2(output2d[i]);
        }

        // transpose 2d array (HACK, TRANSPOSES OUTPUT!!!)
        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
              output2d[i][j]=output2d[j][i];
            }
        }

        // FFT in 1 direction again
        for (int i = 0; i < input.getHeight(); i++) {
             output2d[i]=FFT.fftPower2(output2d[i]);
        }


        retIm=new BufferedImage(input.getWidth(), input.getHeight(),BufferedImage.TYPE_BYTE_GRAY);


        //create image for return
        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
//                retIm.setRGB(i, j, (i*j)%256);

//                double outputVal=output2d[i][j].getReal()*256*256
//                        +output2d[i][j].getReal()*256
//                        +output2d[i][j].getReal();
//                retIm.setRGB(i, j, (int)outputVal);
                
                retIm.setRGB(i, j, (int)output2d[i][j].getReal());
            }
        }

        return retIm;
    }

}
