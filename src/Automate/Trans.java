package Automate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amine_err on 30/07/2015.
 */
public class Trans implements Serializable {
    private State from;
    private State to;
    private char name;
    private boolean epsilon;
    public static final String Eps = "eps";
//    public static final String digit = "D"; // 0..9

    public Trans() {
    }

    public Trans(char name) {
        this.name = name;
    }

    public boolean isEpsilon() {
        return epsilon;
    }

    public void setEpsilon(boolean epsilon) {
        name = 0;
        this.epsilon = epsilon;
    }

    public State getFrom() {
        return from;
    }
    public void setFrom(State from) {
        this.from = from;
    }

    public State getTo() {
        return to;
    }
    public void setTo(State to) {
        this.to = to;
    }

    public char getName() {
        return name;
    }
    public void setName(char name) {
        this.name = name;
    }

    public static ArrayList<Trans> getTransitions(State from, State to){
        ArrayList<Trans> res = new ArrayList<>();
        ArrayList<Trans> alt = from.getTransitions();
        for(Trans trans : alt){
            if( trans.getTo().equals(to) ){
                res.add(trans);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return name + ", "+from/*.getName()*/+", "+to/*.getName()*/;
    }
}
