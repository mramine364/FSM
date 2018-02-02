
package RegExp;

import Automate.State;
import Automate.Trans;

/**
 *
 * @author amine
 */
public class Eq {
    State s;
    Poly p;

    public Eq() {
        p = new Poly();
    }
    
    void applyArden(){
        int i=p.hasMono(s);
        if( i==-1 ) return;
        Mono m = p.l.remove(i);
//        System.out.println("mletter: "+m.letter);
        String str = Mono.addDot(m.letter);
//        System.out.println("res: "+str);
        for(Mono m2 : p.l){
            if( m2.letter.equals(Trans.Eps) )
                m2.letter = str;
            else
                m2.letter = str+"."+m2.letter;
        }
    }
    
    void applyArden2(){
        int i=p.hasMono(s);
        if( i==-1 ) return;
        Mono m = p.l.remove(i);
        String str = Mono.addDot(m.letter);
        for(Mono m2 : p.l){
            if( m2.letter.equals(Trans.Eps) )
                m2.letter = str;
            else
                m2.letter = m2.letter+"."+str;
        }
    }
    
    void add(Mono m){
        if( m.s==null ){
            p.l.add(0,m);
            return;
        }
        for(Mono m2 : p.l){
            if( m2.s!=null && m2.s.equals(m.s) ){
                m2.letter = "("+m2.letter+"|"+m.letter+")";
                return;
            }
        }
        p.l.add(m);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.s.getName()).append(" = ").append(p.toString());
        return s.toString();
    }
    
}
