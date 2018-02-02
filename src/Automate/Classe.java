package Automate;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by amine_err on 30/07/2015.
 */
public class Classe {
    private ArrayList<State> ale;

    public Classe() { ale = new ArrayList<>(); }
    public Classe(ArrayList<State> ale) {
        this.ale = ale;
    }

    public boolean contain(State e){
        return ale.contains(e);
    }
    
    
    public void add(State e){
        ale.add(e);
    }
    public void addSet(State e){
        if( !(e == null || contain(e)) ){
            this.add(e);
        }
    }
    public void addSet(Classe c){
        for(State e : c.getAle()){
            this.addSet(e);
        }
    }

    public Classe epsilonFermeture(){
        if( isEmpty() )
            return new Classe();
        Classe c = new Classe();
        for(State e : getAle()){
            c.addSet(e.epsilonFermeture());
        }
        //System.out.println("this.gerale: "+this.getAle());
        State state = this.getAle().get(0);
        //System.out.println("state.getAf(): "+state.getAf());
        state.getAf().initializeSearch();
        return c;
    }

    public ArrayList<State> getAle() { return ale;  }
    public void setAle(ArrayList<State> ale) {
        this.ale = ale;
    }

    public boolean containInitial(){
        for(State e : ale){
            if( e.isInitial() )
                return true;
        }
        return false;
    }
    public boolean containFinale(){
        for(State e : ale){
            if( e.isFinal() )
                return true;
        }
        return false;
    }

    public Classe f(char str){
        Classe res = new Classe();
        for(State state : this.getAle()){
            res.addSet(state.f2(str));
        }
        Collections.sort(res.getAle(), (State o1, State o2) -> o1.getName().compareTo(o2.getName()));
        return res;
    }
    public boolean isEmpty(){
        return this.getAle().isEmpty();
    }
    public boolean haveSameStates(Classe c){
        if( getAle().size()!=c.getAle().size() )
            return false;

        for(State state : getAle()){
            if(!c.contain(state))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String str = "[";
        for(State state : getAle()){
            str += state.getName()+", ";
        }
        return str+"]";
    }
}
