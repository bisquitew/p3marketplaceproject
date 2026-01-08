package model.interfaces;

public interface Persistable {
    void saveToFile(String filePath);
    void loadFromFile(String filePath);
}
