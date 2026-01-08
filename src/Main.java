package app;

import app.Menu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Handyman Marketplace in FULL MYSQL mode.");

        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner);
        menu.start();
    }
}
