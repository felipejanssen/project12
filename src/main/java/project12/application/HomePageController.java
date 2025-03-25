package project12.application;

import com.almasb.fxgl.entity.action.Action;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {
    @FXML
    Pane root;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Button confirmButton;

    String[] configs = {"Eurler Solver", "Runge-Kutta-4 Solver"};
    @FXML
    public void initialize() {
        choiceBox.getItems().addAll(configs);
    }

    @FXML
    public void confirmButtonClicked(ActionEvent event) throws IOException {
        String config = choiceBox.getValue();
        if (config.equals("Eurler Solver")) {
            switchToEurler();
        } else if(config.equals("Runge-Kutta-4 Solver")) {
            switchToRK();
            return;
        }

    }
    @FXML
    private void switchToEurler() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurler.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToRK() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk.fxml"));
        root.getChildren().setAll(scene);
        return;
    }


}
