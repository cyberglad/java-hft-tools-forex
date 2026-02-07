import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicArrayDemo {
    public static void main(String[] args) {
        double[] values = new double[10];
        // Create a handle to access elements of double[]
        VarHandle handle = MethodHandles.arrayElementVarHandle(double[].class);

        int index = 0;
        double delta = 1.0;

        // Cycle of CAS (Optimistic Retry Loop)
        double oldVal, newVal;
        do {
            oldVal = (double) handle.getVolatile(values, index);
            newVal = oldVal + delta;
            // Replace only if avalue is still oldVal
        } while (!handle.compareAndSet(values, index, oldVal, newVal));

        System.out.println("Atomic increment result: " + values[index]);
    }
}