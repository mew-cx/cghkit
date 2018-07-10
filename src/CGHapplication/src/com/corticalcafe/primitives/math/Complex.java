package com.corticalcafe.primitives.math;

/**
 * Quick and dirty Complex class
 * <p>Title: Computer Generated Hologram Maker</p>
 * <p>Description:Simulates  Hologram Simulator</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 *
 * HISTORY
 *
 * 20110417 - Corrected problem with randomized phases (thanks to Hib Engler for finding this)
 * 
 */

public class Complex
{
  public float re, im; //real and imaginary parts, accessible to outside world

  public Complex()
  {
  }

  public Complex(double real, double imaginary)
  {
    re= (float) real;
    im= (float) imaginary;
  }

  public void assignCartesian(double real, double imaginary)
  {
    re= (float) real;
    im= (float) imaginary;
  }

  public void assignPolar(double amplitude, double phase)
  {
    re= (float) (amplitude * Math.cos(phase));
    im= (float) (amplitude * Math.sin(phase));
  }

  public Complex assign(Complex z)
  {
    re=z.re;
    im=z.im;
    return this;
  }

  public Complex plus(Complex z)
  {
    return new Complex(re+z.re, im+z.im);
  }

  public Complex minus(Complex z)
  {
    return new Complex(re-z.re, im-z.im);
  }

  public Complex times(Complex z)
  {
    return new Complex(re*z.re-im*z.im, im*z.re+re*z.im);
  }

  public Complex times(double x)
  {
    return new Complex(x*re, x*im);
  }

  public Complex plusAssign(Complex z)
  {
    re += z.re;
    im += z.im;
    return this;
  }

  public Complex getConjugate()
  {
    return new Complex(re, -im);
  }

  public double getAmplitude()
  {
    return Math.sqrt(re*re+im*im);
  }

  public double getPhase()
  {
    return Math.atan2(im, re);
  }

  public double getReal()
  {
    return re;
  }

  public double getImaginary()
  {
    return im;
  }

  public void randomizePhase()
  {
    assignPolar(getAmplitude(), Math.random()*Math.PI*2);
  }

}