
package File;

import Automate.FSM;
import Automate.State;
import Automate.Trans;
import AutomateUI.FSMUI;
import AutomateUI.StateUI;
import AutomateUI.TransLine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author amine
 */
public class Open {
    
    public static Fsmui open_fsmui(File fn, FSMUI mp){

        Fsmui o=null;
        try {
            FileInputStream fi = new FileInputStream(fn);
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Read object
            o = (Fsmui) oi.readObject();
            ArrayList<StateUI> ls = new ArrayList<>();
            for(stateui s: o.ls){
                StateUI st = s.getStateUI(mp);
                ls.add(st);
                mp.mainPane.getChildren().add(st);
                // System.out.println(s.getText().getText()+"("+s.getCenterX()+", "+s.getCenterY()+")");
            }
            //System.out.println("t:"+o.lt.size());
            for(transline t: o.lt){
                TransLine tr = t.getTransLine(ls, mp);
                tr.addTo(mp);
            }
            oi.close();
            fi.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return o;
    }
    
    public static FSM open_fsm(File fn){

        FSM af = new FSM();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fn));
            
            String line = br.readLine();
            ArrayList<Character> alphabet = new ArrayList<>();
            ArrayList<String> tal = new ArrayList<>(Arrays.asList(line.split(" ")));
            for(String t: tal)
                alphabet.add(t.charAt(0));
            af.setAlphabet(alphabet);
            
            HashMap<String, State> hs = new HashMap<>();
            while ((line=br.readLine()) != null && !line.equals("-")) {
                String[] st = line.split(" ");
                State state = new State();
                state.setAf(af);
                af.add(state);
                state.setName(st[0]);
                state.setInitial(Boolean.parseBoolean(st[1]));
                state.setFinal(Boolean.parseBoolean(st[2]));
                hs.put(st[0], state);
            }
            
            while ((line=br.readLine()) != null && !line.equals("")) {
                //System.out.println(" line="+line);
                String[] st = line.split(" ");
                Trans trans = new Trans();
                if(Trans.Eps.equals(st[0]))
                    trans.setEpsilon(true);
                else
                    trans.setName(st[0].charAt(0));
                trans.setFrom(hs.get(st[1]));
                trans.setTo(hs.get(st[2]));
                af.add(trans);
            }
            
            af.prepareStates();
        } catch (IOException ex) { ex.printStackTrace(); }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
        return af;
    }

    public static List<String> open_tests(File fn){
        List<String> tests = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fn));
            String line;
            while ((line = br.readLine())!=null){
                tests.add(line);
            }
        }
        catch (IOException ex) { ex.printStackTrace(); }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
        return tests;
    }
}
