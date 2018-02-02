
package RegExp;

import Automate.State;

/**
 *
 * @author amine
 */
public class Mono {
    String letter;
    State s;

    public Mono() {
    }

    public Mono(String letter, State s) {
        this.letter = letter;
        this.s = s;
    }

    @Override
    public String toString() {
        return "Mono{" + "letter=" + letter + ", s=" + s + '}';
    }
    
    public static String addDot(String str){
        if( str.charAt(0)=='(' )
        {
            int k=1,i=1;
            while(i<str.length() && k!=0){
                if( str.charAt(i)=='(' ) k++;
                else if( str.charAt(i)==')' ) k--;
                i++;
            }
            if( k==0 && i==str.length() )
                return str+"*";
            else
                return "("+str+")*";
        }
        else{
            if( str.contains("*") || str.contains(".") || str.contains("|") ){
                return "("+str+")*";
            }else
                return str+"*";
        }
    }
}
