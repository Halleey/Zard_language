package memory_manager.ownership.frees;

import memory_manager.EscapeInfo;
import memory_manager.ownership.graphs.OwnershipGraph;
import memory_manager.ownership.graphs.OwnershipNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FreePlanner {

    public static final int END_OF_SCOPE = -1;

    private final OwnershipGraph ownership;
    private final Map<String, Integer> lastUse;
    private final EscapeInfo escapeInfo;

    public FreePlanner(
            OwnershipGraph ownership,
            Map<String, Integer> lastUse,
            EscapeInfo escapeInfo
    ) {
        this.ownership = ownership;
        this.lastUse = lastUse;
        this.escapeInfo = escapeInfo;
    }

    public Map<Integer, List<FreeAction>> plan() {

        Map<Integer, List<FreeAction>> result =
                new LinkedHashMap<>();

        for (OwnershipNode root : ownership.getRoots().values()) {

            String var = root.getId();

            if (!ownership.isRoot(var))
                continue;

            int freeAt = decideFreePoint(var);

            result
                    .computeIfAbsent(freeAt, k -> new ArrayList<>())
                    .add(new FreeAction(root));
        }

        return result;
    }

    private int decideFreePoint(String var) {

        if (escapeInfo != null && escapeInfo.escapes(var))
            return END_OF_SCOPE;

        Integer stmt = lastUse.get(var);
        if (stmt != null)
            return stmt;

        return END_OF_SCOPE;
    }
}
