module com.vinylvault {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;

    opens com.vinylvault            to javafx.fxml;
    opens com.vinylvault.controller to javafx.fxml;
    opens com.vinylvault.model      to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.vinylvault.service    to javafx.fxml;
    opens com.vinylvault.util       to javafx.fxml;

    exports com.vinylvault;
}
