package Automate;

import java.util.ArrayList;

/**
 * Created by amine_err on 30/07/2015.
 */
public class Classes {
    private Classes cs;
    private ArrayList<Classe> alc;

    public Classes() {
        alc = new ArrayList<>();
    }

    public Classes getCs() {
        return cs;
    }
    public void setCs(Classes cs) {
        this.cs = cs;
    }

    public ArrayList<Classe> getAlc() {
        return alc;
    }
    public void setAlc(ArrayList<Classe> alc) {
        this.alc = alc;
    }

    public void add(Classe c){
        alc.add(c);
    }
    public Classe getClasse(State e){
        for( Classe c : alc ){
            if( c.contain(e) )
                return c;
        }
        return null;
    }
    public ArrayList<Classe> getNextsClasses(State e){
        ArrayList<State> ale =  e.getNexts();
        ArrayList<Classe> alc = new ArrayList<>();
        for( State ee : ale ){
            alc.add(getClasse(ee));
        }
        return alc;
    }
    public void add(State e){
        for( int i=0; i<alc.size(); i++ ){
            Classe c = alc.get(i);
            for( int j=0; j<c.getAle().size(); j++ ){
                State e2 = c.getAle().get(j);
                if( cs.getNextsClasses(e).equals(cs.getNextsClasses(e2)) && cs.getClasse(e).equals(cs.getClasse(e2)) ){
                    c.add(e);return;
                }
            }
        }
        Classe c = new Classe();
        c.add(e);
        alc.add(c);
    }
    public int nbrClasses(){
        return alc.size();
    }

    public FSM toAFD(){
        FSM res = new FSM();
        //System.out.println("AF.toAFD");
        //System.out.println(getAlc().get(0).getAle().get(0).getAf());        
        res.setAlphabet(getAlc().get(0).getAle().get(0).getAf().getAlphabet());
        int i=0;
        for( Classe c : this.getAlc() ){
            State state = new State(); state.setName("e"+i);i++;
            if( c.containInitial() )
                state.setInitial(true);
            if( c.containFinale() )
                state.setFinal(true);
            res.add(state);
        }
        ArrayList<Character> Alphabet = res.getAlphabet();
        for( Classe c : this.getAlc() ){
            ArrayList<Classe> alc = this.getNextsClasses(c.getAle().get(0));
            ArrayList<Trans> ts = new ArrayList<>();
            int k=0;
            for( Classe c2 : alc ){
                if( c2 != null ){
                    Trans t = new Trans();
                    State et = res.getQ().get(this.getAlc().indexOf(c));
                    t.setFrom(et);
                    State ef = res.getQ().get(this.getAlc().indexOf(c2));
                    t.setTo(ef);
                    t.setName(Alphabet.get(k));
                    ts.add(t);
                }
                k++;
            }
            res.getQ().get(this.getAlc().indexOf(c)).setTransitions(ts);
        }
        return res;
    }

    public FSM toAFD2(){

        FSM res = new FSM();
        res.setAlphabet(getAlc().get(0).getAle().get(0).getAf().getAlphabet());
        int i=0;
        for( Classe c : this.getAlc() ){
            State state = new State(); state.setName("e"+i);i++;
            if( c.containFinale() )
                state.setFinal(true);
            if( c.containInitial() && c.getAle().size()==1 )
                state.setInitial(true);
            res.add(state);
        }

        ArrayList<Character> Alphabet = res.getAlphabet();
        for( Classe c : this.getAlc() ){
            ArrayList<Trans> ts = new ArrayList<>();
            for( char s : Alphabet ){
                Classe c2 = c.f(s);
                if( !c2.isEmpty() ){
                    Trans t = new Trans();
                    t.setName(s);
                    t.setFrom(res.getQ().get(this.getAlc().indexOf(c)));
                    t.setTo(res.getQ().get(this.indexOf2(c2)));
                    ts.add(t);
                }
            }
            res.getQ().get(this.getAlc().indexOf(c)).setTransitions(ts);
        }
        return res;
    }

    public int indexOf2(Classe c){
        int i=0;
        for(Classe classe : getAlc()){
            if(classe.haveSameStates(c))
                return i;
            i++;
        }
        return -1;
    }

    public boolean contain(Classe c) {
        for(Classe classe : getAlc()){
            if(classe.haveSameStates(c))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String str = "(";
        for(Classe classe : getAlc())
            str += classe+", ";
        return str+")";
    }
}
