module com.ta.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.web;
    requires com.fasterxml.jackson.databind;

    opens com.ta.client to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.ta.client;
}