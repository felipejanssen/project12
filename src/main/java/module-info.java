module project12.application {
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

    exports project12.DifferentialSolver;
    opens project12.DifferentialSolver to javafx.fxml;

    exports project12.SolarSystem;
    opens project12.SolarSystem to javafx.fxml;
    exports project12.DifferentialSolver.OldSolverGUI;
    opens project12.DifferentialSolver.OldSolverGUI to javafx.fxml;
}