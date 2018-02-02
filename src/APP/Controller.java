package APP;

import AutomateUI.FSMUI;
import AutomateUI.TransLine;
import Test.TestController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import Automate.FSM;
import Automate.Trans;
import Automate.State;
import RegExp.RE;
import AutomateUI.StateUI;
import AutomateUI.Variable;
import File.Conf;
import File.Open;
import File.Save;
import File.Fsmui;
import File.stateui;
import File.transline;
import File.variable;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    public TabPane tabs;

    @FXML
    private TextField transition_name_txt;

    private static ArrayList<FSMUI> afuis = new ArrayList<>();
    
    private GridPane varPane;
    private static ObservableList<Variable> defined_var_data;
    public static HashMap<String, FSM> dvs;
    public static ObservableList<Variable> builtin_var_data;
    public static HashMap<String, FSM> bivs;
    private Stage stage_var, stage_test;

    //private Scene scene;
    private Stage stage;

    void init(Scene scene, Stage stage){
        //this.scene = scene;
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addNewFSMUI(new FSMUI(this));
        
        transition_name_txt.setOnKeyReleased((KeyEvent event) -> {
            String str = transition_name_txt.getText();
            //System.out.println("str="+str);
            if(str==null || str.isEmpty())
                TransLine.trans_name = TransLine.default_trans_name;
            else
                TransLine.trans_name = str;
        });
        
        load_varUI();
    }

    public void addNewFSMUI(FSMUI afui){
        afuis.add(afui);
        tabs.getTabs().add(afui.tab);
        tabs.getSelectionModel().selectLast();
        //return afui;
    }

    public void removeFSMUI(FSMUI afui){
        afuis.remove(afui);
        //for(AFUI afui1: afuis)
            //System.out.println(afui1.tab.getText());
    }

    private FSMUI getSelectedFSMUI(){
        FSMUI safui = null;
        for(FSMUI afui: afuis)
            if(afui.tab==tabs.getSelectionModel().getSelectedItem()){
                safui = afui;
                break;
            }
        return safui;
    }

    @FXML
    private void ClearUI(ActionEvent event){
        FSMUI afui = getSelectedFSMUI();
        afui.clear();
    }
    
    @FXML
    private void Show_Variables(ActionEvent event) throws IOException{
        System.out.println("show variables");        
        stage_var.show();
    }

    private void load_varUI(){

        TableView builtin_variables = new TableView<>();
        VBox.setVgrow(builtin_variables, Priority.ALWAYS);
        Label builtin_label = new Label("Built-in");
        builtin_label.setFont(new Font(18));
        builtin_label.setPadding(new Insets(5, 0, 0, 15));
        TableColumn builtin_vars = new TableColumn("Var");
        TableColumn builtin_values = new TableColumn("Expression");
        // associate tableview columns with Variable class properties
        builtin_vars.setCellValueFactory(new PropertyValueFactory("Var"));
        builtin_values.setCellValueFactory(new PropertyValueFactory("Expr"));
        builtin_variables.getColumns().addAll(builtin_vars, builtin_values);
        builtin_var_data = FXCollections.observableArrayList(
                new Variable("po", "."),
                new Variable("co", ","),
                new Variable("pi", "|"),
                new Variable("et", "*"),
                new Variable(Trans.Eps, ""),
                new Variable("lb", "("),
                new Variable("rb", ")"),
                new Variable("sp", " ")
        );
        builtin_variables.setItems(builtin_var_data);
        
        TableView defined_variables = new TableView<>();
        Label defined_label = new Label("Defined");
        defined_label.setFont(new Font(18));
        defined_label.setPadding(new Insets(5, 0, 0, 15));
        TableColumn defined_vars = new TableColumn("Var");
        TableColumn defined_epressions = new TableColumn("Expression");
        // associate tableview columns with Variable class properties
        defined_vars.setCellValueFactory(new PropertyValueFactory("Var"));
        defined_epressions.setCellValueFactory(new PropertyValueFactory("Expr"));
        defined_variables.getColumns().addAll(defined_vars, defined_epressions);
        defined_var_data = FXCollections.observableArrayList();
        defined_variables.setItems(defined_var_data);
        
        VBox builtin_varPane = new VBox();
        //builtin_varPane.setStyle("-fx-background-color: blue");
        builtin_varPane.setPrefWidth(160);
        //GridPane.setHgrow(builtin_varPane, Priority.NEVER);
        GridPane.setVgrow(builtin_varPane, Priority.ALWAYS);
        builtin_varPane.setSpacing(15);
        builtin_varPane.setPadding(new Insets(0,0,40,0));
        builtin_varPane.getChildren().addAll(builtin_label, builtin_variables);
        
        VBox defined_varPane = new VBox();
        GridPane.setHgrow(defined_varPane, Priority.ALWAYS);
        //GridPane.setVgrow(defined_varPane, Priority.ALWAYS);
        defined_varPane.setSpacing(15);
        Label var_dv_err = new Label("");
        var_dv_err.setPadding(new Insets(0, 5, 5, 0));
        var_dv_err.setTextFill(Color.web("#a94442"));
        TextField var_dv = new TextField();
        var_dv.setPromptText("Variable");
        TextField expr_dv = new TextField();
        expr_dv.setPromptText("Expression");
        HBox.setHgrow(expr_dv, Priority.ALWAYS);
        Button remove_dv = new Button("Remove");
        remove_dv.setMinWidth(60);
        remove_dv.setOnAction((ActionEvent event) -> {
            System.out.println("remove var");
            int selectedIndex = defined_variables.getSelectionModel().getSelectedIndex();
            if(selectedIndex!=-1){
                Variable v = defined_var_data.remove(selectedIndex);
                dvs.remove(v.get_var());
            }
        });
        Button add_dv = new Button("Add");
        add_dv.setMinWidth(40);
        add_dv.setOnAction((ActionEvent event) -> {
            String var = var_dv.getText();
            String expr = expr_dv.getText();
            if(var==null || var.isEmpty()){
                var_dv_err.setText("Variable can't be empty");
                return;
            }
            if(var.length()==1){
                var_dv_err.setText("Variable must contain at least 2 characters");
                return;
            }
            if(var.contains(".") || var.contains("|") || var.contains("*") || 
                    var.contains("(") || var.contains(")") ){
                var_dv_err.setText("Variable can't contain and operator");
                return;
            }
            if(var.contains(" ")){
                var_dv_err.setText("Variable can't have a space");
                return;
            }
            if(dvs.containsKey(var) || bivs.containsKey(var)){
                var_dv_err.setText("Variable already defined");
                return;
            }
            if(expr==null || expr.replaceAll(" ", "").isEmpty()){
                var_dv_err.setText("Expression can't be empty");
                return;
            }
            
            // parsing and compiling expression
            try {
                RE reg_exp = new RE(expr);
                FSM af = reg_exp.toAF().getAFND().getAFD().getOptimizedAFD();
                af.prepareTransitions();
                dvs.put(var, af);
                defined_var_data.add(new Variable(var, expr));
                //System.out.println("af="+af);
                //System.out.println("af.s0="+af.getQ().get(0).getAf());
                //System.out.println("var="+var+", alphabet="+af.getAlphabet()+", automate");
                //af.log2();
            } catch (Exception ex) {
                var_dv_err.setText(ex.getMessage());
                ex.printStackTrace();
            }
            
        });        
        HBox addPane = new HBox();
        addPane.setSpacing(10);
        addPane.setPadding(new Insets(0, 5, 0, 0));
        addPane.getChildren().addAll(var_dv, expr_dv, add_dv, remove_dv);
        defined_varPane.getChildren().addAll(defined_label, defined_variables, addPane, var_dv_err);

        varPane = new GridPane();
        //varPane.setStyle("-fx-background-color: red");
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow(Priority.NEVER);
        cc0.setMinWidth(160);
        varPane.getColumnConstraints().add(0, cc0);
        varPane.setHgap(25);
        varPane.add(builtin_varPane,0,0);
        varPane.add(defined_varPane,1,0);

        bivs = new HashMap<>();        
        dvs = new HashMap<>();

        for(Variable v : builtin_var_data){
            try {
                FSM af;
                //System.out.println("expr: "+v.getExpr());
                if(v.get_var().equals(Trans.Eps)){
                    af = new FSM();
                    State s1 = new State();
                    s1.setName("e1");
                    s1.setInitial(true);
                    s1.setFinal(true);
                    af.add(s1);                    
                }
                else if(v.get_expr().length()==1){
                    af = new FSM();
                    ArrayList<Character> al = new ArrayList<>();
                    al.add(v.get_expr().charAt(0));
                    af.setAlphabet(al);
                    State s1 = new State(), s2 = new State();
                    s1.setName("e1"); s2.setName("e2");
                    s1.setInitial(true);
                    s2.setFinal(true);
                    af.add(s1); af.add(s2);
                    Trans t = new Trans(v.get_expr().charAt(0));
                    t.setFrom(s1); t.setTo(s2);
                    af.add(t);
                    af.prepareStates();
                }
                else{                    
                    RE reg_exp = new RE(v.get_expr());
                    af = reg_exp.toAF().getAFND().getAFD().getOptimizedAFD();
                }                
                bivs.put(v.get_var(), af);                
                //af.log();
            } catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Scene scene = new Scene(varPane);
        if(stage_var==null)
            stage_var = new Stage();
        stage_var.setTitle("Variables");
        stage_var.setScene(scene);
    }
    
    @FXML
    public void SaveUI(ActionEvent event) throws IOException{
        System.out.println("save.");

        FSMUI safui = getSelectedFSMUI();

        String tfn = tabs.getSelectionModel().getSelectedItem().getText();
        String fn = tfn.endsWith("*")?tfn.substring(0,tfn.length()-1):tfn;

        if(safui.fileui==null || !safui.fileui.exists()){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save DFA");
            File fpath = new File(Conf.path);
            fileChooser.setInitialDirectory(fpath);
            // System.getProperty("user.home")
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(Conf.ui_description, Conf.ui_extentions)
            );
            //String filename = "afui_"+Long.toString(System.currentTimeMillis())+".ser";
            String filename = fn+".ser";
            fileChooser.setInitialFileName(filename);
            safui.fileui = fileChooser.showSaveDialog(stage);

            fn = safui.fileui.getName().substring(0, safui.fileui.getName().lastIndexOf(".ser"));
        }

        if( safui.fileui!=null ){
            System.out.println("file: "+safui.fileui);
            Fsmui o = new Fsmui();

            for( Object obj : safui.mainPane.getChildren() ){
                if( obj.getClass().getSimpleName().equals(StateUI.class.getSimpleName()) ){
                    StateUI stateUI = (StateUI) obj;
                    o.ls.add(new stateui(stateUI));
                }
               else if( obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                    TransLine t = (TransLine) obj;
                    o.lt.add(new transline(t));
                }
            }
            o.isafd = safui.afd.isSelected();
            o.isafnd = safui.afnd.isSelected();
            o.isafdo = safui.afdo.isSelected();
            o.isafne = safui.afne.isSelected();
            o.isregexp = safui.regExp.isSelected();
            o.isautomate = !safui.regExp.isSelected(); // automate.isSelected();
            o.regexptext = safui.regExpText.getText();
            o.trans_name = TransLine.trans_name;
            
            for(Variable v : defined_var_data){
                variable tv = new variable();
                tv.Var = v.get_var();
                tv.Expr = v.get_expr();
                tv.af = dvs.get(v.get_var());
                o.lv.add(tv);
            }

            Save.save_fsmui(safui.fileui, o);
            safui.saved(fn);
        }
               
    }
    
    @FXML
    private void OpenUI(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open DFA");
        File fpath = new File(Conf.path);
        fileChooser.setInitialDirectory(fpath);
        // System.getProperty("user.home")
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java Objects (*.ser)", "*.ser")
        );
        File file = fileChooser.showOpenDialog(stage);
        if( file!=null ){
            System.out.println("file: "+file);
            String afuiName;
            int ti;
            if((ti=file.getName().lastIndexOf(".ser"))!=-1)
                afuiName = file.getName().substring(0, ti);
            else
                afuiName = file.getName();
            FSMUI afui = new FSMUI(this, afuiName);
            afui.fileui = file;

            Fsmui o = Open.open_fsmui(file, afui);
            //System.out.println("o:"+o);
            afui.afd.setSelected(o.isafd);
            afui.afdo.setSelected(o.isafdo);
            afui.afnd.setSelected(o.isafnd);
            afui.afne.setSelected(o.isafne);
            afui.regExp.setSelected(o.isregexp);
            afui.automate.setSelected(!o.isregexp); //o.isautomate;
            afui.regExpText.setText(o.regexptext);
            if(!o.trans_name.equals(TransLine.default_trans_name))
                transition_name_txt.setText(o.trans_name);
            TransLine.trans_name = o.trans_name;

            addNewFSMUI(afui);
            
            for(variable v : o.lv){
                Variable tv = new Variable(v.Var, v.Expr);
                defined_var_data.add(tv);
                dvs.put(v.Var, v.af);
            }
            
        }        
    }

    @FXML
    private void NewUI(ActionEvent event) {
        addNewFSMUI(new FSMUI(this));
    }
    
    @FXML
    private void SaveFSM(ActionEvent event) throws IOException, Exception{
        System.out.println("save af.");

        FSMUI safui = getSelectedFSMUI();

        String tfn = tabs.getSelectionModel().getSelectedItem().getText();
        String fn = tfn.endsWith("*")?tfn.substring(0,tfn.length()-1):tfn;

        if(safui.filefsm==null || !safui.filefsm.exists()){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save FSM");
            File fpath = new File(Conf.path);
            fileChooser.setInitialDirectory(fpath);
            // System.getProperty("user.home")
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(Conf.fsm_description, Conf.fsm_extentions)
            );
            //String filename = "afui_"+Long.toString(System.currentTimeMillis())+".ser";
            String filename = fn+".txt";
            fileChooser.setInitialFileName(filename);
            safui.filefsm = fileChooser.showSaveDialog(stage);

            fn = safui.filefsm.getName().substring(0, safui.filefsm.getName().lastIndexOf(".txt"));
        }

        if( safui.filefsm!=null ){
            FSM af = safui.getFSM();
            Save.save_fsm(af, fn);
        }
    }
    
    @FXML
    private void OpenFSM(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open FSM");
        File fpath = new File(Conf.path);
        fileChooser.setInitialDirectory(fpath);
        // System.getProperty("user.home")
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(Conf.fsm_description, Conf.fsm_extentions)
        );
        File file = fileChooser.showOpenDialog(stage);
        if( file!=null ){
            System.out.println("file: "+file);
            FSM o = Open.open_fsm(file);
            //System.out.println("o:"+o);
            String afuiName;
            int ti;
            if((ti=file.getName().lastIndexOf(".txt"))!=-1)
                afuiName = file.getName().substring(0, ti);
            else
                afuiName = file.getName();

            FSMUI afui = FSMUI.toFSMUI(o, this, afuiName);
            afui.filefsm = file;
            addNewFSMUI(afui);
        }
    }

    @FXML
    private void TestFSM(ActionEvent event) throws IOException {
        if(stage_test==null)
            stage_test = new Stage();
        //Parent root = FXMLLoader.load(getClass().getResource("../Test/test.fxml"));
        //stage_test.setTitle("Testing FSM");
        //stage_test.setScene(new Scene(root));
        //stage_test.show();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../Test/test.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        TestController controller = fxmlLoader.getController();
        controller.init(stage_test);
        stage_test.setTitle("Testing FSM");
        stage_test.setScene(scene);
        stage_test.show();
    }

    @FXML
    private void SaveAsVariable(ActionEvent event) throws Exception {
        FSMUI fsmui = getSelectedFSMUI();
        FSM fsm = fsmui.getFSM();

        String tfn = tabs.getSelectionModel().getSelectedItem().getText();
        String fn = tfn.endsWith("*")?tfn.substring(0,tfn.length()-1):tfn;
        dvs.put(fn, fsm);
        defined_var_data.add(new Variable(fn, fsm.toRegExp()));
    }

}