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
    String[] functions = {"Custom", "Lotka-Volterra", "FitzHugh-Nagumo", "SIR"};
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
                switchToEulerLV();
            } else if (function.equals("FitzHugh-Nagumo")) {
                switchToEulerFHN();
            } else if (function.equals("SIR")) {
                switchToEulerSIR();
            } else if (function.equals("Custom")) {
                switchToCustomEuler();
            }
        } else if(config.equals("Runge-Kutta-4 Solver")) {
            if (function.equals("Lotka-Volterra")) {
                switchToRK4LV();
            }else if (function.equals("FitzHugh-Nagumo")) {
                switchToRK4FHN();
            } else if (function.equals("SIR")) {
                switchToRK4SIR();
            } else if (function.equals("Custom")) {
                switchToCustomRK();
            }
        }

    }

    @FXML
    private void switchToCustomEuler() throws IOException{
        Parent scene = FXMLLoader.load(getClass().getResource("customEuler.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToCustomRK() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("customRK.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToEulerLV() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eulerLV.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToEulerFHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eulerFHN.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToEulerSIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eulerSIR.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToRK4LV() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4LV.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToRK4FHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4FHN.fxml"));
        root.getChildren().setAll(scene);
        return;
    }
    @FXML
    private void switchToRK4SIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4SIR.fxml"));
        root.getChildren().setAll(scene);
        return;
    }


}
