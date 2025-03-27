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
    AnchorPane root;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ChoiceBox<String> choiceBox2;
    @FXML
    private Button confirmButton;

    String[] configs = {"Eurler Solver", "Runge-Kutta-4 Solver"};
    String[] functions = {"Lotka-Volterra", "FitzHugh-Nagumo", "SIR"};
    @FXML
    public void initialize() {
        choiceBox.getItems().addAll(configs);
        choiceBox2.getItems().addAll(functions);
    }

    @FXML
    public void confirmButtonClicked(ActionEvent event) throws IOException {
        String config = choiceBox.getValue();
        String function = choiceBox2.getValue();
        if (config.equals("Eurler Solver")) {
            if (function.equals("Lotka-Volterra")) {
                switchToEurlerLV();
            } else if (function.equals("FitzHugh-Nagumo")) {
                switchToEurlerFHN();
            } else if (function.equals("SIR")) {
                switchToEurlerSIR();
            }
        } else if(config.equals("Runge-Kutta-4 Solver")) {
            switchToRK();
            return;
        }

    }
    @FXML
    private void switchToEurlerLV() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerLV.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToEurlerFHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerFHN.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToEurlerSIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerSIR.fxml"));
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
