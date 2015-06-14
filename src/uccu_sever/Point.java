/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import org.python.modules.jffi.JITRuntime;

/**
 *
 * @author xiaoshuang
 */
public class Point {
   public int x;
   public int y;
   public Point(int x, int y)
   {
       this.x = x;
       this.y = y;
   }
   public Point(Point p)
   {
       this.x = p.x;
       this.y = p.y;
   }
   public double disFrom(Point p)
   {
       double res = (double)((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
       return Math.sqrt(res);
   }
   public Point movePoint(Point p, int d)
   {
       double dist = this.disFrom(p);
       if(dist < 0.00000001)
           return new Point(p);
       int nx = (int) (this.x + ((double)(p.x - this.x))*(d)/dist);
       int ny = (int) (this.y + ((double)(p.y - this.y))*(d)/dist);
       return new Point(nx, ny);
   }
   public Point movePointTrunc(Point p, int d)
   {
       double dist = this.disFrom(p);//实际距离
       if(dist <= d)//实际距离小于移动距离
           return new Point(p);
       double dd = d;
       int nx = (int) ((double)this.x + ((double)(p.x - this.x))*(dd)/dist);
       int ny = (int) ((double)this.y + ((double)(p.y - this.y))*(dd)/dist);
       
       return new Point(nx, ny);
   }
   public boolean equals(Point p)
   {
       return this.x == p.x && this.y == p.y;
   }
   @Override
   public String toString()
   {
       return "( "+x +", "+y+" )";
   }
}
