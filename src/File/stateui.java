
package File;

import AutomateUI.FSMUI;
import AutomateUI.StateUI;
import java.io.Serializable;


public class stateui implements Serializable {
    
    private boolean isfinale;
    private boolean isinitial;
    private String text;
    private double centerx, centery;
    
    public stateui(StateUI s){
        isinitial = s.isInitiale();
        isfinale = s.isFinale();
        text = s.getText().getText();
        centerx = s.getCenterX();
        centery = s.getCenterY();
    }
    
    StateUI getStateUI(FSMUI af){
        String type="state";
        if(isinitial) type+="I";
        if(isfinale) type+="F";
        StateUI s = new StateUI(af, type, text);
        s.setCenterX(centerx);
        s.setCenterY(centery);
        s.makeDynamic(af);
        return s;
    }
}
