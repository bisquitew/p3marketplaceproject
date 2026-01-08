package repository;

import java.io.*;
import java.util.*;

public class DataStore {

    public static <T> void saveList(List<T> list, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("Failed to save list: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            return (List<T>) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <K, V> void saveMap(Map<K, V> map, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            System.err.println("Failed to save map: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> loadMap(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            return (Map<K, V>) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load map: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
