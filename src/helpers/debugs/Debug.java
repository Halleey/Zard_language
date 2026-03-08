package helpers.debugs;

public class Debug {
    public static boolean ENABLE = false;

    public static void log(String msg) {
        if(ENABLE){
            System.out.println("[DEBUG] " + msg);
        }
    }
}
