
package RegExp;

import APP.Controller;
import Automate.State;
import Automate.Trans;
import AutomateUI.Variable;
import java.util.ArrayList;

/**
 *
 * @author amine
 */
public class Sys {
    ArrayList<Eq> eqs;

    public Sys() {
        eqs = new ArrayList<>();
    }
    
    public String resolve(){
        for(int i=eqs.size()-1;i>-1;i--){
            Eq e = eqs.get(i);
//            System.out.println("e i: "+e.p.l);
            e.applyArden();
//            System.out.println("e f: "+e.p.l);
            for(int j=i-1;j>-1;j--){
                Eq e2 = eqs.get(j);
//                System.out.println("e2 i: "+e2.p.l);
                int k = e2.p.hasMono(e.s);
                if( k==-1 ) continue;
                Mono m = e2.p.l.remove(k);
                for(Mono m2 : e.p.l){
                    Mono nm = new Mono();
                    nm.s = m2.s;
                    if( m.letter.equals(Trans.Eps) ){
                        if( m2.letter.equals(Trans.Eps) ){
                            nm.letter = Trans.Eps;
                        }else{
                            nm.letter = m2.letter;
                        }
                    }else{
                        if( m2.letter.equals(Trans.Eps) ){
                            nm.letter = m.letter;
                        }else{
                            nm.letter = m.letter+"."+m2.letter;
                        }
                    }
                    e2.add(nm);
                }
//                System.out.println("e2 f: "+e2.p.l);
            }
        }
        return eqs.get(0).p.toString();
    }
    
    public String resolve2(){
        for(int i=0;i<eqs.size();i++){
            Eq e = eqs.get(i);
//            System.out.println("e i: "+e.p.l);
            e.applyArden2();
//            System.out.println("e f: "+e.p.l);
            for(int j=i+1;j<eqs.size();j++){
                Eq e2 = eqs.get(j);
//                System.out.println("e2 i: "+e2.p.l);
                int k = e2.p.hasMono(e.s);
                if( k==-1 ) continue;
                Mono m = e2.p.l.remove(k);
                for(Mono m2 : e.p.l){
                    Mono nm = new Mono();
                    nm.s = m2.s;
                    if( m.letter.equals(Trans.Eps) ){
                        if( m2.letter.equals(Trans.Eps) ){
                            nm.letter = Trans.Eps;
                        }else{
                            nm.letter = m2.letter;
                        }
                    }else{
                        if( m2.letter.equals(Trans.Eps) ){
                            nm.letter = m.letter;
                        }else{
                            nm.letter = m2.letter+"."+m.letter;
                        }
                    }
                    e2.add(nm);
                }
//                System.out.println("e2 f: "+e2.p.l);
            }
        }
        return eqs.get(eqs.size()-1).p.toString();
    }
    
    public void add(State s){
        Eq eq = new Eq();
        eq.s = s;
        if( s.isFinal())
            eq.add(new Mono(Trans.Eps, null));
        for(Trans t : s.getTransitions()){
            Mono m = new Mono();
            m.s = t.getTo();
            if(t.isEpsilon())
                m.letter = Trans.Eps;
            else{
                boolean vf=false;
                for(Variable v : Controller.builtin_var_data){
                    if(!v.get_expr().isEmpty() && v.get_expr().charAt(0)==t.getName()){
                        m.letter = v.get_var();
                        vf=true;
                        break;
                    }
                }
                if(!vf)
                    m.letter = t.getName()+"";
            }
            eq.add( m );
        }
        if( s.isInitial() )
            eqs.add(0,eq);
        else
            eqs.add(eq);
    }
    // slower
    public void add2(ArrayList<State> Q){
        Eq eq[] = new Eq[Q.size()+1];
        int k=0,i=0;
        for(State s : Q){
            eq[i]=new Eq();
            eqs.add(eq[i]);
            eq[i].s = s;
            i++;            
        }
        eq[eq.length-1] = new Eq();
        eqs.add(eq[eq.length-1]);
        eq[eq.length-1].s = new State();
        eq[eq.length-1].s.setName("e4386837205668069376");
        for(State s : Q){
            if(s.isInitial()){
                eq[k].add(new Mono(Trans.Eps, null));
            }
            k++;
            for(Trans t : s.getTransitions()){
                for(i=0;i<eq.length-1;i++){
                    if( eq[i].s.equals(t.getTo()) ){
                        Mono m =new Mono();
                        m.s = t.getFrom();
                        
                        if(t.isEpsilon())
                            m.letter = Trans.Eps;
                        else{
                            boolean vf=false;
                            for(Variable v : Controller.builtin_var_data){
                                if(!v.get_expr().isEmpty() && v.get_expr().charAt(0)==t.getName()){
                                    m.letter = v.get_var();
                                    vf=true;
                                    break;
                                }
                            }
                            if(!vf)
                                m.letter = t.getName()+"";
                        }
                        
                        eq[i].add(m);
                        break;
                    }
                }
            }
            if( s.isFinal() ){
                eq[eq.length-1].add(new Mono(Trans.Eps, s));
            }
        }        
    }
    
}
