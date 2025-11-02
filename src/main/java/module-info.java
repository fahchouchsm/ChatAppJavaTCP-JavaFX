module com.fahchouch {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.fahchouch.client to javafx.graphics, javafx.fxml;
    opens com.fahchouch.client.login to javafx.graphics, javafx.fxml;

    exports com.fahchouch.client;
    exports com.fahchouch.client.login;
}
