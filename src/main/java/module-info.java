module GUIs.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.scripting;

    exports GUIs.DifferentialSolver;
    opens GUIs.DifferentialSolver to javafx.fxml;

    exports GUIs.SolarSystem;
    opens GUIs.SolarSystem to javafx.fxml;
    exports GUIs.DifferentialSolver.OldSolverGUI;
    opens GUIs.DifferentialSolver.OldSolverGUI to javafx.fxml;
}