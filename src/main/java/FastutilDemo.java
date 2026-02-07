import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.HashMap;

public class FastutilDemo {
    public static void main(String[] args) {
        int elements = 1_000_000;

        // 1. Standard map (consumes much memory, slow bc of boxing)
        System.gc();
        long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();
        HashMap<Integer, Double> jdkMap = new HashMap<>();
        System.out.println("JDK HashMap Memory before : " + (memBefore) / 1024 / 1024 + " MB");
        for (int i = 0; i < elements; i++) jdkMap.put(i, (double) i);
        // for each int or double, wrapper is created (Integer, Double)
        // processor wasted a lot of time for memory allocation in heap for million of objects.
        // Plus collisions in chaining take time
        long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("JDK HashMap insert time: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("JDK HashMap Memory after: " + (memAfter) / 1024 / 1024 + " MB");

        // 2. Fastutil (primitives, fast, compact)
        System.gc();
        start = System.currentTimeMillis();
        Int2DoubleOpenHashMap fastMap = new Int2DoubleOpenHashMap();
        memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Fastutil Map Memory before : " + (memBefore) / 1024 / 1024 + " MB");
        for (int i = 0; i < elements; i++) fastMap.put(i, (double) i);
        // processor simply puts int or doubles in the array of primitives, linear memory consumption w/o creating objects
        // note: processor allocated 74MB, bc it already allocates with some spare memory
        memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Fastutil Map insert time: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Fastutil Map Memory after: " + (memAfter) / 1024 / 1024 + " MB");

        // 3. Search (open adressing in action)
        start = System.nanoTime();
        fastMap.get(500_000);
        // search very fast bc data is taken straight from cache l3/l2
        System.out.println("Fastutil search time: " + (System.nanoTime() - start) + "ns");
    }
}