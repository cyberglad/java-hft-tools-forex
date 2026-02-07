# Java HFT Tools: Forex Market Simulation

A demonstration project showcasing high-performance tools and techniques for building Low-Latency and High-Frequency Trading (HFT) systems using Java 21. This project covers memory optimization, low-level concurrency primitives, and database locking strategies.

## 🎯 Key Concepts

This project implements and visualizes the following technical pillars:
1.  **Memory Management**: Comparison between standard JDK collections and `fastutil` primitive maps to minimize GC pressure.
2.  **Lock-Free & Optimistic Locking**: Implementation of `StampedLock`, `VarHandle`, and CAS (Compare-And-Swap) loops.
3.  **Market Simulation**: Modeling currency pair price action using a Stochastic Process (Random Walk).
4.  **Database Integrity**: Demonstrating Optimistic vs. Pessimistic locking strategies using Hibernate/JPA.

---

## 🏗 Project Structure

### 1. Fastutil vs. JDK HashMap (`FastMapVsJdkDemo.java`)
Demonstrates why **Open Addressing** outperforms **Separate Chaining** in HFT.
* **JDK HashMap**: Creates millions of wrapper objects (`Integer`, `Double`), leading to "Pointer Chasing" and high memory overhead.
* **Fastutil Int2DoubleOpenHashMap**: Stores data in dense primitive arrays, ensuring **CPU Cache Friendliness**.
* *Result:* ~30% reduction in memory footprint and 2-3x faster insertion speeds.



### 2. Concurrent Market Simulation (`ConcurrentPrimitiveMapDemo.java`)
A thread-safe price feed implementation.
* **Provider**: Simulates a Liquidity Provider (Market Maker) generating EUR/USD prices via a Gaussian Random Walk in the `generateNextPrice` method.
* **Consumer**: A Trading Bot utilizing **Optimistic Reads**.
* **StampedLock**: Allows the bot to read data without acquiring a heavy mutex, only falling back to a read-lock if a write occurred during the read operation.



### 3. Low-Level CAS (`LowLevelCasDemo.java`)
Utilizes `VarHandle` (the modern, safe alternative to `sun.misc.Unsafe`).
* Shows how to update array elements atomically at the CPU instruction level.
* Implements a **Compare-And-Swap** loop to prevent race conditions without using `synchronized`.



### 4. Hibernate Locking (`HibernateLockDemo.java`)
Demonstrates concurrency control at the Persistence Layer:
* **Optimistic**: Using the `@Version` annotation. Checks versioning during `UPDATE` statements.
* **Pessimistic**: Using `LockModeType.PESSIMISTIC_WRITE`. Generates `SELECT ... FOR UPDATE` SQL queries.

---

## 🛠 Tech Stack

* **Java 21** (Required for `VarHandle` and modern JVM features)
* **Fastutil 8.5.12** — High-performance primitive collections.
* **Spring Data JPA / Hibernate 6** — Data persistence and locking.
* **H2 Database** — In-memory database for demo execution.

---

## 🚀 How to Run

1.  **Build the project**:
    ```bash
    mvn clean install
    ```

2.  **Run the Memory/Performance Benchmark**:
    ```bash
    mvn exec:java -Dexec.mainClass="com.hft.demo.FastMapVsJdkDemo"
    ```

3.  **Run the Live Market Simulation**:
    ```bash
    mvn exec:java -Dexec.mainClass="com.hft.demo.ConcurrentPrimitiveMapDemo"
    ```

## 📈 Technical