package app;

import model.Booking;
import repository.BookingDAO;

import java.time.LocalDateTime;
import java.util.List;

public class BookingReminderThread extends Thread {

    private final BookingDAO bookingDAO;
    private boolean running = true;

    public BookingReminderThread(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
        setDaemon(true); // thread stops when app stops
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<Booking> all = bookingDAO.findAll();

                LocalDateTime now = LocalDateTime.now();

                for (Booking b : all) {
                    if (b.getScheduledDateTime().isAfter(now)
                            && b.getScheduledDateTime().isBefore(now.plusMinutes(30))) {
                        System.out.println("[THREAD] Reminder: Booking " + b.getId()
                                + " starts at " + b.getScheduledDateTime());
                    }
                }

                Thread.sleep(10_000); // check every 10 seconds

            } catch (Exception e) {
                System.out.println("[THREAD] Error: " + e.getMessage());
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}
