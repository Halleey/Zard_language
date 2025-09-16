package home;

import ast.ASTNode;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;

public class MainAST extends ASTNode {

    public final List<ASTNode> body;

    public MainAST(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {

       for(ASTNode node : body){
           node.evaluate(variables);
       }
        return null;
    }
}
