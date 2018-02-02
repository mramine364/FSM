package AutomateUI;

import APP.Controller;
import Automate.FSM;
import Automate.State;
import Automate.Trans;
import RegExp.RE;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static SF.SF.getMSecond;

public class FSMUI {

    public Tab tab;
    //private ScrollPane msp;
    public Pane mainPane;
    //private Group mgroup;
    private Pane middlePane;
    private Slider zoom;
    private GridPane spane;
    private ScrollPane rightPane;
    private GridPane palette;
    public RadioButton regExp, automate;
    public TextField regExpText;
    public RadioButton afne, afnd, afd, afdo;
    ToggleButton statei, state, stateif, statef, trans, delete;
    Button convertBtn;
    Pane cursor;

    private boolean _saved = true;
    public File fileui;
    public File filefsm;

    private final String stateImgUrl = "../IMG/state.png";
    private final String stateFImgUrl = "../IMG/final.png";
    private final String stateIImgUrl = "../IMG/init_state.png";
    private final String stateIFImgUrl = "../IMG/init_final_state.png";
    private final String transImgUrl = "../IMG/trans.png";
    private final String deleteImgUrl = "../IMG/DeleteRed.png";

    private static int count = 1;
    private static double initWidth=530, initHeight=483;
    static double cursorWidth = 22;
    static double cursorHeight = 22;

    String paletteOn = "";
    private double newScale = 1;
    int ecount;

    public FSMUI(Controller controller){
        this(controller, "untitled "+count++);
    }

