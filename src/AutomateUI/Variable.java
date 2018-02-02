
package AutomateUI;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author amine
 */
public class Variable {
    private final SimpleStringProperty Var;
    private final SimpleStringProperty Expr;

    public Variable(String Variable, String Value) {
        this.Var = new SimpleStringProperty(Variable);
        this.Expr = new SimpleStringProperty(Value);
    }

    public String getVar() {
        return Var.getValue();
    }

    public String getExpr() {
        switch(Expr.getValue()){
            case " ":
                return "Space";
            case ",":
                return ", (Comma)";
            case "":
                return "Epsilon transition";
            default:
                return Expr.getValue();
        }
    }

    public String get_var() {
        return Var.getValue();
    }

    public String get_expr() {
        return Expr.getValue();        
    }
        
}
