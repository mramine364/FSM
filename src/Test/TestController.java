package Test;

import Automate.FSM;
import TestUI.TestUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import File.Conf;
import File.Open;
import File.Save;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class TestController implements Initializable {

    @FXML
    private TextField new_test_txt;

    @FXML
    private TextField fsm_path_txt;

    @FXML
    private TableView<TestUI> test_unit;

    @FXML
    private TextField tests_path_txt;

    private FSM fsm;
    private List<String> tests;
    private Stage stage;
    private ObservableList<TestUI> testsui;

    public void init(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TableColumn testCol = new TableColumn("Test");
        testCol.setMinWidth(300);
        testCol.setCellValueFactory(new PropertyValueFactory<TestUI, String>("test"));
        testCol.setCellFactory(TextFieldTableCell.forTableColumn());
        testCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<TestUI, String>>) t ->{
            TestUI testUI = t.getTableView().getItems().get(t.getTablePosition().getRow());
            testUI.setTest(t.getNewValue());
            if(fsm!=null)
                testUI.setResult(fsm.isAccepted(t.getNewValue()));
            t.getTableView().refresh();
            //System.out.println(t.getNewValue());
            //System.out.println(fsm.isAccepted(t.getNewValue()));
        });

        TableColumn resultCol = new TableColumn("Result");
        resultCol.setCellValueFactory(new PropertyValueFactory<TestUI, Boolean>("result"));

        test_unit.setEditable(true);
        test_unit.getColumns().addAll(testCol, resultCol);
        test_unit.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        testsui = FXCollections.observableArrayList();
        test_unit.setItems(testsui);
    }

    @FXML
    void BrowseFSM(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse FSM");
        File fpath = new File(Conf.path);
        fileChooser.setInitialDirectory(fpath);
        // System.getProperty("user.home")
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(Conf.test_description, Conf.test_extentions)
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file!=null){
            fsm_path_txt.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void BrowseTests(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse Test File");
        File fpath = new File(Conf.path);
        fileChooser.setInitialDirectory(fpath);
        // System.getProperty("user.home")
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file!=null){
            tests_path_txt.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void AddTest(ActionEvent event) {
        String ntest = new_test_txt.getText();
        testsui.add(new TestUI(ntest, fsm.isAccepted(ntest)));
    }

    @FXML
    void DeleteTest(ActionEvent event) {
        ObservableList<TestUI> oltui = test_unit.getSelectionModel().getSelectedItems();
        testsui.removeAll(oltui);
    }

    @FXML
    void LoadFiles(ActionEvent event) {
        File file = new File(fsm_path_txt.getText());
        //System.out.println(file.exists());
        //System.out.println(file.getAbsolutePath());
        if(file.exists()) {
            fsm = Open.open_fsm(file);
            Notifications.create().position(Pos.TOP_LEFT)
                    .title("Success")
                    .text("FSM file has been successfully loaded.")
                    .hideAfter(Duration.millis(2000))
                    .owner(stage)
                    .graphic(new ImageView(new Image(getClass().getResourceAsStream("../IMG/success_icon.png"))))
                    .show();
        }
        else{
            Notifications.create().position(Pos.BOTTOM_LEFT)
                    .title("Error")
                    .text("FSM file doesn't exist.")
                    .hideAfter(Duration.millis(2000))
                    .owner(stage)
                    .showError();
        }

        File file2 = new File(tests_path_txt.getText());
        if(file2.exists()) {
            tests = Open.open_tests(file2);
            Notifications.create().position(Pos.TOP_LEFT)
                    .title("Success")
                    .text("Tests file has been successfully loaded.")
                    .hideAfter(Duration.millis(2000))
                    .owner(stage)
                    .graphic(new ImageView(new Image(getClass().getResourceAsStream("../IMG/success_icon.png"))))
                    .show();
        }
        else{
            Notifications.create().position(Pos.BOTTOM_LEFT)
                    .title("Error")
                    .text("Tests file doesn't exist.")
                    .hideAfter(Duration.millis(2000))
                    .owner(stage)
                    .showError();
        }
    }

    @FXML
    void CheckTests(ActionEvent event) {
        if (tests!=null && fsm!=null){
            for(String test : tests){
                testsui.add(new TestUI(test, fsm.isAccepted(test)));
            }
            tests.clear();
        }
    }

    @FXML
    void SaveTests(ActionEvent event) {
        List<String> ts = new ArrayList<>();
        for(TestUI testUI : testsui){
            ts.add(testUI.getTest());
        }
        Save.save_tests(ts, tests_path_txt.getText());
    }

}
