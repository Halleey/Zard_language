package low.lists.string;


public class ListAddStringEmitter {

    public String emit(String listTmp, String listType, String valTmp, String valType) {

        if (!listType.equals("%ArrayListString*")) {
            throw new RuntimeException(
                    "[ListAddStringEmitter] Expected %ArrayListString* but got " + listType
            );
        }

        if (!valType.equals("%String*")) {
            throw new RuntimeException(
                    "[ListAddStringEmitter] Expected %String* but got " + valType
            );
        }
        return
                "  call void @arraylist_string_add(%ArrayListString* "
                        + listTmp + ", %String* " + valTmp + ")\n"
                        + ";;VAL:" + listTmp + ";;TYPE:%ArrayListString*\n";
    }
}