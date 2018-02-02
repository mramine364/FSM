
package File;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author amine
 */
public class Fsmui implements Serializable {
    public ArrayList<stateui> ls;
    public ArrayList<transline> lt;
    public boolean isregexp, isautomate;
    public String regexptext;
    public boolean isafne, isafnd, isafd, isafdo;
    public String trans_name;
    
    public ArrayList<variable> lv;
    
    
    public Fsmui(){
        ls = new ArrayList<>();
        lt = new ArrayList<>();
        lv = new ArrayList<>();
        isafdo = true;
    }
}
