module lt.ffda.patchercligui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.crypto.ec;
    requires org.json;

    opens lt.ffda.patchercligui to javafx.fxml;
    exports lt.ffda.patchercligui;
    exports lt.ffda.patchercligui.controller;
    opens lt.ffda.patchercligui.controller to javafx.fxml;
}