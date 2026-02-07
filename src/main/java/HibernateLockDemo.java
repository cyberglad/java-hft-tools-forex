import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

// 1. Entity с Optimistic Lock
@Entity
class Inventory {
    @Id Long id;
    int stock;
    @Version Long version; // СУТЬ OPTIMISTIC LOCK
}

// 2. Repository с Pessimistic Lock
interface InventoryRepository extends CrudRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) // СУТЬ PESSIMISTIC LOCK (FOR UPDATE)
    @Query("select i from Inventory i where i.id = :id")
    Inventory findAndLockById(Long id);
}

public class HibernateLockDemo {
    public static void main(String[] args) {
        System.out.println("Pessimistic: blocks a string in a DB (SELECT FOR UPDATE)");
        System.out.println("Optimistic: checks 'version' bf update UPDATE");
    }
}