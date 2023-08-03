module lt.ffda.revancedcligui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.crypto.ec;
    requires org.json;

    opens lt.ffda.revancedcligui to javafx.fxml;
    exports lt.ffda.revancedcligui;
    exports lt.ffda.revancedcligui.controller;
    opens lt.ffda.revancedcligui.controller to javafx.fxml;
}