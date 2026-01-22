package controller;

import app.MainFX;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Message;
import model.User;
import repository.MessageDAO;
import repository.UserDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {

    @FXML private ListView<Message> messageListView;
    @FXML private TextField inputField;
    @FXML private Label headerLabel;

    private long bookingId;
    private final MessageDAO messageDAO = new MessageDAO();
    private final UserDAO userDAO = new UserDAO();
    private final IdGenerator idGen = new IdGenerator(System.currentTimeMillis());
    private Timeline poller;

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
        headerLabel.setText("Chat for Booking #" + bookingId);
        loadMessages();
        startPolling();
    }

    @FXML
    public void initialize() {
        // Custom Cell Factory for Chat Bubbles
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean isMe = msg.getSenderUserId() == Session.getCurrentUser().getId();

                    Label textLabel = new Label(msg.getText());
                    textLabel.setWrapText(true);
                    textLabel.setMaxWidth(350);
                    textLabel.getStyleClass().add(isMe ? "chat-bubble-me" : "chat-bubble-other");

                    Label infoLabel = new Label(msg.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm")));
                    infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                    VBox bubble = new VBox(2, textLabel, infoLabel);
                    bubble.setAlignment(isMe ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);

                    HBox container = new HBox(bubble);
                    container.setAlignment(isMe ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);
                    container.setPadding(new javafx.geometry.Insets(5));

                    setGraphic(container);
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });
    }

    private void loadMessages() {
        try {
            List<Message> messages = messageDAO.findByBookingId(bookingId);
            messageListView.getItems().setAll(messages);
            messageListView.scrollTo(messages.size() - 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        Message msg = new Message(
                idGen.nextId(),
                bookingId,
                Session.getCurrentUser().getId(),
                text,
                LocalDateTime.now()
        );

        try {
            messageDAO.insert(msg);
            inputField.clear();
            loadMessages();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to send message.").show();
        }
    }

    private void startPolling() {
        // Auto-refresh every 3 seconds
        poller = new Timeline(new KeyFrame(Duration.seconds(3), e -> loadMessages()));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    @FXML
    private void onBack() {
        if (poller != null) poller.stop();
        // Return to the correct dashboard based on role
        if (Session.getCurrentUser().getRole() == model.enums.UserRole.HANDYMAN) {
            MainFX.getSceneManager().showHandymanDashboard();
        } else {
            MainFX.getSceneManager().showBookingsScene();
        }
    }
}