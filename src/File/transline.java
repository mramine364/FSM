
package File;

import AutomateUI.FSMUI;
import AutomateUI.StateUI;
import AutomateUI.TransLine;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author amine
 */
public class transline implements Serializable {

    private String text, from, to;
    private double textx, texty;
    private double startx, starty, endx, endy;
    private double controlx1, controlx2, controly1, controly2;
    
    public transline(TransLine t){
        startx = t.startX();
        starty = t.startY();
        endx = t.endX();
        endy = t.endY();
        text = t.getText().getText();
        textx = t.getText().getLayoutX();
        texty = t.getText().getLayoutY();
        controlx1 = t.getCurve().getControlX1();
        controlx2 = t.getCurve().getControlX2();
        controly1 = t.getCurve().getControlY1();
        controly2 = t.getCurve().getControlY2();
        from = t.getFrom().getText().getText();
        to = t.getTo().getText().getText();
    }
    
    TransLine getTransLine(ArrayList<StateUI> ls, FSMUI mp){

        TransLine t = new TransLine(mp);
        t.startX(startx);
        t.startY(starty);
        t.endX(endx);
        t.endY(endy);
        t.getText().setText(text);
        t.textResize();
        t.getText().setLayoutX(textx);
        t.getText().setLayoutY(texty);
        t.getCurve().setControlX1(controlx1);
        t.getCurve().setControlY1(controly1);
        t.getCurve().setControlX2(controlx2);
        t.getCurve().setControlY2(controly2);
        t.getReverseCurve().setControlX1(controlx2);
        t.getReverseCurve().setControlY1(controly2);
        t.getReverseCurve().setControlX2(controlx1);
        t.getReverseCurve().setControlY2(controly1);
        t.fixPolygon();
        for(StateUI s : ls){
            if( s.getText().getText().equals(from) ){
                t.setFrom(s);
            }
            if( s.getText().getText().equals(to) ){
                t.setTo(s);
            }
        }
        
        return t;
    }
}
