
import Automate.FSM;
import Automate.State;
import Automate.Trans;
import java.util.ArrayList;


public class MainTest {
    
    char c;
    public static void main(String[] args){
//        String s = "\\";
//        System.out.println(s);
//        System.out.println(s.length());
            
        //test_afe_to_afnd2();
        
        //MainTest o = new MainTest();
        //System.out.println("c="+o.c);
        //System.out.println("c="+(int)o.c);
        //System.out.println("c="+(o.c==0));
        //System.out.println("c="+(Character.getName(o.c)==null));
        //System.out.println("c="+Character.getNumericValue(o.c));
        
        Character a='1', b='1';
        System.out.println(a==b);

    }
    
    static void test_afe_to_afnd(){
        FSM af = new FSM();
        
        ArrayList<Character> alp = new ArrayList<>();
        alp.add('a'); alp.add('b'); alp.add('c');
        af.setAlphabet(alp);
        
        State s1 = new State(); s1.setName("e1"); s1.setInitial(true);
        State s2 = new State(); s2.setName("e2"); 
        State s3 = new State(); s3.setName("e3"); 
        State s4 = new State(); s4.setName("e4"); s4.setFinal(true);
        
        Trans t1 = new Trans(); t1.setName('a'); t1.setFrom(s1); t1.setTo(s2);
        Trans t2 = new Trans(); t2.setName('a'); t2.setFrom(s2); t2.setTo(s2);
        Trans t3 = new Trans(); t3.setName('b'); t3.setFrom(s2); t3.setTo(s3);
        Trans t4 = new Trans(); t4.setEpsilon(true); t4.setFrom(s2); t4.setTo(s3);
        Trans t5 = new Trans(); t5.setEpsilon(true); t5.setFrom(s2); t5.setTo(s4);
        Trans t6 = new Trans(); t6.setName('b'); t6.setFrom(s3); t6.setTo(s3);
        Trans t7 = new Trans(); t7.setName('c'); t7.setFrom(s3); t7.setTo(s4);
        Trans t8 = new Trans(); t8.setEpsilon(true); t8.setFrom(s3); t8.setTo(s4);
        Trans t9 = new Trans(); t9.setName('c'); t9.setFrom(s4); t9.setTo(s4);

        af.add(s1); af.add(s2); af.add(s3); af.add(s4);
        
        af.add(t1); af.add(t2); af.add(t3); af.add(t4); af.add(t5); af.add(t6); 
        af.add(t7); af.add(t8); af.add(t9); 
        
        af.prepareStates();
        
        //af.log2();        
        System.out.println("AFED");
        af.log();
        
        System.out.println("AFND");
        af.getAFND().log();        
    }
    
    static void test_afe_to_afnd2(){
        FSM af = new FSM();
        
        ArrayList<Character> alp = new ArrayList<>();
        alp.add('*');
        af.setAlphabet(alp);
        
        State s0 = new State(); s0.setName("e0"); s0.setInitial(true);
        State s1 = new State(); s1.setName("e1"); s1.setFinal(true);
        State s2 = new State(); s2.setName("e2"); 
        State s3 = new State(); s3.setName("e3"); 
        State s4 = new State(); s4.setName("e4"); s4.setFinal(true);
        
        Trans t1 = new Trans(); t1.setEpsilon(true); t1.setFrom(s0); t1.setTo(s2);
        Trans t2 = new Trans(); t2.setName('*'); t2.setFrom(s2); t2.setTo(s3);
        Trans t3 = new Trans(); t3.setEpsilon(true); t3.setFrom(s3); t3.setTo(s4);
        Trans t4 = new Trans(); t4.setEpsilon(true); t4.setFrom(s4); t4.setTo(s1);

        af.add(s0); af.add(s1); af.add(s2); af.add(s3); af.add(s4);
        
        af.add(t1); af.add(t2); af.add(t3); af.add(t4);
        
        af.prepareStates();
        
        //af.log2();        
        System.out.println("AFED");
        af.log();
        
        System.out.println("AFND");
        af.getAFND().log();
    }

}
