
package RegExp;

import Automate.State;
import java.util.ArrayList;

/**
 *
 * @author amine
 */
public class Poly {
    ArrayList<Mono> l;

    public Poly() {
        l = new ArrayList<>();
    }
    
    int hasMono(State s){ // s != null
        int i=0;
        for(Mono m : l){
            if( m.s!=null && m.s.equals(s) )
                return i;
            i++;
        }
        return -1;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i=0;i<l.size();i++){
            Mono m = l.get(i);
            s.append(m.letter);
            if( m.s!=null )
                s.append("[").append(m.s.getName()).append("]");
            if( i<l.size()-1 )
                s.append("|");
        }
        return s.toString();
    }
    
}
