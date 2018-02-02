package TestUI;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class TestUI {

    private final SimpleStringProperty test;
    private final SimpleBooleanProperty result;

    public TestUI(String test, boolean result) {
        this.test = new SimpleStringProperty(test);
        this.result = new SimpleBooleanProperty(result);
    }

    public String getTest() {
        return test.get();
    }

    public void setTest(String test) {
        this.test.set(test);
    }

    public boolean isResult() {
        return result.get();
    }

    public void setResult(boolean result) {
        this.result.set(result);
    }
}
