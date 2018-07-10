package com.corticalcafe.primitives.math;

/**
 * quick and dirty Complex Array class
 * <p>Title: Computer Generated Hologram Maker</p>
 * <p>Description:Simulates  Hologram Simulator</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Alan Stein
 * @version 1.0
 */
public class ComplexArray2D
{
  private double[] data;
  private int x, y;

  /**
   * create new 2D array m by n of Complex numbers
   * @param m
   * @param n
   */
  public ComplexArray2D(int m, int n)
  {
    x=m;
    y=n;
    data=new double[2*x*y];
  }

  public Complex get(int i, int j)
  {
    return new Complex(data[2*(i*x+j)], data[2*(i*x+j)+1]);
  }

  public void set(int i, int j, Complex z)
  {
    data[2*(i*x+j)]=z.re;
    data[2*(i*x+j)+1]=z.im;
  }

}