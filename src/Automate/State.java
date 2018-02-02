package Automate;

import AutomateUI.StateUI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class State implements Serializable {
    private boolean Final;
    private boolean Initial;
    private String name;
    private boolean searched;
    private FSM af;
    private ArrayList<Trans> transitions;
    transient private StateUI stateUI;

    public State(){ transitions = new ArrayList<>(); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State other = (State) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public StateUI getStateUI() {
        return stateUI;
    }

    public void setStateUI(StateUI stateUI) {
        this.stateUI = stateUI;
    }

    public FSM getAf() {
        return af;
    }
    public void setAf(FSM af) {
        this.af = af;
    }    

    public State f(char str){
        for(Trans t : transitions){
            if( t.getName()==str )
                return t.getTo();
        }
        return null;
    }
    public Classe f2(char str){
        Classe res = new Classe();
        for(Trans t : transitions){
            if( !t.isEpsilon() && t.getName()==str )
                res.addSet(t.getTo());
        }
        return res;
    }
    public Classe f3(){
        Classe res = new Classe();
        for(Trans t : transitions){
            if( t.isEpsilon() )
                res.addSet(t.getTo());
        }
        return res;
    }
    
    public Classe epsilonFermeture(){
        if( isSearched() )
            return new Classe();
        Classe c = new Classe();
        c.addSet(this); this.setSearched(true);
        Classe c2 = f3();
        if( !c2.isEmpty() ){
            for(State e : c2.getAle()){
                c.addSet(e.epsilonFermeture());
            }
        }
        return c;
    }

    public ArrayList<State> getNexts(){
        ArrayList<State> ale = new ArrayList<>();
        ArrayList<Character> als = this.getAf().getAlphabet();
        for(char str : als){
            ale.add(this.f(str));
        }
        return ale;
    }
    public boolean isReachedFrom(State e){
        if( e == null )
            return false;
        if( e.isSearched() )
            return false;
        if( this.equals(e) )
            return true;
        e.setSearched(true);
        ArrayList<State> nexts = e.getNexts();
        for(State ee : nexts){
            if( isReachedFrom(ee) )
                return true;
        }
        return false;
    }

    public Classe toClasse(){
        Classe c = new Classe();
        c.add(this);
        return c;
    }

    public boolean isFinal() {
        return Final;
    }
    public void setFinal(boolean aFinal) {
        Final = aFinal;
    }

    public boolean isInitial() {
        return Initial;
    }
    public void setInitial(boolean initial) {
        Initial = initial;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSearched() {
        return searched;
    }
    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public ArrayList<Trans> getTransitions() {
        return this.transitions;
    }
    public void setTransitions(ArrayList<Trans> transitions) {
        this.transitions = transitions;
        Collections.sort(this.transitions, (Trans o1, Trans o2) ->{ 
            if(o1.getName()==0 || o2.getName()==0)
                return 0;
            return o1.getName()-o2.getName();
        });    }
    public void addTansition(Trans t){
        transitions.add(t);
        Collections.sort(transitions, (Trans o1, Trans o2) ->{ 
            if(o1.getName()==0 || o2.getName()==0)
                return 0;
            return o1.getName()-o2.getName();
        });
    }
    public void removeTransition(Trans t){
        this.transitions.remove(t);
    }

    @Override
    public String toString() {
        return getName();
    }
}
