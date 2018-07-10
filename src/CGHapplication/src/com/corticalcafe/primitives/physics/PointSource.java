package com.corticalcafe.primitives.physics;

import com.corticalcafe.primitives.math.Complex;
import com.corticalcafe.primitives.geometry.Point3D;

import com.corticalcafe.primitives.DebugMessages;

import org.jdom.*;

public class PointSource
{
  private Point3D point=new Point3D();
  private Complex wave=new Complex();
  private double lambda=670e-9;  //approx wavelength of red light (m)

//  static final double TWOPI=Math.PI*2;
//  static final double c=3e+8;  //speed of light (m/s)

  public PointSource()
  {
  }

  /**
   * initialize a pointsource
   * @param p spatial coords (m)
   * @param wave phase/amplitude of source
   * @param lambda wavelength of wave (m)
   * @param atten attenuation (0=none)
   */
  public PointSource(Point3D p, Complex wave, double lambda)
  {
    point.setPoint(p);
    this.wave.assign(wave);
    this.lambda=lambda;
  }


  /**
   * initialize a pointsource
   * @param x x coord
   * @param y y coord
   * @param z z coord
   * @param amp amplitude
   * @param lambda wavelength of wave (m)
   * @param phase phase
   * @param atten attenuation through material (0=none)
   */
  public PointSource(double x, double y, double z, double amp, double lambda, double phase)
  {
    point.setPoint(x, y, z);
    wave.assignPolar(amp, phase);
    this.lambda=lambda;
  }


  /**
   * initialize a pointsource - *OBSOLETED*
   * @param x x coord
   * @param y y coord
   * @param z z coord
   * @param amp amplitude
   * @param lambda wavelength of wave (m)
   * @param phase phase
   * @param atten attenuation through material (0=none)
   */
  public PointSource(double x, double y, double z, double amp, double lambda, double phase, double atten)
  {
    point.setPoint(x, y, z);
    wave.assignPolar(amp, phase);
    this.lambda=lambda;
  }

  /**
   * get spatial coords
   * @return spatial coords
   */
  public Point3D getPoint3D()
  {
    return point;
  }

  /**
   * get phase/amplitude of source
   * @return phase/amplitude
   */
  public Complex getComplex()
  {
    return wave;
  }

  /**
   * compute phase/amplitude of wave propagation when it reaches new coordinates
   * @param p spatial destination of wave
   * @return phase/amplitude
   */
  public Complex propagateWithoutAttenuation(PointSource p)
  {
    final double c=3e+8;  //speed of light (m/s)
    double dist=point.distanceToPoint(p.getPoint3D());
//    DebugMessages.debug("dist="+dist/lambda+" wavelengths");
    double time=dist/c;  //compute time for wave to travel over distance

    Complex w=new Complex();
//    DebugMessages.debug("lambda="+lambda+" time="+time+" time*c/lambda="+time*c/lambda);
//    w.assignPolar(wave.getAmplitude(), wave.getPhase()+TWOPI*time*(c/lambda));
    w.assignPolar(wave.getAmplitude(), wave.getPhase()+Math.PI*2*time*(c/lambda));

    return w;
  }


  public Complex propagateWith3dAttenuation(PointSource p)
  {
    final double c=3e+8;  //speed of light (m/s)
    double dist=point.distanceToPoint(p.getPoint3D());
//    DebugMessages.debug("dist="+dist/lambda+" wavelengths");
    double time=dist/c;  //compute time for wave to travel over distance

    Complex w=new Complex();
//    DebugMessages.debug("lambda="+lambda+" time="+time+" time*c/lambda="+time*c/lambda);

//    w.assignPolar((wave.getAmplitude()/(2*TWOPI*dist*dist)), wave.getPhase()+TWOPI*time*(c/lambda));
    w.assignPolar((wave.getAmplitude()/(2*Math.PI*2*dist*dist)), wave.getPhase()+Math.PI*2*time*(c/lambda));

    return w;
  }


  public double getLambda()
  {
    return lambda;
  }


  public void setLambda(double lambda)
  {
    this.lambda=lambda;
  }

  public void debug(String str)
  {
    DebugMessages.debug(str+": ("+point.getX()+","+point.getY()+","+point.getZ()+") amp="+wave.getAmplitude()
        +" phase="+wave.getPhase()+" real="+wave.getReal()+" imag="+wave.getImaginary());
  }

  public void debug()
  {
      debug("");
  }

  public void randomizePhase()
  {
    getComplex().randomizePhase();
  }

  /**
   * reads XML for a pointsource, something like:
   * 	<PointSource x="0" y="0" z="0" amp="1" wavelength="670e-9" phase="-3.1415"/>
   * @param rootElem
   */
  public void fromXML(Element rootElem)
  {
    if(rootElem==null || rootElem.getName().compareTo("PointSource")!=0)
    {
      DebugMessages.error("Error reading PointSource stream!");
      return;
    }

    double x=Double.parseDouble(rootElem.getAttributeValue("x"));
    double y=Double.parseDouble(rootElem.getAttributeValue("y"));
    double z=Double.parseDouble(rootElem.getAttributeValue("z"));
    double amp=Double.parseDouble(rootElem.getAttributeValue("amp"));
    double wavelength=Double.parseDouble(rootElem.getAttributeValue("wavelength"));
    double phase=Double.parseDouble(rootElem.getAttributeValue("phase"));

    point.setPoint(x, y, z);
    wave.assignPolar(amp, phase);
    this.lambda=wavelength;
  }

}