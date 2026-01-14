package app;

import repository.BookingDAO;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Starting Handyman Marketplace in FULL MYSQL mode.");

        BookingReminderThread reminderThread = new BookingReminderThread(new BookingDAO());
        reminderThread.start();

        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner);

        menu.start();
    }
}
