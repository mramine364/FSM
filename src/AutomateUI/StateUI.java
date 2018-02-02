package AutomateUI;

import Automate.State;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import java.util.ArrayList;


public class StateUI extends Pane {

    double stateRadius = 24;
    private StateUI This = this;
    private Circle circle;
    private Circle circle2;
    private Line line;
    private Line line2;
    private Line line3;
    private TextField text;
    private State state;
    private boolean positioned = false;
    private ArrayList<TransLine> transLines;

    // Drawing
    double velocity_x,velocity_y, net_force_x,net_force_y;

    private double lastMouseEventX;
    private double lastMouseEventY;


//    @Override
//    public String toString() {
//        return ""
//                + "StateUI{" + "stateRadius=" + stateRadius + ", "
//                + "This=" + "" + ", circle=" + circle + ", "
//                + "circle2=" + circle2 + ", line=" + line + ", "
////                + "line2=" + line2 + ", line3=" + line3 + ", "
////                + "text=" + text + ", state=" + state + ", "
////                + "positioned=" + positioned + ", transLines=" + transLines + ", "
////                + "velocity_x=" + velocity_x + ", velocity_y=" + velocity_y + ", "
////                + "net_force_x=" + net_force_x + ", net_force_y=" + net_force_y + ", "
//                + "level=" + level + '}';
//    }
    
    ArrayList<TransLine> getTransLines() {
        return transLines;
    }

    public void setTransLines(ArrayList<TransLine> transLines) {
        this.transLines = transLines;
    }

    public boolean isPositioned() {
        return positioned;
    }

    public void setPositioned(boolean positioned) {
        this.positioned = positioned;
    }

    public State getState() {
        if( state == null ){
            state = new State();
            state.setInitial( isInitiale() );
            state.setFinal( isFinale() );
        }
        state.setName(text.getText());        
        return state;
    }

    public TextField getText() {
        return text;
    }

    public void setText(TextField text) {
        this.text = text;
    }

    public void setState(State state) {

        this.state = state;
    }

    public boolean isInitiale(){
        return line != null;
    }

    public boolean isFinale(){
        return circle2!=null;
    }

    Circle getCircle() {
        return circle;
    }

    public StateUI() {
    }

    StateUI(FSMUI afui) {
        this(afui, "State");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
    }

    StateUI(FSMUI afui, final String type) {
        this(afui,type,"e"+afui.ecount);
        afui.ecount++;
    }
    public StateUI(FSMUI afui, final String type, String sname) {
        // type : [State, Statei, Stateif, Statef]
        transLines = new ArrayList<>();
        
        velocity_x=velocity_y=0;
        net_force_y=net_force_x=0;

        circle = new Circle();
        circle.setFill(Paint.valueOf("#d84aff1a")); 
        //stateRadius = 24
        circle.setLayoutX(29); circle.setLayoutY(27); circle.setRadius(24);
        circle.setStroke(Paint.valueOf("#580882")); 
        circle.setStrokeType(StrokeType.valueOf("INSIDE"));

        text = new TextField();
        text.setText(sname);
        text.setPrefWidth(24 * 1.4);
        text.setPrefHeight(24 * 0.7);
        text.setStyle("-fx-background-color: rgba(246, 241, 255, 0.27);");
        text.setLayoutX(circle.getLayoutX() - text.getPrefWidth() / 2);
        text.setLayoutY(circle.getLayoutY() - text.getPrefHeight() / 2);
        text.setPadding(new Insets(0));
        text.setAlignment(Pos.CENTER);
        text.setOnMouseClicked((MouseEvent event) -> {
            text.setEditable(!text.isEditable());
            afui.change();
        });
        text.setMouseTransparent(true);

        if( type.equalsIgnoreCase("StateI") ){
            setPrefHeight(51); setPrefWidth(53);
            line = new Line();
            line.setEndX(20); line.setLayoutX(-6); line.setLayoutY(5.2); 
            line.setRotate(65); line.setStartX(10);
            line2 = new Line();
            line2.setEndX(20); line2.setLayoutX(-9); line2.setLayoutY(8.5); 
            line2.setRotate(19); line2.setStartX(10); line2.setStrokeMiterLimit(6);
            line3 = new Line();
            line3.setEndX(15); line3.setLayoutX(-2); line3.setLayoutY(5); line3.setRotate(43);
            getChildren().addAll(line,line2,line3,circle,text);
        }else if( type.equalsIgnoreCase("StateIF") ){
            setPrefHeight(51); setPrefWidth(53);
            line = new Line();
            line.setEndX(20); line.setLayoutX(-6); line.setLayoutY(5.2); 
            line.setRotate(65); line.setStartX(10);
            line2 = new Line();
            line2.setEndX(20); line2.setLayoutX(-9); line2.setLayoutY(8.5); 
            line2.setRotate(19); line2.setStartX(10); line2.setStrokeMiterLimit(6);
            line3 = new Line();
            line3.setEndX(15); line3.setLayoutX(-2); line3.setLayoutY(5); line3.setRotate(43);
            circle2 = new Circle();
            circle2.setFill(Paint.valueOf("#d84dff14")); circle2.setLayoutX(29); 
            circle2.setLayoutY(27); circle2.setRadius(20);
            circle2.setStroke(Paint.valueOf("#592872d9")); 
            circle2.setStrokeType(StrokeType.valueOf("INSIDE"));
            getChildren().addAll(line,line2,line3,circle2,circle,text);
        }else if( type.equalsIgnoreCase("StateF") ){
            setPrefHeight(48); setPrefWidth(48);
            circle2 = new Circle();
            circle2.setFill(Paint.valueOf("#d84dff14")); circle2.setLayoutX(29); 
            circle2.setLayoutY(27); circle2.setRadius(20);
            circle2.setStroke(Paint.valueOf("#592872d9")); 
            circle2.setStrokeType(StrokeType.valueOf("INSIDE"));
            getChildren().addAll(circle2,circle,text);
        }else{
            setPrefHeight(48); setPrefWidth(48);
            getChildren().addAll(circle,text);
        }

        circle.setOnMouseEntered((MouseEvent event) -> {
            if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                setOpacity(0.6);
                circle.setFill(Paint.valueOf("#550000"));
                if (type.equalsIgnoreCase("StateIF")) {
                    circle2.setFill(Paint.valueOf("#550000"));
                } else if (type.equalsIgnoreCase("StateF")) {
                    circle2.setFill(Paint.valueOf("#550000"));
                }
            } else {
                setOpacity(0.6);
            }
            text.setMouseTransparent(!afui.paletteOn.equalsIgnoreCase(""));
        });

