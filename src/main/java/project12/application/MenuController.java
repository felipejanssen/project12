package project12.application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import java.io.IOException;
public class MenuController {
    @FXML
    private BorderPane root;

    @FXML
    public void initialize() throws IOException {
        switchToHome();
    }

    @FXML
    private void switchToHome() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("home.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToEurlerLV() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerLV.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToEurlerFHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerFHN.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToEurlerSIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eurlerSIR.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToRK() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToAbout() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("about.fxml"));
        root.setCenter(scene);
    }

}
