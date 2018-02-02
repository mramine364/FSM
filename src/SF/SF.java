package SF;

import javafx.geometry.Point2D;

/**
 * Created by don on 8/22/2015.
 */
public class SF {

    // return p where p.distance(A)=r and p belong to (AB)
    public static Point2D getMSecond(Point2D A, Point2D B, double r){
        if( A.getX()==B.getX() && A.getY()==B.getY() ){
            return B;
        }
        
        if( A.getX()==B.getX() ){
            double ys;
            ys = A.getY()+(A.getY()<B.getY()?r:-r);
            return new Point2D(A.getX(), ys);
        }else{
            double xs,ys;
            double a = (A.getY()-B.getY())/(A.getX()-B.getX()), b = A.getY()-a*A.getX();
            xs = -r*(A.getX()-B.getX())/A.distance(B)+A.getX();
            ys = a*xs+b;
            return new Point2D(xs,ys);
        }
        
    }

    // return p where p is the rotation point of P with center O by angle theta
    public static Point2D getRotatePoint(Point2D P, Point2D O, double theta){
        double rx = Math.cos(theta)*(P.getX()-O.getX()) - Math.sin(theta)*(P.getY()-O.getY()) + O.getX();
        double ry = Math.sin(theta)*(P.getX()-O.getX()) + Math.cos(theta)*(P.getY()-O.getY()) + O.getY();
        return new Point2D(rx,ry);
    }
}