        circle.setOnMouseExited((MouseEvent event) -> {
            setOpacity(1);
            circle.setFill(Paint.valueOf("#d84aff1a"));
            if( type.equalsIgnoreCase("StateIF") ){
                circle2.setFill(Paint.valueOf("#d84dff14"));
            }else if( type.equalsIgnoreCase("StateF") ){
                circle2.setFill(Paint.valueOf("#d84dff14"));
            }
        });

        circle.setOnMouseClicked((MouseEvent event) -> {
            // System.out.println("circle.setOnMouseClicked");
            if( afui.paletteOn.equalsIgnoreCase("Delete") ){
                // remove trans related to the circle                
                for(int i=0; i<afui.mainPane.getChildren().size(); i++ ){
                    Object obj = afui.mainPane.getChildren().get(i);
                    if( obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                        TransLine tl = (TransLine) obj;
                        if( tl.getFrom().equals(This) || tl.getTo().equals(This) ){
                            tl.removeFrom(afui);
                            i--;
                        }
                    }
                }
                // remove circle
                afui.mainPane.getChildren().remove(This);
            }
        });

        circle.setOnMousePressed((MouseEvent event) -> {
            if (afui.paletteOn.equalsIgnoreCase("Trans")) {
                final TransLine line1 = new TransLine(afui);
                //System.out.println("ex="+event.getX()+", ey="+event.getY());
                line1.startX(getCenterX()+event.getX());
                line1.startY(getCenterY()+event.getY());
                line1.endX(getLayoutX());
                line1.endY(getLayoutY());
                line1.addTo(afui);
                line1.setFrom(This);
                afui.mainPane.setOnMouseDragged((MouseEvent event1) -> {
                    afui.cursor.setLayoutX(event1.getX() -FSMUI.cursorWidth/2);
                    afui.cursor.setLayoutY(event1.getY() -FSMUI.cursorHeight/2);
                    line1.endX(event1.getX());
                    line1.endY(event1.getY());
                    line1.fixPolygon();
                    line1.fixText();
                    line1.fixControl();
                });
                afui.mainPane.setOnMouseReleased((MouseEvent event1) -> {
                    afui.mainPane.setOnMouseDragged((MouseEvent event2) -> {
                        afui.cursor.setLayoutX(event2.getX() - FSMUI.cursorWidth / 2);
                        afui.cursor.setLayoutY(event2.getY() - FSMUI.cursorHeight / 2);
                    });
                    afui.mainPane.setOnMouseReleased(null);
                    // if released on a circle
                    boolean removeLine = true;
                    for (Object obj : afui.mainPane.getChildren()) {
                        if (obj.getClass().getSimpleName().equals(StateUI.class.getSimpleName())) {
                            StateUI stateUI = (StateUI) obj;
                            Point2D O = new Point2D(stateUI.getLayoutX()+ stateUI.getCircle().getLayoutX(), stateUI.getLayoutY()+ stateUI.getCircle().getLayoutY());
                            Point2D M = new Point2D(line1.endX(), line1.endY());
                            if (O.distance(M)<=stateRadius) {
                                line1.setTo(stateUI);
                                removeLine = false;
                                line1.fixControl();
                                line1.fixText();
                                line1.fixPolygon();
                                break;
                            }
                        }
                    }
                    if (removeLine) {
                        line1.removeFrom(afui);
                        line1.setFrom(null);
                    } else {
                        line1.setOnMouseClicked((MouseEvent event2) -> {
                            if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                                line1.removeFrom(afui);
                                afui.change();
                            }
                        });
                        line1.getText().setOnMouseClicked((MouseEvent event3) -> {
                            if (afui.paletteOn.equalsIgnoreCase("Delete")) {
                                line1.removeFrom(afui);
                                afui.change();
                            }
                            else if (afui.paletteOn.equalsIgnoreCase("")) {
                                line1.getText().setEditable(!line1.getText().isEditable());
                                afui.change();
                            }
                        });
                        afui.change();
                    }
                });
            }
        });

        //makeDynamic(afui);
        //afui.mainPane.getChildren().add(this);

    }

    public void setCenterX(double x){
        setLayoutX(x-getCircle().getLayoutX());
    }
    public void setCenterY(double y){
        setLayoutY(y - getCircle().getLayoutY());
    }

    public double getCenterX(){
        return getLayoutX()+getCircle().getLayoutX();
    }
    public double getCenterY(){
        return getLayoutY()+getCircle().getLayoutY();
    }
    
    public void makeDynamic(FSMUI afui){
                
        final ArrayList<TransLine> alt = new ArrayList<>();
        final ArrayList<TransLine> alt2 = new ArrayList<>();
        setOnMousePressed((MouseEvent event1) -> {
            alt.clear();
            alt2.clear();
            alt.addAll(afui.getTransRelatedTo(this));
            alt2.addAll(afui.getTransRelatedTo2(this));
            lastMouseEventX = getLayoutX()+event1.getX();
            lastMouseEventY = getLayoutY()+event1.getY();
            //System.out.println("ex: "+event1.getX()+", ey:"+event1.getY());
            //System.out.println("glx: "+getLayoutX()+", gly:"+getLayoutY());
            //System.out.println("lx: "+afui.lastMouseEventX+", ly:"+afui.lastMouseEventY);
            //System.out.println("alt "+alt);
            /*alt.stream().forEach(transLine -> System.out.println(transLine.getFrom().getText().getText()+" -> " +
                    transLine.getTo().getText().getText()));*/
            //System.out.println("alt2 "+alt2);
            /*alt2.stream().forEach(transLine -> System.out.println(transLine.getFrom().getText().getText()+" -> " +
                    transLine.getTo().getText().getText()));*/
        });
        setOnMouseDragged((MouseEvent event1) -> {

            if (afui.paletteOn.equalsIgnoreCase("")) {
                double pmouseX = getLayoutX()+event1.getX();
                double pmouseY = getLayoutY()+event1.getY();
                double dX = pmouseX-lastMouseEventX,
                       dY = pmouseY-lastMouseEventY;

                setLayoutX(getLayoutX() +dX);
                setLayoutY(getLayoutY() +dY);

                //System.out.print("x:"+event1.getSceneX()+", y:"+event1.getSceneY());
                //System.out.println("; dx:"+dX+", dy:"+dY);
                //System.out.println("; newScale:"+afui.newScale);

                lastMouseEventX = pmouseX;
                lastMouseEventY = pmouseY;

                for( TransLine transline : alt ){
                    if( transline!=null && transline.getPolygon()!=null ){
                        transline.endX(transline.endX() + dX);
                        transline.endY(transline.endY() + dY);
                        transline.fixPolygon();
                        transline.fixControl();
                        transline.fixText();
                    }
                }
                for( TransLine transline : alt2 ){
                    if( transline!=null && transline.getPolygon()!=null ){
                        transline.startX(transline.startX() + dX);
                        transline.startY(transline.startY() + dY);
                        transline.fixPolygon();
                        transline.fixControl();
                        transline.fixText();
                    }
                }
            }

            afui.cursor.setLayoutX(lastMouseEventX -FSMUI.cursorWidth/2);
            afui.cursor.setLayoutY(lastMouseEventY -FSMUI.cursorHeight/2);
        });
    }
}
