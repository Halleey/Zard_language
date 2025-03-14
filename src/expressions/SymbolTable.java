package expressions;

import java.util.HashMap;
import java.util.Map;

//public class SymbolTable<K, V> {
//    private final Map<K, V> table = new HashMap<>();
//
//    public void define(K key, V value) {
//        table.put(key, value);
//    }
//
//    public V get(K key) {
//        return table.get(key);
//    }
//
//    public boolean exists(K key) {
//        return table.containsKey(key);
//    }
//
//    public void update(K key, V value) {
//        if (!table.containsKey(key)) {
//            throw new RuntimeException("Variável '" + key + "' não foi declarada.");
//        }
//        table.put(key, value);
//    }
//}