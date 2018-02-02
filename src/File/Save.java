
package File;

import Automate.FSM;
import Automate.Trans;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author amine
 */
public class Save {

    public static void save_fsmui(File file, Fsmui o){
             
        try (ObjectOutputStream oos =
                new ObjectOutputStream(
                        new FileOutputStream(file))) {            
            oos.writeObject(o);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }
    
    public static void save_fsm(FSM o, String filename){
             
        //String filename = "af_"+Long.toString(System.currentTimeMillis())+".txt";
        try {  
            PrintWriter writer = new PrintWriter(Conf.path+filename+".txt", "UTF-8");
            //oos.writeObject(o);
            StringBuilder res=new StringBuilder();
            
            res.append(o.getAlphabet().get(0));
            for(int i=1;i<o.getAlphabet().size();i++){
                res.append(" ").append(o.getAlphabet().get(i));
            }
            res.append("\n");
            
            for(int i=0;i<o.getQ().size();i++){
                res.append(o.getQ().get(i).getName()).append(" ")
                   .append(o.getQ().get(i).isInitial()).append(" ")
                   .append(o.getQ().get(i).isFinal()).append("\n");
            }
            res.append("-");
            res.append("\n");
            
            for(int i=0;i<o.getQ().size();i++){
                for(int j=0;j<o.getQ().get(i).getTransitions().size();j++){
                    Trans t = o.getQ().get(i).getTransitions().get(j);
                    res.append(t.isEpsilon()?Trans.Eps:t.getName()).append(" ")
                        .append(t.getFrom().getName()).append(" ")
                        .append(t.getTo().getName()).append("\n");
                }                
            }
            
            System.out.println("save_fsm Done");
            
            writer.print(res);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }

    public static void save_tests(List<String> tests, String path){

        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            for(String test : tests){
                writer.println(test);
            }
            System.out.println("save_tests Done");
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
