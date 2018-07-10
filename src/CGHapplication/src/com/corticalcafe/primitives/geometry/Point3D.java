package com.corticalcafe.primitives.geometry;

import com.corticalcafe.primitives.DebugMessages;

/**
 * Very primitive class with some useful methods for dealing with 3D coordinates
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Point3D
{
  private double xCoord, yCoord, zCoord;

  public Point3D()
  {
  }

  public Point3D(Point3D p)
  {
    setPoint(p.getX(), p.getY(), p.getZ());
  }

  public Point3D(double x, double y, double z)
  {
    setPoint(x, y, z);
  }

  public void setPoint(double x, double y, double z)
  {
    xCoord=x;
    yCoord=y;
    zCoord=z;
  }

  public void setPoint(Point3D pIn)
  {
    setPoint(pIn.getX(), pIn.getY(), pIn.getZ());
  }

  public void translate(Point3D p)
  {
    xCoord+=p.getX();
    yCoord+=p.getY();
    zCoord+=p.getZ();
  }

  public void scale(Point3D p)
  {
    xCoord*=p.getX();
    yCoord*=p.getY();
    zCoord*=p.getZ();
  }

  public double getX()
  {
    return xCoord;
  }

  public double getY()
  {
    return yCoord;
  }

  public double getZ()
  {
    return zCoord;
  }

  public void debugPoint(String str)
  {
    DebugMessages.debug(str+": X="+xCoord+" Y="+yCoord+" Z="+zCoord);
  }

  public void debugPoint()
  {
    debugPoint("");
  }

  public void sumWeightedPoint(Point3D p, double wt)
  {
    double inv=1-wt;

    xCoord=(xCoord*inv+p.getX()*wt)/2;
    yCoord=(yCoord*inv+p.getY()*wt)/2;
    zCoord=(zCoord*inv+p.getZ()*wt)/2;
  }

  public double distanceToPoint(Point3D p)
  {
    return Math.sqrt((xCoord-p.getX())*(xCoord-p.getX())
        + (yCoord-p.getY())*(yCoord-p.getY())
        + (zCoord-p.getZ())*(zCoord-p.getZ()));
  }

}