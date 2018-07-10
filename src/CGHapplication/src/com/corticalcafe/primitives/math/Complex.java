package com.corticalcafe.primitives.math;

/**
 * Quick and dirty Complex class
 * <p>Title: Computer Generated Hologram Maker</p>
 * <p>Description:Simulates  Hologram Simulator</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */

public class Complex
{
  public double re, im; //real and imaginary parts, accessible to outside world

  public Complex()
  {
  }

  public Complex(double real, double imaginary)
  {
    re=real;
    im=imaginary;
  }

  public void assignCartesian(double real, double imaginary)
  {
    re=real;
    im=imaginary;
  }

  public void assignPolar(double amplitude, double phase)
  {
    re=amplitude*Math.cos(phase);
    im=amplitude*Math.sin(phase);
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
    assignPolar(getPhase(), Math.random()*Math.PI*2);
  }

}