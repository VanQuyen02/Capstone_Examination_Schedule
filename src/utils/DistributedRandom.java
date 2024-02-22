/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author Admin
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedRandom {
    private Map<Integer, Integer> disMap;
    private int totalWeight;

    public DistributedRandom() {
        disMap = new HashMap<>();
        totalWeight = 0;
    }

    public static DistributedRandom newDistributedRandomSlot(int numSlot, int numRoomPerSlot) {
        DistributedRandom rnd = new DistributedRandom();
        for (int i = 0; i < numSlot; i++) {
            rnd.add(i, numRoomPerSlot);
        }
        return rnd;
    }

    public void add(int key, int value) {
        disMap.put(key, disMap.getOrDefault(key, 0) + value);
        totalWeight += value;
    }

    public void delete(int key) {
        if (disMap.containsKey(key)) {
            totalWeight -= disMap.get(key);
            disMap.remove(key);
        }
    }

    public void printCountKey() {
        System.out.println("Current number of keys: " + disMap.size() + ", total weight: " + totalWeight);
    }

    public int getRandom() {
        if (totalWeight <= 0) {
            System.out.println("Total weight is non-positive: " + totalWeight);
            return 0;
        }
        int randomValue = new Random().nextInt(totalWeight);
        for (Map.Entry<Integer, Integer> entry : disMap.entrySet()) {
            int weight = entry.getValue();
            if (randomValue < weight) {
                return entry.getKey();
            }
            randomValue -= weight;
        }
        return 0;
    }

    public static void main(String[] args) {
        DistributedRandom rnd = new DistributedRandom();

        // Add some values
        rnd.add(0, 5);
        rnd.add(1, 3);
        rnd.add(2, 7);

        // Print count of keys and total weight
        rnd.printCountKey();

        // Get a random value
        int randomValue = rnd.getRandom();
        System.out.println("Random value: " + randomValue);

        // Delete a key
        rnd.delete(1);

        // Print count of keys and total weight after deletion
        rnd.printCountKey();
    }
}