    public FSMUI(Controller controller, String afuiName){

        tab = new Tab();
        tab.setText(afuiName);

        tab.setOnClosed(event -> {
            controller.removeFSMUI(this);
        });

        tab.setOnCloseRequest(event -> {
            if(!isSaved()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Save Changes?");
                String we = tab.getText().endsWith("*")?tab.getText().substring(0,tab.getText().length()-1):tab.getText();
                alert.setHeaderText(we+" has been modified, save changes?");

                ButtonType Yes = new ButtonType("Yes");
                ButtonType No = new ButtonType("No");
                ButtonType Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(Yes, No, Cancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == Yes){
                    // ... user chose "Yes"
                    try {
                        controller.SaveUI(null);
                        controller.removeFSMUI(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (result.get() == No) {
                    // ... user chose "No"
                    controller.removeFSMUI(this);
                }
                else {
                    // ... user chose CANCEL or closed the dialog
                    event.consume();
                }
            }
        });

        spane = new GridPane();
        //GridPane.setHgrow(spane, Priority.NEVER);
        //GridPane.setVgrow(spane, Priority.NEVER);

        //msp = new ScrollPane();
        //msp.setStyle("-fx-background-color: blue");
        //spane.add(msp,0,0);
        //ccs0.setHgrow(Priority.NEVER);
        //GridPane.setHgrow(msp, Priority.ALWAYS);
        //GridPane.setVgrow(msp, Priority.ALWAYS);

        mainPane = new Pane();
        mainPane.setStyle("-fx-background-color: white;");
        //mgroup = new Group(mainPane);
        //mgroup.setStyle("-fx-background-color: red");
        //msp.setContent(mgroup);
        //mainPane.prefHeightProperty().bind(msp.heightProperty());
        //mainPane.prefWidthProperty().bind(msp.widthProperty());

        spane.add(mainPane,0,0);
        ColumnConstraints ccs0 = new ColumnConstraints();
        spane.getColumnConstraints().add(0, ccs0);
        GridPane.setHgrow(mainPane, Priority.ALWAYS);
        GridPane.setVgrow(mainPane, Priority.ALWAYS);

        //mainPane.setOnScroll(event -> {
            //System.out.println("onscroll");
            //System.out.println("dx="+event.getDeltaX()+", dy="+event.getDeltaY());
            //if(event.getDeltaY()>0){
                //System.out.println("scroll up");
                //newScale += zoom.getMinorTickCount()/zoom.getMax();
                //if(newScale>2)
                    //newScale=2;
            //}
            //else if(event.getDeltaY()<0){
                //System.out.println("scroll down");
                //newScale -= zoom.getMinorTickCount()/zoom.getMax();
                //if(newScale<.1)
                    //newScale=.1;
            //}
            //System.out.println("x="+event.getX()+", y="+event.getY());
            //updateScale(newScale, newScale, event.getX(), event.getY());
            //zoom.setValue(newScale*100);
        //});

        cursor = new Pane();
        mainPane.getChildren().add(cursor);
        cursor.setPrefHeight(cursorHeight);
        cursor.setPrefWidth(cursorWidth);
        cursor.setStyle("-fx-background-color: grey;");
        cursor.setOpacity(0.6);
        cursor.setMouseTransparent(true);

        middlePane = new Pane();
        middlePane.setPrefWidth(40);
        zoom = new Slider();
        zoom.setMinorTickCount(5);
        zoom.setOrientation(Orientation.VERTICAL);
        zoom.setPrefWidth(40);
        zoom.setPrefHeight(400);
        zoom.setShowTickLabels(true);
        zoom.setShowTickMarks(true);
        zoom.setMax(200);
        zoom.setValue(100);

        GridPane.setHgrow(zoom, Priority.NEVER);
        GridPane.setVgrow(zoom, Priority.ALWAYS);
        middlePane.getChildren().add(zoom);
        spane.add(middlePane,1,0);
        ColumnConstraints ccs = new ColumnConstraints();
        ccs.setMinWidth(40);
        ccs.setHgrow(Priority.NEVER);
        spane.getColumnConstraints().add(1, ccs);

        tab.setContent(spane);

        middlePane.setStyle("-fx-background-color: #fff0f9");
        zoom.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if(mainPane.prefHeightProperty().isBound() || mainPane.prefWidthProperty().isBound()) {
                mainPane.prefHeightProperty().unbind();
                mainPane.prefWidthProperty().unbind();
                //GridPane.setHgrow(msp, Priority.NEVER);
                //GridPane.setVgrow(msp, Priority.NEVER);
            }
            newScale = (double)newValue/100;
            newScale = (newScale<.1)?.1:newScale;
            //System.out.println("newscale: "+newScale);
            updateScale(newScale, newScale,mainPane.getWidth()/2,mainPane.getHeight()/2);
            //updateScale(newScale, newScale,0,0);
        });

        mainPane.setOnMouseMoved((MouseEvent event) -> {
            cursor.setLayoutX(event.getX() -cursorWidth/2);
            cursor.setLayoutY(event.getY() -cursorHeight/2);
            //System.out.print("scene x:"+event.getSceneX()+", y:"+event.getSceneY());
            //System.out.print(", x:"+event.getX()+", y:"+event.getY()); // Yep
            //System.out.println(",screen x:"+event.getScreenX()+", y:"+event.getScreenY());
        });
        mainPane.setOnMouseDragged((MouseEvent event) -> {
            //System.out.println("mainpanedragged");
            cursor.setLayoutX(event.getX() -cursorWidth/2);
            cursor.setLayoutY(event.getY() -cursorHeight/2);
        });

        mainPane.setOnMouseClicked((MouseEvent event) -> {
            //System.out.println("(w, h) = ("+mainPane.getWidth()+", "+mainPane.getHeight()+")");
            //System.out.println("(sw, sh) = ("+msp.getWidth()+", "+msp.getHeight()+")");
            //System.out.println("(w, h) = ("+mainPane.getWidth()+", "+mainPane.getHeight()+")");
            //System.out.println("(pw, ph) = ("+mainPane.getPrefWidth()+", "+mainPane.getPrefHeight()+")");
            if( paletteOn.equalsIgnoreCase("stateI") ){
                final StateUI p = new StateUI(this,"stateI");
                // System.out.println("p: "+p);
                p.setLayoutX(event.getX() - p.getPrefWidth()/2);
                p.setLayoutY(event.getY() - p.getPrefHeight()/2);

                p.makeDynamic(this);
                mainPane.getChildren().add(p);

                change();
            }else if( paletteOn.equalsIgnoreCase("state") ){
                final StateUI p = new StateUI(this);
                p.setLayoutX(event.getX() -p.getPrefWidth()/2);
                p.setLayoutY(event.getY() -p.getPrefHeight()/2);

                p.makeDynamic(this);
                mainPane.getChildren().add(p);

                change();
            } else if( paletteOn.equalsIgnoreCase("stateIF") ){
                final StateUI p = new StateUI(this, "stateIF");
                p.setLayoutX(event.getX() -p.getPrefWidth()/2);
                p.setLayoutY(event.getY() -p.getPrefHeight()/2);

                p.makeDynamic(this);
                mainPane.getChildren().add(p);

                change();
            } else if( paletteOn.equalsIgnoreCase("stateF") ){
                final StateUI p = new StateUI(this, "stateF");
                p.setLayoutX(event.getX() -p.getPrefWidth()/2);
                p.setLayoutY(event.getY() -p.getPrefHeight()/2);

                p.makeDynamic(this);
                mainPane.getChildren().add(p);

                change();
            }
        });

        /* right pane */
        rightPane = new ScrollPane();
        rightPane.setPrefHeight(552);
        rightPane.setPrefWidth(190);
        spane.add(rightPane,2,0);
        ColumnConstraints ccs2 = new ColumnConstraints();
        ccs2.setMinWidth(190);
        ccs2.setHgrow(Priority.NEVER);
        spane.getColumnConstraints().add(2, ccs2);
        GridPane.setHalignment(rightPane, HPos.CENTER);
        GridPane.setVgrow(rightPane, Priority.NEVER);
        rightPane.setStyle("-fx-background-color: snow;");

        VBox vBox = new VBox();
        rightPane.setContent(vBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setMaxWidth(175);
        vBox.setPadding(new Insets(0,2,5,2));

        VBox vBox1 = new VBox();
        vBox.getChildren().add(vBox1);
        vBox1.setSpacing(3);

        Label accepted_label = new Label("Is accepted: ");
        TextField accepted_txt = new TextField();
        vBox1.getChildren().addAll(accepted_label, accepted_txt);
        HBox hBox1 = new HBox();
        vBox1.getChildren().add(hBox1);
        hBox1.setSpacing(3);
        TextArea accepted_res = new TextArea();
        hBox1.getChildren().add(accepted_res);
        accepted_res.setPrefHeight(30);
        accepted_res.setEditable(false);
        accepted_res.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-text-fill: blue");
        Button accepted_btn = new Button("Check");
        accepted_btn.setMinWidth(50);
        hBox1.getChildren().add(accepted_btn);
        accepted_btn.setOnAction((event)->{
            String[] arr_str = accepted_txt.getText().split(",");
            StringBuilder res_str = new StringBuilder();
            try {
                int i;
                boolean r;
                FSM af = getFSM();
                for(i=0;i<arr_str.length-1;i++){
                    r = af.isAccepted(arr_str[i]);
                    res_str.append(r?"True":"False").append(",");
                }
                r = af.isAccepted(arr_str[i]);
                res_str.append(r?"True":"False");
                accepted_res.setText(res_str.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox vBox12 = new VBox();
        vBox.getChildren().add(vBox12);
        vBox12.setSpacing(3);

        Label draw_att_label = new Label("Attraction: ");
        draw_att_label.setPrefWidth(90);
        TextField draw_att_txt = new TextField("0.01");
        HBox hBox2 = new HBox();
        vBox12.getChildren().add(hBox2);
        hBox2.getChildren().addAll(draw_att_label, draw_att_txt);
        Label draw_rep_label = new Label("Repulsion: ");
        draw_rep_label.setPrefWidth(90);
        TextField draw_rep_txt = new TextField("200");
        HBox hBox3 = new HBox();
        vBox12.getChildren().add(hBox3);
        hBox3.getChildren().addAll(draw_rep_label, draw_rep_txt);
        Label draw_dam_label = new Label("Damping: ");
        draw_dam_label.setPrefWidth(90);
        TextField draw_dam_txt = new TextField("0.85");
        HBox hBox4 = new HBox();
        vBox12.getChildren().add(hBox4);
        hBox4.getChildren().addAll(draw_dam_label, draw_dam_txt);
        Button redraw = new Button("Redraw");
        vBox12.getChildren().add(redraw);
        redraw.setOnAction((event)->{
            double repulsion, attraction, damping;
            try{
                repulsion = Double.parseDouble(draw_rep_txt.getText());
                attraction = Double.parseDouble(draw_att_txt.getText());
                damping = Double.parseDouble(draw_dam_txt.getText());
                Draw(repulsion, attraction, damping);
            }catch(Exception ex){
                //System.out.println("reDraw Error: "+ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox vBox13 = new VBox();
        vBox.getChildren().add(vBox13);
        vBox13.setSpacing(3);

        Label dep_label = new Label("From Start:");
        TextField dep_txt = new TextField();
        dep_txt.setEditable(false);
        dep_txt.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-text-fill: blueviolet");
        Label arr_label = new Label("From End:");
        TextField arr_txt = new TextField();
        arr_txt.setEditable(false);
        arr_txt.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-text-fill: brown");
        Button toRE_btn = new Button("Regular Expression");
        vBox13.getChildren().addAll(dep_label, dep_txt, arr_label, arr_txt, toRE_btn);
        toRE_btn.setOnAction(event -> {
            try {
                FSM af = getFSM();
                dep_txt.setText(af.toRegExp());
                arr_txt.setText(af.toRegExp2());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox vBox2 = new VBox();
        vBox.getChildren().add(vBox2);
        vBox2.setSpacing(3);

        ToggleGroup typeInput = new ToggleGroup();
        regExp = new RadioButton();
        vBox2.getChildren().add(regExp);
        regExp.setToggleGroup(typeInput);
        regExp.setMnemonicParsing(false);
        regExp.setText("Regular Exp");

        regExpText = new TextField();
        vBox2.getChildren().add(regExpText);
        regExpText.setPrefSize(153, 25);

        VBox vBox3 = new VBox();
        vBox.getChildren().add(vBox3);
        vBox3.setSpacing(3);

        automate = new RadioButton();
        vBox3.getChildren().add(automate);
        automate.setToggleGroup(typeInput);
        automate.setMnemonicParsing(false);
        automate.setSelected(true);
        automate.setText("Automate");

        palette = new GridPane();
        vBox3.getChildren().add(palette);
        palette.setAlignment(Pos.CENTER);
        palette.setOpacity(.82);
        GridPane.setVgrow(palette, Priority.NEVER);
        palette.setStyle("-fx-border-width: 2px; -fx-border-color: teal; -fx-background-color: snow;");

        statei = new ToggleButton();
        palette.add(statei,0,0);
        statei.setMnemonicParsing(false);
        statei.setPrefSize(44,22);
        GridPane.setHalignment(statei, HPos.CENTER);

        state = new ToggleButton();
        palette.add(state,1,0);
        state.setMnemonicParsing(false);
        state.setPrefSize(44,22);
        GridPane.setHalignment(state, HPos.CENTER);

        stateif = new ToggleButton();
        palette.add(stateif,2,0);
        stateif.setMnemonicParsing(false);
        stateif.setPrefSize(44,22);
        GridPane.setHalignment(stateif, HPos.CENTER);

        statef = new ToggleButton();
        palette.add(statef,0,1);
        statef.setMnemonicParsing(false);
        statef.setPrefSize(44,22);
        GridPane.setHalignment(statef, HPos.CENTER);

        trans = new ToggleButton();
        palette.add(trans,1,1);
        trans.setMnemonicParsing(false);
        trans.setPrefSize(44,22);
        GridPane.setHalignment(trans, HPos.CENTER);

        delete = new ToggleButton();
        palette.add(delete,2,1);
        delete.setMnemonicParsing(false);
        delete.setPrefSize(44,22);
        GridPane.setHalignment(delete, HPos.CENTER);

        VBox vBox4 = new VBox();
        vBox.getChildren().add(vBox4);
        vBox4.setSpacing(3);
        vBox4.setAlignment(Pos.CENTER);

        convertBtn = new Button();
        vBox4.getChildren().add(convertBtn);
        convertBtn.setAlignment(Pos.CENTER);
        convertBtn.setMnemonicParsing(false);
        convertBtn.setPrefSize(143,35);
        convertBtn.setText("Convert To");

        convertBtn.setOnAction((ActionEvent event) -> {
            FSM af;
            FSMUI afui;
            try {
                if( regExp.isSelected() ){
                    RE re = new RE(regExpText.getText());
                    af = re.toAF();
                }
                else { // if( automate.isSelected() )

                    af = getFSM();
                    af.compile();
                    //System.out.println("------START SHOW DFA2-------");
                    //af.log2();
                    //System.out.println("------FIN-------");
                    System.out.println("------START SHOW DFA-------");
                    af.log();
                    System.out.println("------FIN-------");
                }
                if (afne.isSelected()) {
                    System.out.println("------START AFNE-------");
                    af.log();
                    System.out.println("------FIN-------");
                    controller.addNewFSMUI(afui = FSMUI.toFSMUI(af, controller));
                } else if (afnd.isSelected()) {
                    System.out.println("------START AFND-------");
                    af.getAFND().log();
                    System.out.println("------FIN-------");
                    controller.addNewFSMUI(afui = FSMUI.toFSMUI(af.getAFND(), controller));
                } else if (afd.isSelected()) {
                    System.out.println("------START AFD-------");
                    af.getAFND().getAFD().log();
                    System.out.println("------FIN-------");
                    controller.addNewFSMUI(afui = FSMUI.toFSMUI(af.getAFND().getAFD(), controller));
                } else /*if (afdo.isSelected())*/ {
                    System.out.println("------START AFDO-------");
                    af.getAFND().getAFD().getOptimizedAFD().log();
                    System.out.println("------FIN-------");
                    controller.addNewFSMUI(afui = FSMUI.toFSMUI(af.getAFND().getAFD().getOptimizedAFD(), controller));
                }
                if(regExp.isSelected()){
                    afui.regExpText.setText(regExpText.getText());
                }
                afui.change();

            }catch (Exception e){
                e.printStackTrace();
                //System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Automate Exception");
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
            }
        });

        GridPane cgp = new GridPane();
        vBox4.getChildren().add(cgp);
        cgp.setVgap(3);
        cgp.setHgap(3);

        ToggleGroup group = new ToggleGroup();
        afne = new RadioButton();
        cgp.add(afne,0,0);
        afne.setToggleGroup(group);
        afne.setMnemonicParsing(false);
        afne.setText("AFN-E");

        afnd = new RadioButton();
        cgp.add(afnd,1,0);
        afnd.setToggleGroup(group);
        afnd.setMnemonicParsing(false);
        afnd.setText("AFND");

        afd = new RadioButton();
        cgp.add(afd,2,0);
        afd.setToggleGroup(group);
        afd.setMnemonicParsing(false);
        afd.setText("AFD");

        afdo = new RadioButton();
        cgp.add(afdo,0,1);
        afdo.setToggleGroup(group);
        afdo.setMnemonicParsing(false);
        afdo.setText("AFDO");
        afdo.setSelected(true);

        /* Etat initiale */
        final Image img = new Image(getClass().getResourceAsStream(stateIImgUrl));
        statei.setText("");  statei.setGraphic(new ImageView(img));
        statei.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("stateI") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                statei.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "stateI";
                state.setSelected(false);
                stateif.setSelected(false);
                statef.setSelected(false);
                trans.setSelected(false);
                delete.setSelected(false);
            }
        });
        /**/
        /* Etat  */
        final Image img1 = new Image(getClass().getResourceAsStream(stateImgUrl));
        state.setText("");  state.setGraphic(new ImageView(img1));
        state.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("state") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                state.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img1));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "state";
                statei.setSelected(false);
                stateif.setSelected(false);
                statef.setSelected(false);
                trans.setSelected(false);
                delete.setSelected(false);
            }
        });
        /**/
        /* Etat initiale final */
        final Image img2 = new Image(getClass().getResourceAsStream(stateIFImgUrl));
        stateif.setText("");  stateif.setGraphic(new ImageView(img2));
        stateif.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("stateIF") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                stateif.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img2));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "stateIF";
                statei.setSelected(false);
                state.setSelected(false);
                statef.setSelected(false);
                trans.setSelected(false);
                delete.setSelected(false);
            }
        });
        /**/
        /* Etat final */
        final Image img3 = new Image(getClass().getResourceAsStream(stateFImgUrl));
        statef.setText("");  statef.setGraphic(new ImageView(img3));
        statef.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("stateF") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                statef.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img3));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "stateF";
                statei.setSelected(false);
                state.setSelected(false);
                stateif.setSelected(false);
                trans.setSelected(false);
                delete.setSelected(false);
            }
        });
        /**/
        /* Etat final */
        final Image img4 = new Image(getClass().getResourceAsStream(transImgUrl));
        trans.setText("");  trans.setGraphic(new ImageView(img4));
        trans.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("Trans") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                trans.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img4));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "Trans";
                statei.setSelected(false);
                state.setSelected(false);
                stateif.setSelected(false);
                statef.setSelected(false);
                delete.setSelected(false);
            }
        });
        /**/
        /* Delete */
        final Image img5 = new Image(getClass().getResourceAsStream(deleteImgUrl));
        delete.setText("");  delete.setGraphic(new ImageView(img5));
        delete.setOnMouseClicked((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("Delete") ){
                cursor.getChildren().clear();
                //getScene().setCursor(Cursor.DEFAULT);
                paletteOn = "";
                delete.setSelected(false);
            }else{
                cursor.getChildren().setAll(new ImageView(img5));
                //getScene().setCursor(Cursor.NONE);
                paletteOn = "Delete";
                statei.setSelected(false);
                state.setSelected(false);
                stateif.setSelected(false);
                statef.setSelected(false);
                trans.setSelected(false);
            }
        });
        /* Initilaze palette */
        rightPane.setOnMouseMoved((MouseEvent event) -> {
            cursor.getChildren().clear();
            //getScene().setCursor(Cursor.DEFAULT);
        });
        rightPane.setOnMouseExited((MouseEvent event) -> {
            if( paletteOn.equalsIgnoreCase("stateI") ){
                cursor.getChildren().setAll(new ImageView(img));
                //getScene().setCursor(Cursor.NONE);
            }else if( paletteOn.equalsIgnoreCase("state") ){
                cursor.getChildren().setAll(new ImageView(img1));
                //getScene().setCursor(Cursor.NONE);
            }
            else if( paletteOn.equalsIgnoreCase("stateIF") ){
                cursor.getChildren().setAll(new ImageView(img2));
                //getScene().setCursor(Cursor.NONE);
            }
            else if( paletteOn.equalsIgnoreCase("stateF") ){
                cursor.getChildren().setAll(new ImageView(img3));
                //getScene().setCursor(Cursor.NONE);
            }
            else if( paletteOn.equalsIgnoreCase("Trans") ){
                cursor.getChildren().setAll(new ImageView(img4));
                //getScene().setCursor(Cursor.NONE);
            }
            else if( paletteOn.equalsIgnoreCase("Delete") ){
                cursor.getChildren().setAll(new ImageView(img5));
                //getScene().setCursor(Cursor.NONE);
            }
        });

    }

    void change(){
        if(!isSaved())
            return;
        _saved = false;
        tab.setText(tab.getText()+"*");
    }

    public void saved(String fileName){
        _saved = true;
        tab.setText(fileName);
    }

    private boolean isSaved(){
        return _saved;
    }

    public void clear(){
        for(int i=0; i<mainPane.getChildren().size(); i++){
            Object obj = mainPane.getChildren().get(i);
            if(obj.getClass().getSimpleName().equals(StateUI.class.getSimpleName())){
                //StateUI stateUI = (StateUI) obj;
                mainPane.getChildren().remove(i);
                i--;
            }
            else if(obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName())){
                TransLine transLine = (TransLine) obj;
                transLine.removeFrom(this);
                i--;
            }
        }
    }

    private void updateScale(double x, double y, double pivotX, double pivotY){
        mainPane.getTransforms().clear();
        mainPane.getTransforms().add(new Scale(x, y, pivotX, pivotY));
    }

    public FSM getFSM() throws Exception{
        FSM res = new FSM();
        //set Alphabet
        res.setAlphabet( getAlphabet() );

        //set Q : ArrayList<Etat>
        setQ(res);

        //set T : ArrayList<trans>
        setTs(res);

        //add transitions and states of automate variables
        setVarTsAndQ(res);

        //prepare states
        res.prepareStates();

        //return FSM
        return res;
    }

    private ArrayList<Character> getAlphabet() throws Exception{
        ArrayList<Character> al = new ArrayList<>();
        HashSet<Character> hal = new HashSet<>();
        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                TransLine t = (TransLine) obj;
                String str = t.getText().getText().replaceAll(" ", "");
                String[] s = str.split(",");
                for( int i=0; i<s.length; i++ ){
                    if(s[i].length()==1)
                        hal.add(s[i].charAt(0));
                    else if(APP.Controller.bivs.containsKey(s[i])){
                        hal.addAll(APP.Controller.bivs.get(s[i]).getAlphabet());
                    }
                    else if(APP.Controller.dvs.containsKey(s[i])){
                        hal.addAll(APP.Controller.dvs.get(s[i]).getAlphabet());
                    }
                    else{
                        throw new Exception("Undefined variable: "+s[i]);
                    }
                }
            }
        }
        al.addAll(hal);
        return al;
    }

    private void setQ(FSM af){

        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals(StateUI.class.getSimpleName()) ){
                StateUI stateUI = (StateUI) obj;
                State state = stateUI.getState();
                //state.setAf(af);
                //System.out.println("setQ="+state);
                af.add(state);
                state.getTransitions().clear();
            }
        }
    }

    private void setTs(FSM af){
        //System.out.println("setTs");
        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                TransLine t = (TransLine) obj;
                String str = t.getText().getText().replaceAll(" ", "");
                String[] s = str.split(",");
                HashSet<String> hs = new HashSet<>();

                for( int i=0; i<s.length; i++ ){
                    hs.add(s[i]);
                }

                for( String s1 : hs ){
                    if(s1.length()==1){
                        Trans trans1 = new Trans();
                        trans1.setName(s1.charAt(0));
                        trans1.setFrom( t.getFrom().getState() );
                        trans1.setTo( t.getTo().getState() );
                        af.add(trans1);
                    }
                }
            }
        }
    }

    private void setVarTsAndQ(FSM af){
        //System.out.println("setVarTsAndQ");
        for( Object obj : mainPane.getChildren() ){
            if( obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                TransLine t = (TransLine) obj;
                String str = t.getText().getText().replaceAll(" ", "");
                String[] s = str.split(",");
                HashSet<String> hs = new HashSet<>();

                for( int i=0; i<s.length; i++ ){
                    hs.add(s[i]);
                }

                for( String s1 : hs ){
                    if(s1.length()>1){
                        FSM taf=null;
                        boolean bivsb=false, dvsb=false;
                        if(APP.Controller.bivs.containsKey(s1)){
                            taf = APP.Controller.bivs.get(s1).copy(false);
                            bivsb = true;
                        }else if(APP.Controller.dvs.containsKey(s1)){
                            taf = APP.Controller.dvs.get(s1).copy(false);
                            //System.out.println("setvartsandq taf.log2");
                            //taf.log2();
                            dvsb = true;
                        }
                        if(bivsb || dvsb){
                            Trans t1=new Trans(), t2=new Trans();
                            t1.setEpsilon(true); t2.setEpsilon(true);
                            t1.setFrom(t.getFrom().getState());
                            t1.setTo(taf.Initial());
                            af.add(t1);
                            //taf.intiale().getTransitions().clear();
                            taf.Initial().setInitial(false);
                            State fs = new State();
                            fs.setName("e"+ecount); ecount++;
                            //fs.setFinal(true);
                            af.add(fs);
                            //System.out.println("setQ2="+fs);
                            ArrayList<State> fss = taf.Finaux();
                            for(State st : fss){
                                Trans tt=new Trans();
                                tt.setEpsilon(true);
                                tt.setFrom(st);
                                st.setFinal(false);
                                tt.setTo(fs);
                                af.add(tt);
                            }
                            t2.setFrom(fs);
                            t2.setTo(t.getTo().getState());
                            af.add(t2);
                            af.addT(taf.getTs());
                            //System.out.println("setQ2="+taf.getQ());
                            af.addE(taf.getQ());
                        }
                    }
                }
                //af.log2();
            }
        }
    }

    ArrayList<TransLine> getTransRelatedTo(StateUI pc){ // end
        ArrayList<TransLine> alp = new ArrayList<>();
        for( Object ob : mainPane.getChildren() ){
            if( ob.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                TransLine line = (TransLine)ob;
                if( line.getTo()!=null && line.getTo().equals(pc) ){
                    alp.add(line);
                }
            }
        }
        return alp;
    }

    ArrayList<TransLine> getTransRelatedTo2(StateUI pc){ // start
        ArrayList<TransLine> alp = new ArrayList<>();
        for( Object ob : mainPane.getChildren() ){
            if( ob.getClass().getSimpleName().equals(TransLine.class.getSimpleName()) ){
                TransLine line = (TransLine)ob;
                if(  line.getFrom() !=null && line.getFrom().equals(pc) ){
                    alp.add(line);
                }
            }
        }
        return alp;
    }

    public static FSMUI toFSMUI(FSM af, Controller controller, String afuiName){
        return _toFSMUI(new FSMUI(controller, afuiName), af);
    }

    public static FSMUI toFSMUI(FSM af, Controller controller){
        return _toFSMUI(new FSMUI(controller), af);
    }

    private static FSMUI _toFSMUI(FSMUI afui, FSM af){
        System.out.println("toAFUI");

        // create UI from core
        // create states
        ArrayList<StateUI> alei = new ArrayList<>();
        for( State state : af.getQ() ){
            //System.out.println(state.isInitial() + " " + state.isFinal() + " " + state.getTransitions());
            StateUI stateUI2;
            if( state.isInitial() ){
                if( !state.isFinal() ){
                    stateUI2 = new StateUI(afui, "StateI");
                }else{
                    stateUI2 = new StateUI(afui, "StateIF");
                }
            }else{
                if( !state.isFinal() ){
                    stateUI2 = new StateUI(afui, "State");
                }else{
                    stateUI2 = new StateUI(afui, "StateF");
                }
            }
            final StateUI stateUI = stateUI2;
            //System.out.println("stateUI: "+stateUI);

            afui.mainPane.getChildren().add(stateUI);
            alei.add(stateUI);
            stateUI.getText().setText(state.getName());
            stateUI.setState(state);
            state.setStateUI(stateUI);

            stateUI.makeDynamic(afui);
        }

        ArrayList<TransLine> altl = new ArrayList<>();
        for( State state : af.getQ() ){
            for (State state1 : af.getQ()){
                TransLine t = new TransLine(afui);
                t.getText().setText("");
                ArrayList<Trans> alt = Trans.getTransitions(state, state1);
                if( alt.size()>0 ){
                    int k = 0;
                    for( Trans trans : alt ){
                        String str = trans.isEpsilon() ? Trans.Eps:trans.getName()+"";
                        if( k==0 ){
                            t.getText().setText( str );
                        }else{
                            if( !Arrays.asList(t.getText().getText().split(",")).contains(str) ){
                                t.getText().setText( t.getText().getText()+","+str);
                            }
                        }
                        k++;
                    }
                    t.setFrom(state.getStateUI());
                    t.setTo(state1.getStateUI());
                    altl.add(t);
                    //System.out.println("T: "+t);
                    t.addTo(afui);
                }
            }
        }
        //System.out.println("ALTL: "+altl);
        //System.out.println("ALEI: "+alei);

        // prepare etatui
        for( StateUI stateUI : alei ){
            for( TransLine t : altl ){
                if( t.getFrom().equals(stateUI) ){
                    stateUI.getTransLines().add(t);
                }
            }
            //System.out.println(stateUI+", "+stateUI.getTransLines());
        }

        // position the states
        // create transitions
        // combine transitions
        afui._Draw(200, 0.01, 0.85, true, alei, altl);

        return afui;
    }

    private void Draw(double R, double A, double damping){

        ArrayList<StateUI> alsui = new ArrayList<>();
        ArrayList<TransLine> altui = new ArrayList<>();
        for(Object obj : mainPane.getChildren()){
            if(obj.getClass().getSimpleName().equals(StateUI.class.getSimpleName()))
                alsui.add((StateUI)obj);
            else if(obj.getClass().getSimpleName().equals(TransLine.class.getSimpleName()))
                altui.add((TransLine)obj);
        }

        _Draw(R, A, damping,false, alsui, altui);
    }

    private void _Draw(double R, double A, double damping, boolean isFirstDrawing
            , ArrayList<StateUI> alei, ArrayList<TransLine> altl){ // FDP Barycentic Method

        System.out.println("FDP Barycentic Method");
        //System.out.println("trans size: "+altl.size());

        // initialize positioning
        int sss = (int) (Math.sqrt(alei.size())) +1, ix=0, iy=0, dx=100, dy=100;
        for( StateUI stateUI : alei ){
            stateUI.setCenterX(ix*dx);
            stateUI.setCenterY(iy*dy);
            ix++;
            if(ix>=sss){
                ix=0;
                iy++;
            }
        }

        double vsum=10;
        while( vsum>1 ){
            for(int i=0;i<alei.size();i++){
                StateUI v =alei.get(i);
                StateUI u;
                v.net_force_x=v.net_force_y=0;
                for(int j=0;j<alei.size();j++){
                    if(i==j) continue;
                    u=alei.get(j);
                    double rsq=(v.getLayoutX()-u.getLayoutX())*(v.getLayoutX()-u.getLayoutX())
                              +(v.getLayoutY()-u.getLayoutY())*(v.getLayoutY()-u.getLayoutY());
                    // repulsion
                    //  System.out.println("repulsion: "+(R*(v.getLayoutX()-u.getLayoutX())/rsq)+","
                          //          + ""+(R*(v.getLayoutY()-u.getLayoutY())/rsq));
                    v.net_force_x+=R*(v.getLayoutX()-u.getLayoutX())/rsq;
                    v.net_force_y+=R*(v.getLayoutY()-u.getLayoutY())/rsq;
                }

                for(int j=0;j<altl.size();j++){
                    TransLine l=altl.get(j);

                    if( !l.getFrom().equals(v) && !l.getTo().equals(v) )
                        continue;
                    if( l.getFrom().equals(v) )
                        u = l.getTo();
                    else
                        u = l.getFrom();
                    // counting the attraction
                    v.net_force_x += A*(u.getLayoutX() - v.getLayoutX());
                    v.net_force_y += A*(u.getLayoutY() - v.getLayoutY());
                }
		        // counting the velocity (with damping 0.85)
                v.velocity_x = (v.velocity_x + v.net_force_x)*damping;
                v.velocity_y = (v.velocity_y + v.net_force_y)*damping;
            }

            vsum=0;
            // set new positions
            for(int i=0;i<alei.size();i++){
                StateUI v = alei.get(i);
                v.setLayoutX(v.getLayoutX()+v.velocity_x);
                v.setLayoutY(v.getLayoutY()+v.velocity_y);
                vsum+=Math.abs(v.velocity_x)+Math.abs(v.velocity_y);
                // System.out.println("velocity: ("+v.velocity_x+", "+v.velocity_y+")");
            }
            // System.out.println("vsum: "+vsum);
        }
        // 2nd part
        vsum = 10;
        while( vsum>1 ){
            for(int i=0;i<alei.size();i++){
                StateUI v =alei.get(i);
                StateUI u;
                v.net_force_x=v.net_force_y=0;
                for(int j=0;j<alei.size();j++){
                    if(i==j) continue;
                    u=alei.get(j);
                    double rsq=(v.getLayoutX()-u.getLayoutX())*(v.getLayoutX()-u.getLayoutX())
                              +(v.getLayoutY()-u.getLayoutY())*(v.getLayoutY()-u.getLayoutY());
                    // repulsion
                    v.net_force_x+=R*(v.getLayoutX()-u.getLayoutX())/rsq;
                    v.net_force_y+=R*(v.getLayoutY()-u.getLayoutY())/rsq;
                    // countin the attraction
                    v.net_force_x += A*(u.getLayoutX() - v.getLayoutX());
                    v.net_force_y += A*(u.getLayoutY() - v.getLayoutY());
                }
		        // counting the velocity (with damping 0.85)
                v.velocity_x = (v.velocity_x + v.net_force_x)*damping;
                v.velocity_y = (v.velocity_y + v.net_force_y)*damping;
            }
            vsum=0;
            // set new positions
            for(int i=0;i<alei.size();i++){
                StateUI v = alei.get(i);
                v.setLayoutX(v.getLayoutX()+v.velocity_x);
                v.setLayoutY(v.getLayoutY()+v.velocity_y);
                vsum+=Math.abs(v.velocity_x)+Math.abs(v.velocity_y);
                //System.out.println("velocity: ("+v.velocity_x+", "+v.velocity_y+")");
            }
            //System.out.println("vsum: "+vsum);
        }

        // FDP BM END
        // center
        double minx = Double.MAX_VALUE,miny = Double.MAX_VALUE,
               maxx = -Double.MAX_VALUE,maxy = -Double.MAX_VALUE,
               px=mainPane.getLayoutX(),py=mainPane.getLayoutY(),
                hei=isFirstDrawing ? initHeight : mainPane.getHeight(),
                wid=isFirstDrawing ? initWidth : mainPane.getWidth();
        int it = 0;
        for(StateUI e : alei){
            if(it==0){
                minx=maxx=e.getCenterX();
                miny=maxy=e.getCenterY();
                it++;
                continue;
            }
            if( minx>e.getCenterX() ){
                minx = e.getCenterX();
            }else if( maxx<e.getCenterX() ){
                maxx=e.getCenterX();
            }
            if( miny>e.getCenterY() ){
                miny = e.getCenterY();
            }else if( maxy<e.getCenterY() ){
                maxy = e.getCenterY();
            }
            //System.out.println("e: "+e.getCenterX()+", "+e.getCenterY());
            it++;
        }
        //System.out.println("minx: "+minx);
        //System.out.println("maxx: "+maxx);
        //System.out.println("miny: "+miny);
        //System.out.println("maxy: "+maxy);
        //System.out.println("mp_x: "+px);
        //System.out.println("mp_y: "+py);
        //System.out.println("mp_wid: "+wid);
        //System.out.println("mp_hei: "+hei);
        //System.out.println("spane0 bounds: "+spane.getChildren().get(0).getLayoutBounds());

        double delta_x, delta_y;
        delta_x = (px+wid)/2-(minx+maxx)/2; delta_y = (py+hei)/2-(miny+maxy)/2;
        for(StateUI e : alei){
            e.setCenterX( e.getCenterX()+delta_x );
            e.setCenterY( e.getCenterY()+delta_y );
        }

        // position the transitions
        for( TransLine t : altl ){
            if( t.getTo().equals(t.getFrom()) ){
                double sx = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX()-t.getTo().getCircle().getRadius()/2;
                double sy = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY()-t.getTo().getCircle().getRadius()/2;
                double ex = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX()+t.getTo().getCircle().getRadius()/2;
                double ey = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY()-t.getTo().getCircle().getRadius()/2;
                Point2D s = new Point2D(sx,sy);
                Point2D e = new Point2D(ex,ey);
                t.startX(s.getX());
                t.startY(s.getY());
                t.endX(e.getX());
                t.endY(e.getY());
                t.fixControl();
            }else{
                double sox = t.getFrom().getLayoutX()+t.getFrom().getCircle().getLayoutX();
                double soy = t.getFrom().getLayoutY()+t.getFrom().getCircle().getLayoutY();
                double eox = t.getTo().getLayoutX()+t.getTo().getCircle().getLayoutX();
                double eoy = t.getTo().getLayoutY()+t.getTo().getCircle().getLayoutY();
                Point2D so = new Point2D(sox,soy);
                Point2D eo = new Point2D(eox,eoy);
                Point2D s = getMSecond(so,eo,t.getFrom().getCircle().getRadius());
                Point2D e = getMSecond(eo,so,t.getTo().getCircle().getRadius());
                //System.out.print("sox="+sox+", "+"soy="+soy+", eox="+eox+", eoy="+eoy+", so="+so+", eo="+eo);
                //System.out.println(", s="+s+", e="+e);
                t.startX(s.getX());
                t.startY(s.getY());
                t.endX(e.getX());
                t.endY(e.getY());
                t.fixControl();
            }
            t.fixPolygon();
            t.fixText();
            //System.out.println("Tp: "+t);
        }
    }

}
