package variables.exceptions;

public class ExceptionVar extends RuntimeException {

    public  ExceptionVar(String varName){
        super("Váriavel "+ varName + " já foi declarada");
    }
}
