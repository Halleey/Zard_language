package memory_manager.ownership.escapes;

import java.util.HashMap;
import java.util.Map;
public class EscapeInfo {

    private final Map<String, EscapeLevel> escapes =
            new HashMap<>();

    public void declare(String var) {

        escapes.putIfAbsent(
                var,
                EscapeLevel.NONE
        );
    }

    public void markEscape(String var, EscapeLevel level) {

        EscapeLevel current = escapes.getOrDefault(var, EscapeLevel.NONE);

        if (level.ordinal() > current.ordinal()) {
            escapes.put(var, level);
        }
    }

    public EscapeLevel getLevel(String var) {

        return escapes.getOrDefault(var, EscapeLevel.NONE);
    }

    public boolean escapes(String var) {
        return getLevel(var) != EscapeLevel.NONE;
    }

    public boolean escapesLoop(String var) {

        return getLevel(var).ordinal() >= EscapeLevel.LOOP.ordinal();
    }

    public boolean escapesFunction(String var) {

        return getLevel(var).ordinal() >= EscapeLevel.FUNCTION.ordinal();
    }

    public Map<String, EscapeLevel> getMap() {
        return escapes;
    }

    public void dump() {
        System.out.println();
        System.out.println("==== ESCAPE ANALYSIS ====");

        if (escapes.isEmpty()) {

            System.out.println("(nenhuma variável)");

            return;
        }

        escapes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {

                    System.out.printf(
                            "  %-20s -> %s%n",
                            entry.getKey(),
                            entry.getValue()
                    );
                });
    }

    public void dumpSummary() {

        long none = escapes.values()
                        .stream()
                        .filter(e -> e == EscapeLevel.NONE)
                        .count();

        long loop = escapes.values()
                        .stream()
                        .filter(e -> e == EscapeLevel.LOOP)
                        .count();

        long function = escapes.values()
                        .stream()
                        .filter(e -> e == EscapeLevel.FUNCTION)
                        .count();

        long global = escapes.values()
                        .stream()
                        .filter(e -> e == EscapeLevel.GLOBAL)
                        .count();

        System.out.println();
        System.out.println("==== ESCAPE SUMMARY ====");

        System.out.println("NONE     : " + none);

        System.out.println("LOOP     : " + loop);

        System.out.println("FUNCTION : " + function);

        System.out.println("GLOBAL   : " + global);
    }
}