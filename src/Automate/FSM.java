package Automate;


import RegExp.Sys;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class FSM implements Serializable {
    private ArrayList<Character> alphabet;
    private ArrayList<State> Q;
    private ArrayList<Trans> ts;    

    public FSM(){
        Q = new ArrayList<>();
        ts = new ArrayList<>();
        alphabet = new ArrayList<>();
    }
    public FSM copy(boolean prepare){
        FSM c = new FSM();
        for(char s : alphabet){
            c.alphabet.add(s);
        }
        for(State s : Q){
            State t = new State();
            t.setFinal(s.isFinal());
            t.setInitial(s.isInitial());
            t.setName(s.getName());
            //t.setName("e"+StateUI.ecount);
            c.add(t);
        }        
        for(Trans s : ts){
            Trans t = new Trans();            
            t.setName(s.getName());
            if(s.isEpsilon())
                t.setEpsilon(s.isEpsilon());
            else
                for(State _s : c.Q){
                    if(s.getFrom().getName().equals(_s.getName())){
                        t.setFrom(_s);
                    }
                    if(s.getTo().getName().equals(_s.getName())){
                        t.setTo(_s);
                    }
                }
            c.add(t);
        }
        for(State s : c.Q){
            s.setName("e"+(int)(Math.random()*(1000)+1000));
            //StateUI.ecount++;
        }
        if(prepare)
            c.prepareStates();
        //System.out.println("af copy");
        //c.log();
        //System.out.println("---");
        return c;
    }
    public ArrayList<Trans> getTs() {
        return ts;
    }
    public void setTs(ArrayList<Trans> ts) {
        this.ts = ts;
    }

    public void prepareStates(){
        for( Trans t : ts ){
            State e = t.getFrom();
            //System.out.println(e);
            e.addTansition(t);
        }
    }
    public void prepareTransitions(){
        for( State s : Q ){
            for( Trans t : s.getTransitions() ){
                add(t);
            }
        }
    }

    public boolean isAFNE(){
        for(State s:Q)
            for(Trans t:s.getTransitions())
                if(t.isEpsilon())
                    return true;
        return false;
    }
    public boolean isAFND(){
        return !isAFNE();
    }    
    public boolean isAFD(boolean complete){
        HashSet<Character> hs = new HashSet<>();
        for(State s:Q){
            hs.clear();
            for(Trans t:s.getTransitions()){
                if(t.isEpsilon())
                    return false;
                else
                    hs.add(t.getName());
            }
            if(complete){
                if(hs.size()!=s.getTransitions().size()) return false;
            }
            else
                if(hs.size()<s.getTransitions().size()) return false;            
        }
        return true;
    }
    public boolean isAFDComplete(){
        return isAFD(true);
    }

    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }
    public void setAlphabet(ArrayList<Character> alphabet) {
        Collections.sort(alphabet);
        this.alphabet = alphabet;
    }

    public ArrayList<State> getQ() {
        return Q;
    }
    public void setQ(ArrayList<State> q) {
        Q = new ArrayList<>();
        for(State state : q ){
            this.add(state);
        }
    }
    public void addE(ArrayList<State> q) {
        for(State state : q ){
            this.add(state);
        }
    }

    public State f(State e, char str){
        return e.f(str);
    }
    public State Initial(){
        for( State e : Q ){
            if( e.isInitial() )
                return e;
        }
        return null;
    }
    public ArrayList<State> Finaux(){
        ArrayList<State> ale = new ArrayList<>();
        for( State e : Q ){
            if( e.isFinal() )
                ale.add(e);
        }
        return ale;
    }
    public ArrayList<State> NonFinaux(){
        ArrayList<State> ale = new ArrayList<>();
        for( State e : Q ){
            if( !e.isFinal() )
                ale.add(e);
        }
        return ale;
    }
    public ArrayList<State> ReachedFromIntiale(){
        ArrayList<State> ale = new ArrayList<>();
        State E = this.Initial();
        for( State e : Q ){
            if( e.isReachedFrom(E) )
                ale.add(e);
            initializeSearch();
        }
        return ale;
    }
    public ArrayList<State> ReachFinal(){
        ArrayList<State> ale = new ArrayList<>();
        ArrayList<State> Ef = this.Finaux();
        for( State e : Q ){
            if( e.isFinal() )
                ale.add(e);
            else{
                for( State e2 : Ef ){
                    if( e2.isReachedFrom(e) ){
                        ale.add(e);
                        initializeSearch();
                        break;
                    }
                    initializeSearch();
                }
            }
        }
        return ale;
    }
    public void initializeSearch(){
        for( State e : Q )
            e.setSearched(false);
    }

    public FSM getOptimizedAFD(){

        FSM afd = new FSM();
        afd.setAlphabet(this.alphabet);
        ArrayList<State> i1 = this.ReachedFromIntiale();
        ArrayList<State> i2 = this.ReachFinal();
        afd.setQ(FSM.Intersection(i1, i2));
        Classe c1 = new Classe(); c1.setAle(afd.NonFinaux());
        Classe c2 = new Classe(); c2.setAle(afd.Finaux());
        Classes cs = new Classes(); cs.add(c1);cs.add(c2);
        Classes cs2 = new Classes(); cs2.setCs(cs);
        while( true ){
            for( Classe c : cs.getAlc() ){
                for( State e : c.getAle() ){
                    cs2.add(e);
                }
            }
            if( cs.nbrClasses() == cs2.nbrClasses() )
                break;
            else{
                cs = cs2;
                cs2 = new Classes(); cs2.setCs(cs);
            }
        }
        return cs2.toAFD();
    }

    public FSM getAFD(){
        Classes cs = new Classes();
        cs.add(Initial().toClasse());
        ArrayList<Character> Alphabet = this.getAlphabet();
        for(int i=0; i<cs.getAlc().size(); i++){
            Classe c = cs.getAlc().get(i);
            for(char s : Alphabet){
                Classe c2 = c.f(s);
                if( !c2.isEmpty() && !cs.contain(c2) ){
                    cs.add(c2);
                }
            }
        }
        return cs.toAFD2();
    }

    public FSM getAFND(){
        FSM afnd = new FSM();
        afnd.setAlphabet(getAlphabet());
        ArrayList<Character> Alphabet = getAlphabet();
        for(State state : getQ()){
            State e = new State();
            e.setName(state.getName());
            afnd.add(e);
            if(state.isInitial()){
                e.setInitial(true);
                Classe c = state.toClasse().epsilonFermeture();
                ArrayList<State> f = Finaux();
                for(State ee : f){
                    if( c.contain(ee) ){
                        e.setFinal(true);
                        break;
                    }
                }
            }
            if(state.isFinal())
                e.setFinal(true);
            
        }
        for(State state : getQ()){
            //System.out.println("AFND state="+state);
            
            for(char str : Alphabet){
                //System.out.println("state.toClasse: "+state.getAf());
                Classe c0 = state.toClasse().epsilonFermeture();
                //System.out.println("    state-eps="+c0);
                //System.out.println("    f(state-eps)="+c0.f(str));
                //System.out.println("    f(state-eps)-eps="+c0.f(str).epsilonFermeture());
                Classe c = state.toClasse().epsilonFermeture().f(str).epsilonFermeture();
                ArrayList<Trans> alt = new ArrayList<>();
                for(State e : c.getAle()){
                    Trans t = new Trans();
                    t.setName(str);
                    State ef = afnd.getQ().get(getQ().indexOf(state));
                    t.setFrom(ef);
                    t.setTo(afnd.getQ().get(getQ().indexOf(e)));
                    ef.addTansition(t);
                }
            }
        }
        return afnd;
    }

    private static ArrayList<State> Intersection(ArrayList<State> ale, ArrayList<State> ale2){
        ArrayList<State> res = new ArrayList<>();
        for( State e : ale ){
            if( ale2.contains(e) ){
                res.add(e);
            }
        }
        return res;
    }

    public void add(State state){
        state.setAf(this);
        Q.add(state);
    }
    public void add(Trans trans){
        this.ts.add(trans);
    }
    public void addT(ArrayList<Trans> trans){
        this.ts.addAll(trans);
    }
    public void add(FSM af){
        for (State state : af.getQ()){
            add(state);
        }
    }

    public FSM unify(FSM af){
        // initiaux
        State e = new State();
        e.setInitial(true);
        Trans t1 = new Trans(/*Trans.Epsilon*/);
        t1.setEpsilon(true);
        t1.setFrom(e);
        t1.setTo(Initial());
        e.addTansition(t1);
        Trans t2 = new Trans(/*Trans.Epsilon*/);
        t2.setEpsilon(true);
        t2.setFrom(e);
        t2.setTo(af.Initial());
        //System.out.println("e.transitions="+e.getTransitions());
        e.addTansition(t2);
        Initial().setInitial(false);
        af.Initial().setInitial(false);
        // final
        State f = new State();
        f.setFinal(true);
        ArrayList<State> ale = Finaux();
        for(State ee : ale){
            Trans t = new Trans(/*Trans.Epsilon*/);
            t.setEpsilon(true);
            t.setFrom(ee);
            t.setTo(f);
            ee.setFinal(false);
            ee.addTansition(t);
        }
        ArrayList<State> ale2 = af.Finaux();
        for(State ee : ale2){
            Trans t = new Trans(/*Trans.Epsilon*/);
            t.setEpsilon(true);
            t.setFrom(ee);
            t.setTo(f);
            ee.setFinal(false);
            ee.addTansition(t);
        }
        add(af);
        getQ().add(0,e);
        getQ().add(f);
        return this;
    }
    public FSM merge(FSM af){
        ArrayList<State> ale = Finaux();
        State ei = af.Initial();
        ei.setInitial(false);
        for(State e : ale){
            Trans t = new Trans(/*Trans.Epsilon*/);
            t.setEpsilon(true);
            t.setFrom(e);
            t.setTo(ei);
            e.setFinal(false);
            e.addTansition(t);
        }
        add(af);
        return this;
    }
    public FSM loop(){
        State i = new State();
        i.setInitial(true);
        State f = new State();
        f.setFinal(true);        
        ArrayList<State> ale = Finaux();
        State ei = Initial();
        for(State e : ale){
            Trans t = new Trans(/*Trans.Epsilon*/);
            t.setEpsilon(true);
            t.setFrom(e);
            t.setTo(ei);
            e.addTansition(t);
            Trans t2 = new Trans(/*Trans.Epsilon*/);
            t2.setEpsilon(true);
            t2.setFrom(e);
            t2.setTo(f);
            e.addTansition(t2);
            e.setFinal(false);
        }
        Trans t = new Trans(/*Trans.Epsilon*/);
        t.setEpsilon(true);
        t.setFrom(i);
        t.setTo(ei);
        i.addTansition(t);
        ei.setInitial(false);
        Trans t2 = new Trans(/*Trans.Epsilon*/);
        t2.setEpsilon(true);
        t2.setFrom(i);
        t2.setTo(f);
        i.addTansition(t2);
        getQ().add(0,i);
        getQ().add(f);
        return this;
    }
    public FSM complement(){
        for(State s : Q)
            s.setFinal(!s.isFinal());
        return this;
    }

    private boolean hasInitial(){
        for( State state : getQ() ){
            if( state.isInitial() ){
                return true;
            }
        }
        return false;
    }
    private boolean hasFinal(){
        for( State state : getQ() ){
            if( state.isFinal() ){
                return true;
            }
        }
        return false;
    }
    private boolean hasTwoInitials() {
        int count = 0;
        for( State state : getQ() ){
            if( state.isInitial() ){
                count++;
                if( count>1 ){
                    return true;
                }
            }
        }
        return /*count>1*/false;
    }

    public void compile() throws Exception {
        // not empty
        if( getQ().isEmpty() ){
            throw new Exception("No State Exception");
        }
        // has initiale etat
        if( !hasInitial() ){
            throw new Exception("No Initial State Exception");
        }
        // has finale etat
        if( !hasFinal() ){
            throw new Exception("No Final State Exception");
        }
        // does not have 2 initials
        if( hasTwoInitials() ){
            throw new Exception("Two Initials State Exception");
        }

        if( hasNoTransition() ){
            throw new Exception("No Transition Exception");
        }
        // alphabet check already done
        
    }

    private boolean hasNoTransition() {
        return ts.isEmpty();
    }

    public boolean isAccepted(String str){
        return isAccepted(str, Initial(), 0);
    }

    public boolean isAccepted(String str, State is, int i){
        if( str.length()==i && is.isFinal() ){
            return true;
        }
        for(Trans t : is.getTransitions()){
            if( i<str.length() && t.getName()==str.charAt(i) ){
                if( isAccepted(str, t.getTo(), i+1 ) )
                    return true;
            }
            else if( t.isEpsilon() ){
                if( isAccepted(str, t.getTo(), i) )
                    return true;
            }
        }
        return false;
    }
    
    public String toRegExp(){
        Sys sys = new Sys();
        for(State s : Q){
            sys.add(s);
        }
        return sys.resolve();
    }
    public String toRegExp2(){
        Sys sys = new Sys();
        sys.add2(Q);
        return sys.resolve2();
    }

    public void log(){
        ArrayList<State> states = getQ();
        states.forEach((state) -> {
            System.out.println(state.getName()+" "+state.isInitial()+" "+state.isFinal()+" "+state.getTransitions());
        });
    }
    public void log2(){
        System.out.println("states:"+Q.size());
        for(State s:Q)
            System.out.println(s.isInitial()+" "+s.isFinal()+" "+s);
        System.out.println("transitions:"+ts.size());
        for(Trans t:ts)
            System.out.println(t.isEpsilon()+" "+t.getName()+" "
                    + ""+t.getFrom()+" "+t.getTo());
    }
    
    
}
