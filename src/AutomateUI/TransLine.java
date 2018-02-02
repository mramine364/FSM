package AutomateUI;


import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import SF.SF;
import java.util.Objects;
import javafx.geometry.Insets;

/**
 * Created on 8/16/2015.
 */
public class TransLine extends Path {

    private final Polygon polygon;   
    private TextField text;
    private final MoveTo start;
    private final CubicCurveTo curve;
    private final CubicCurveTo reverseCurve;
    private StateUI to;
    private StateUI from;
    private final double arrowHeadTransLength = 14;
    
    public static String default_trans_name;
    public static String trans_name;
    static{
        default_trans_name = "a";
        trans_name = "a";
    }

    private double lastEventX;
    private double lastEventY;
    private double distance;
    
    private static String textc = "rgba(239, 240, 241, 0.3)";
    private static String texthc = "rgba(239, 240, 241, 0.6)";

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransLine other = (TransLine) obj;
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        return true;
    }
    
    public CubicCurveTo getCurve() {
        return curve;
    }
    public CubicCurveTo getReverseCurve() {
        return reverseCurve;
    }
    
    public StateUI getTo() {
        return to;
    }
    public void setTo(StateUI to) {
        this.to = to;
    }
    public StateUI getFrom() {
        return from;
    }
    public void setFrom(StateUI from) {
        this.from = from;
    }

    public TransLine(FSMUI afui) {

        polygon = new Polygon();
        start = new MoveTo();
        curve = new CubicCurveTo();
        reverseCurve = new CubicCurveTo();
        
        setStrokeType(StrokeType.CENTERED);
        setStrokeWidth(2);

        text = new TextField();
        text.setText(trans_name);
        text.setEditable(false);
        text.setAlignment(Pos.CENTER);
        text.setStyle("-fx-background-color: "+textc+";");
        text.setPadding(Insets.EMPTY);
        text.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            textResize();
            fixText();
        });
        textResize();
        fixText();

        makeDynamic(afui);
        makeRemovable(afui);
        
        getElements().addAll(start,curve,reverseCurve);
    }

    private void makeDynamic(FSMUI afui){
        setOnMousePressed((MouseEvent event) -> {
            lastEventX = event.getX();
            lastEventY = event.getY();
            distance = new Point2D(lastEventX, lastEventY).distance(from.getCenterX(), from.getCenterY());
        });

        setOnMouseDragged((MouseEvent event) -> {
            //System.out.println("transLine event     x:"+event.getX()+", y:"+event.getY());
            if (afui.paletteOn.equals("")) {
                if( to.equals(from) ){
                    double lex = event.getX();
                    double ley = event.getY();
                    // recalculate lastEventXY such as it has the same distance from the center
                    double ox = from.getCenterX(), oy = from.getCenterY();
                    double alpha = Math.atan2(ley-oy, lex-ox), dx,dy;
                    dx = distance*Math.cos(alpha);
                    dy = distance*Math.sin(alpha);
                    fixControl(dx + ox, dy + oy, lastEventX, lastEventY);
                    lastEventX = dx + ox;
                    lastEventY = dy + oy;
                }
                else
                    fixControl(new Point2D(startX(), startY()), new Point2D(endX(), endY()), new Point2D(event.getX(), event.getY()));

                fixPolygon();
                fixText();
            }

            afui.cursor.setLayoutX(event.getX() -FSMUI.cursorWidth/2);
            afui.cursor.setLayoutY(event.getY() -FSMUI.cursorHeight/2);
        });
        setOnMouseEntered((MouseEvent event) -> {
            if (afui.paletteOn.equals("")) {
                hovered();
            } else if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                hoveredToDelete();
            }
        });
        setOnMouseExited((MouseEvent event) -> {
            if (afui.paletteOn.equals("") || afui.paletteOn.equalsIgnoreCase("Delete")) {
                unHovered();
            }
        });
        text.setOnMouseEntered((MouseEvent event) -> {
            if (afui.paletteOn.equals("")) {
                hovered();
            } else if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                hoveredToDelete();
            }
        });
        text.setOnMouseExited((MouseEvent event) -> {
            if (afui.paletteOn.equals("") || afui.paletteOn.equalsIgnoreCase("Delete")) {
                unHovered();
            }
        });
    }

    private void makeRemovable(FSMUI afui){
        setOnMouseClicked((MouseEvent event2) -> {
            if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                removeFrom(afui);
            }
        });
        getText().setOnMouseClicked((MouseEvent event3) -> {
            if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                removeFrom(afui);
            } else if (afui.paletteOn.equalsIgnoreCase("")) {
                getText().setEditable(!getText().isEditable());
            }
        });
    }

    public void fixPolygon(){ // arrow
        Point2D A = new Point2D(curve.getX(),curve.getY());
        Point2D Ap = new Point2D(curve.getControlX2(),curve.getControlY2());
        Point2D Ms = SF.getMSecond(A, Ap, arrowHeadTransLength);

        Point2D P1 = SF.getRotatePoint(Ms, A, Math.PI / 7);
        Point2D P2 = SF.getRotatePoint(Ms, A, -Math.PI / 7);
        getPolygon().getPoints().setAll(new Double[]{A.getX(),A.getY(),P1.getX(),P1.getY(),P2.getX(),P2.getY()});
    }

    public final void fixText(){
        double p0x,p0y,p1x,p1y,p2x,p2y,p3x,p3y;
        p0x = startX(); p0y = startY();
        p3x = endX(); p3y = endY();
        p1x = curve.getControlX1(); p1y = curve.getControlY1();
        p2x = curve.getControlX2(); p2y = curve.getControlY2();
        double mx,my,d=20;
        mx = 0.5*0.5*0.5*p0x+3*0.5*0.5*0.5*p1x+3*0.5*0.5*0.5*p2x+0.5*0.5*0.5*p3x;
        my = 0.5*0.5*0.5*p0y+3*0.5*0.5*0.5*p1y+3*0.5*0.5*0.5*p2y+0.5*0.5*0.5*p3y;
        double a=-1,b=-1,tx,ty,dx=-1,dy=-1;
        if( p0x==p3x ){
            tx = mx+d;
            ty=my;
        }else if( p0y==p3y ){
            tx=mx;
            ty=my+d;
        }else{
            a=-(p3x-p0x)/(p3y-p0y);
            b=my-a*mx;
            dx=Math.sqrt(d*d/(1+a*a));
            dy=Math.sqrt(d*d-dx*dx);
            tx = mx+dx;
            ty = my+dy;
        }
        text.setLayoutX(tx-text.getWidth()/2);
        text.setLayoutY(ty-text.getHeight()/2);
    }

    private void fixControl(double mpx, double mpy, double mx, double my){
    	double ox = from.getCenterX(), oy = from.getCenterY();
        double alpha;
    	alpha = Math.atan2(my-oy, mx-ox)-Math.atan2(mpy-oy, mpx-ox);
    	double x1x, x1y, x2x, x2y, p1x, p1y, p2x, p2y;
    	x1x = (startX()-ox)*Math.cos(alpha)+(startY()-oy)*Math.sin(alpha)+ox;
    	x1y = -(startX()-ox)*Math.sin(alpha)+(startY()-oy)*Math.cos(alpha)+oy;
    	x2x = (endX()-ox)*Math.cos(alpha)+(endY()-oy)*Math.sin(alpha)+ox;
    	x2y = -(endX()-ox)*Math.sin(alpha)+(endY()-oy)*Math.cos(alpha)+oy;
    	p1x = (curve.getControlX1()-ox)*Math.cos(alpha)+(curve.getControlY1()-oy)*Math.sin(alpha)+ox;
    	p1y = -(curve.getControlX1()-ox)*Math.sin(alpha)+(curve.getControlY1()-oy)*Math.cos(alpha)+oy;
    	p2x = (curve.getControlX2()-ox)*Math.cos(alpha)+(curve.getControlY2()-oy)*Math.sin(alpha)+ox;
    	p2y = -(curve.getControlX2()-ox)*Math.sin(alpha)+(curve.getControlY2()-oy)*Math.cos(alpha)+oy;
    	startX(x1x); startY(x1y);
    	endX(x2x); endY(x2y);
    	curve.setControlX1( p1x );  curve.setControlY1( p1y );
        curve.setControlX2( p2x );  curve.setControlY2( p2y );

        reverseCurve.setControlX1( curve.getControlX2() );
        reverseCurve.setControlY1( curve.getControlY2() );
        reverseCurve.setControlX2( curve.getControlX1() );
        reverseCurve.setControlY2( curve.getControlY1() );
    }

    void fixControl(){ // linear line
        if( to!=null && to.equals(from) ){
            double x1x,x1y,x2x,x2y,p1x,p1y,p2x,p2y,ox,oy,d;
            ox = to.getCenterX(); oy = to.getCenterY();
            x2x = to.stateRadius*Math.cos(Math.PI/4)+ox;
            x2y = -to.stateRadius*Math.sin(Math.PI/4)+oy;
            x1x = to.stateRadius*Math.cos(3*Math.PI/4)+ox;
            x1y = -to.stateRadius*Math.sin(3*Math.PI/4)+oy;
            d = new Point2D(x1x,x1y).distance(x2x, x2y);
            p1x = x1x; p1y=x1y-d;
            p2x = x2x; p2y=x2y-d;
            
            startX(x1x); startY(x1y);
            endX(x2x); endY(x2y);
            curve.setControlX1( p1x );  curve.setControlY1( p1y );
            curve.setControlX2( p2x );  curve.setControlY2( p2y );

            reverseCurve.setControlX1( curve.getControlX2() );
            reverseCurve.setControlY1( curve.getControlY2() );
            reverseCurve.setControlX2( curve.getControlX1() );
            reverseCurve.setControlY2( curve.getControlY1() );
            return;
        }
        
        curve.setControlX1( startX()/2 + endX()/2 );
        curve.setControlY1( startY()/2 + endY()/2 );
        curve.setControlX2( curve.getControlX1() );
        curve.setControlY2( curve.getControlY1() );

        reverseCurve.setControlX1( curve.getControlX2() );
        reverseCurve.setControlY1( curve.getControlY2() );
        reverseCurve.setControlX2( curve.getControlX1() );
        reverseCurve.setControlY2( curve.getControlY1() );
    }

    private void fixControl(Point2D p0, Point2D p3, Point2D A){
        
        Point2D B;
        double bx,by;
        if( p0.getX()==p3.getX() ){
            bx = A.getX();
            by = p3.getY()-(A.getY()-p0.getY());
        }else if( p0.getY()==p3.getY() ){
            by = A.getY();
            bx = p3.getX()-(A.getX()-p0.getX());
        }else{
            double a,b,ap,bp,dx,dy;
            a=(p3.getY()-p0.getY())/(p3.getX()-p0.getX());
            b=A.getY()-a*A.getX();
            ap=-1/a;
            bp=(p0.getY()+p3.getY())/2-ap*(p0.getX()+p3.getX())/2;
            dx=(bp-b)/(a-ap);
            dy=a*dx+b;
            bx=2*dx-A.getX();
            by=2*dy-A.getY();
        }
        B = new Point2D(bx,by);
        
        double x,y;
        if( p0.getX()==p3.getX() ){
            x=p0.getX();
            y=A.getY();
        }else if( p0.getY()==p3.getY() ){
            x=A.getX();
            y=p0.getY();
        }else{
            double tmp = (p3.getY()-p0.getY())/(p3.getX()-p0.getX());
            x=A.getY()-p0.getY()+A.getX()/tmp+tmp*p0.getX();
            x/=tmp+1/tmp;
            y=tmp*x+p0.getY()-tmp*p0.getX();
        }
        double ta=p0.distance(x, y)/p0.distance(p3) ,
               tb=p3.distance(x, y)/p3.distance(p0);
        double a11=3*(1-ta)*(1-ta)*ta, a12=3*(1-ta)*ta*ta,
               a21=3*(1-tb)*(1-tb)*tb, a22=3*(1-tb)*tb*tb,
               b11=A.getX()-(1-ta)*(1-ta)*(1-ta)*p0.getX()-ta*ta*ta*p3.getX(),
               b12=A.getY()-(1-ta)*(1-ta)*(1-ta)*p0.getY()-ta*ta*ta*p3.getY(),
               b21=B.getX()-(1-tb)*(1-tb)*(1-tb)*p0.getX()-tb*tb*tb*p3.getX(),
               b22=B.getY()-(1-tb)*(1-tb)*(1-tb)*p0.getY()-tb*tb*tb*p3.getY();
        double p1x=(a22*b11-a12*b21)/(a11*a22-a12*a21),
               p1y=(a22*b12-a12*b22)/(a11*a22-a12*a21),
               p2x=(a11*b21-a21*b11)/(a11*a22-a12*a21),
               p2y=(a11*b22-a21*b12)/(a11*a22-a12*a21);
        curve.setControlX1( p1x );
        curve.setControlY1( p1y );
        curve.setControlX2( p2x );
        curve.setControlY2( p2y );

        reverseCurve.setControlX1( curve.getControlX2() );
        reverseCurve.setControlY1( curve.getControlY2() );
        reverseCurve.setControlX2( curve.getControlX1() );
        reverseCurve.setControlY2( curve.getControlY1() );
    }

    

    public final void textResize(){
        //text.setPrefWidth(textLength()); // why 7? Totally trial number.
        text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(),
                    text.getText(), 0.0D) + 10);
    }

    public void addTo(FSMUI p){
        p.mainPane.getChildren().addAll(this, polygon, text);
    }

    void removeFrom(FSMUI p){
        p.mainPane.getChildren().removeAll(this, polygon, text);
    }

    Polygon getPolygon() {
        return polygon;
    }

    public TextField getText() {
        return text;
    }

    public void setText(TextField text) {
        this.text = text;
    }
    
    public void startX(double x){
        start.setX(x);
        reverseCurve.setX(x);
    }

    public void startY(double y){
        start.setY(y);
        reverseCurve.setY(y);
    }

    public void endX(double x){
        curve.setX(x);
    }

    public void endY(double y){
        curve.setY(y);
    }

    public double startX(){
        return start.getX();
    }

    public double startY(){
        return start.getY();
    }

    public double endX(){
        return curve.getX();
    }

    public double endY(){
        return curve.getY();
    }

    private void hovered(){
        setStroke(Color.BLUE);
        polygon.setStroke(Color.BLUE);
        setOpacity(0.6);
        polygon.setOpacity(0.6);
        text.setStyle("-fx-background-color: "+texthc+";");
    }

    private void hoveredToDelete(){
        setStroke(Color.RED);
        polygon.setStroke(Color.RED);
        setOpacity(0.6);
        polygon.setOpacity(0.6);
        text.setStyle("-fx-background-color: rgba(255, 25, 28, 0.67);");
    }

    private void unHovered(){
        setStroke(Color.BLACK);
        polygon.setStroke(Color.BLACK);
        setOpacity(1);
        polygon.setOpacity(1);
        text.setStyle("-fx-background-color: "+textc+";");
    }
    
    @Override
    public String toString(){
        return "["+from+", "+to+"]";
    }
}
