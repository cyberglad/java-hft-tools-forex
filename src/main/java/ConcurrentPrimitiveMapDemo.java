import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentPrimitiveMapDemo {
    private final Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
    private final StampedLock lock = new StampedLock();

    // Параметры рынка
    private static final double VOLATILITY = 0.0002; // 0.02% волатильности

    public void safePut(int key, double val) {
        long stamp = lock.writeLock();
        try {
            map.put(key, val);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public double optimisticGet(int key) {
        long stamp = lock.tryOptimisticRead();
        double val = map.get(key);

        if (!lock.validate(stamp)) {
            // Произошла коллизия с записью — откатываемся к блокировке
            stamp = lock.readLock();
            try {
                return map.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return val;
    }

    /**
     * Логика случайного блуждания цены.
     * @param currentPrice текущая цена
     * @return новая цена с учетом рыночного шума
     */
    private double generateNextPrice(double currentPrice) {
        // Гауссовское распределение позволяет цене "колебаться" вокруг текущей
        double change = currentPrice * VOLATILITY * ThreadLocalRandom.current().nextGaussian();
        return currentPrice + change;
    }

    public void startSimulation() throws InterruptedException {
        int instrumentId = 1; // EUR/USD
        safePut(instrumentId, 1.0850);

        // Поток-писатель: раз в полсекунды меняет цену (Market Data Provider)
        Thread provider = new Thread(() -> {
            double price = 1.0850;
            while (!Thread.currentThread().isInterrupted()) {
                price = generateNextPrice(price);
                safePut(instrumentId, price);
                System.out.printf("[%s] Writing price: %.5f\n", Thread.currentThread().getName(), price);
                try {
                    Thread.sleep(50); // 20 обновлений в секунду
                } catch (InterruptedException e) { break; }
            }
        });

        // Поток-потребитель (Trading Engine)
        Thread consumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                double price = optimisticGet(instrumentId);
                System.out.printf("[%s] Reading price: %.5f\n", Thread.currentThread().getName(), price);

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) { break; }
            }
        });

        provider.setName("Provider");
        consumer.setName("Consumer");
        provider.start();
        consumer.start();

        Thread.sleep(5000);
        provider.interrupt();
        consumer.interrupt();
        System.out.println("Trading session closed");
    }

    public static void main(String[] args) throws InterruptedException {
        new ConcurrentPrimitiveMapDemo().startSimulation();
    }
}