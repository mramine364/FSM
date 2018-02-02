package RegExp;

import APP.Controller;
import Automate.FSM;
import Automate.State;
import Automate.Trans;
import java.util.ArrayList;

/**
 * Created by amine_err on 02/08/2015.
 */
public class RE {
    String exp;
    ArrayList<Character> alphabet;
    
    public RE(){alphabet=new ArrayList<>();}
    public RE(String exp){this(); this.exp = exp;}
    public RE(String exp, ArrayList<Character> alphabet) {
        this.exp = exp;
        this.alphabet = alphabet;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(ArrayList<Character> alphabet) {
        this.alphabet = alphabet;
    }

//    public boolean isSimple(){
//        // return exp.equals(Trans.Epsilon) || alphabet.contains(exp);
//        return exp.length()==1 || Controller.bivs.containsKey(exp)
//                || Controller.dvs.containsKey(exp);
//    }

    private FSM toAF2(){
//        System.out.println(exp);
        if( exp.length()==1 ){
            FSM res = new FSM();
            State e = new State(); e.setInitial(true);
            State f = new State(); f.setFinal(true);
            Trans t = new Trans(exp.charAt(0));
            t.setFrom(e);
            t.setTo(f);
            e.addTansition(t);
            res.add(e);
            res.add(f);
            return res;
        }
        else if(Controller.bivs.containsKey(exp)){
            if( exp.equals(Trans.Eps) ){
                FSM res = new FSM();
                State e = new State(); e.setInitial(true);
                State f = new State(); f.setFinal(true);
                Trans t = new Trans();
                t.setEpsilon(true);
                t.setFrom(e);
                t.setTo(f);
                e.addTansition(t);
                res.add(e);
                res.add(f);
                return res;
            }
            else{
                FSM res = Controller.bivs.get(exp).copy(true);
                return res;
            }
        }
        else if(Controller.dvs.containsKey(exp)){
            FSM res = Controller.dvs.get(exp).copy(true);
            return res;            
        }
        else{
            
            for(int i=exp.length()-1;i>-1;i--){
                if( exp.charAt(i)==')' ){
                    int k = -1;
                    while( !(k==0 && exp.charAt(i)=='(') ){
                        i--;
                        if( exp.charAt(i)==')' ) k--;
                        else if( exp.charAt(i)=='(' ) k++;
                    } i--;
                }
                if( i>-1 && exp.charAt(i)=='|' ){
                    return 
                        new RE(exp.substring(0,i)).toAF2()
                        .unify(new RE(exp.substring(i+1)).toAF2());
                }
            }
            
            for(int i=exp.length()-1;i>-1;i--){
                if( exp.charAt(i)==')' ){
                    int k = -1;
                    while( !(k==0 && exp.charAt(i)=='(') ){
                        i--;
                        if( exp.charAt(i)==')' ) k--;
                        else if( exp.charAt(i)=='(' ) k++;
                    } i--;
                }
                if( i>-1 && exp.charAt(i)=='.' ){
                    FSM r = new RE(exp.substring(0,i)).toAF2();
                    FSM s = new RE(exp.substring(i + 1)).toAF2();
                    return r.merge(s);
                }
            }
            
            for(int i=exp.length()-1;i>-1;i--){
                if( exp.charAt(i)==')' ){
                    int k = -1;
                    while( !(k==0 && exp.charAt(i)=='(') ){
                        i--;
                        if( exp.charAt(i)==')' ) k--;
                        else if( exp.charAt(i)=='(' ) k++;
                    } i--;
                }
                if( i>-1 && exp.charAt(i)=='*' ){
                    return 
                        new RE(exp.substring(0,i)).toAF2().loop();
                }
            }
        }
        
        if( exp.charAt(0)=='(' && exp.charAt(exp.length()-1)==')' ){
            return new RE(exp.substring(1,exp.length()-1)).toAF2();
        }
        
        return null;
    }

    public FSM toAF() throws Exception{
        compile();
        FSM af = toAF2();
        af.setAlphabet(alphabet);
        ArrayList<State> states = af.getQ();
        int i=0;
        for (State state : states ){
            state.setName("e"+i);i++;
            state.setAf(af);
            //for(Trans trans : state.getTransitions()){
                //af.add(trans);
            //}
        }
        return af;
    }

    private void compile() throws Exception {
        //if syntax correct
        exp = exp.replaceAll(" ", "");
        // not empty
        if(exp.equals("")){
            throw new Exception("Empty RegExp Exception");
        }
        char[] ops = new char[]{'|','.','*'};
        // no 2 [*,.,|] in a row , *. or *| is ok      
        for(int i=0;i<ops.length-1;i++)
            for(int j=0;i<ops.length;i++)
                if( exp.contains(""+ops[i]+ops[j]) )
                    throw new Exception("'"+ops[i]+ops[j]+"' Exception");
        if( exp.contains("**") )
            throw new Exception("'**' Exception");
                
        // no start with [*,.,|]
        for(int i=0;i<ops.length;i++)
            if( exp.startsWith(""+ops[i]) )
                throw new Exception("Starting with '"+ops[i]+"' Exception");
        // no end with [.,|]
        for(int i=0;i<ops.length-1;i++)
            if( exp.endsWith(""+ops[i]) )
                throw new Exception("Ending with '"+ops[i]+"' Exception");

        // brackets compilation
        int k = 0;
        for(int i=0; i<exp.length(); i++){
            if( exp.charAt(i)=='(' ){
                k++;
            }
            if( exp.charAt(i)==')' ){
                k--;
            }
            if( k<0 ){
                throw new Exception("Mismatched Brackets Exception");
            }
        }
        if( k>0 ){
            throw new Exception("Mismatched Brackets Exception");
        }
        //if all variables exist
        
        StringBuilder s = new StringBuilder(exp);
        for(int i=0; i<s.length(); i++ ){
            if( s.charAt(i)=='*' || s.charAt(i)=='(' || s.charAt(i)==')' ){
                s.replace(i,i+1,"");
                i--;
            }else if( s.charAt(i)=='|' || s.charAt(i)=='.' ){
                s.replace(i,i+1,",");
                i--;
            }
        }
        String st[] = s.toString().split(",");
        for( String s1 : st ){
            if( s1.length()>1 ){
                if( Controller.bivs.containsKey(s1) ){
                    if(!s1.equals("eps"))
                        alphabet.addAll(Controller.bivs.get(s1).getAlphabet());
                    continue;
                }
                if( Controller.dvs.containsKey(s1) ){
                    alphabet.addAll(Controller.dvs.get(s1).getAlphabet());
                    continue;
                }
                throw new Exception("Undefined Varibale Exception: '"+s1+"' does not exist.");                     
            }
            else{
                alphabet.add(s1.charAt(0));
            }
        }
        // remove duplicates
        for(int i=0;i<alphabet.size();i++){            
            for(int j=i+1;j<alphabet.size();j++){
                if(alphabet.get(j)==alphabet.get(i)){
                    alphabet.remove(j);
                    j--;
                }
            }            
        }
    }
    
//    public static void main(String[] args){
//        //RE re = new RE();
//        AF af = new AF();
//        
//        //af.setAlphabet(alphabet);
//        
//        State s1 = new State();
//        s1.setName("e1");
//        s1.setInitial(true);
//        State s2 = new State();
//        s2.setName("e2");
//        s2.setFinal(true);
//        
//        Trans t = new Trans();
//        t.setEpsilon(true);
//        t.setFrom(s1);
//        t.setTo(s2);
//        
//        af.add(s1);
//        af.add(s2);
//        af.add(t);
//        
//        af.prepareStates();
//        
//        System.out.println("AF");
//        af.log();
//        
//        System.out.println("AFND");
//        af.getAFND().log();
//        
//        System.out.println("AFD");
//        af.getAFND().getAFD().log();
//        
//        System.out.println("AFDO");
//        af.getAFND().getAFD().getOptimizedAFD().log();
//        
//        System.out.println("RegExp");
//        System.out.println("d="+af.toRegExp());
//        System.out.println("a="+af.toRegExp2());
//        
//    }
}
