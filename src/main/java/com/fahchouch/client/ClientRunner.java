package com.fahchouch.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

final class AppConst {
    public static final String APP_NAME = "Chat Application";
    public static final String APP_VERSION = "1.0";
}

public class ClientRunner extends Application {
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new FXMLLoader(getClass().getResource("/com/fahchouch/client/login.fxml")).load());
        stage.setResizable(false);
        stage.titleProperty().set(AppConst.APP_NAME + " - Login");
        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fahchouch/client/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
