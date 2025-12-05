package low.module.lists;

import ast.lists.*;
import low.TempManager;
import low.lists.generics.*;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
public class ListVisitor {

    private final ListEmitter listEmitter;
    private final ListAddEmitter addEmitter;
    private final ListRemoveEmitter removeEmitter;
    private final ListGetEmitter getEmitter;
    private final ListSizeEmitter sizeEmitter;
    private final ListClearEmitter clearEmitter;
    private final ListAddAllEmitter allEmitter;

    private final LLVisitorMain root;

    public ListVisitor(LLVisitorMain root, TempManager temps, GlobalStringManager strings) {
        this.root = root;

        this.listEmitter = new ListEmitter(temps);
        this.addEmitter = new ListAddEmitter(temps, strings);
        this.removeEmitter = new ListRemoveEmitter(temps);
        this.getEmitter = new ListGetEmitter(temps);
        this.sizeEmitter = new ListSizeEmitter(temps);
        this.clearEmitter = new ListClearEmitter(temps);
        this.allEmitter = new ListAddAllEmitter(temps, strings);
    }

    public String visit(ListNode node) {
        return listEmitter.emit(node, root);
    }

    public String visit(ListAddNode node) {
        return addEmitter.emit(node, root);
    }

    public String visit(ListRemoveNode node) {
        return removeEmitter.emit(node, root);
    }

    public String visit(ListGetNode node) {
        return getEmitter.emit(node, root);
    }

    public String visit(ListSizeNode node) {
        return sizeEmitter.emit(node, root);
    }

    public String visit(ListClearNode node) {
        return clearEmitter.emit(node, root);
    }

    public String visit(ListAddAllNode node) {
        return allEmitter.emit(node, root);
    }
}

