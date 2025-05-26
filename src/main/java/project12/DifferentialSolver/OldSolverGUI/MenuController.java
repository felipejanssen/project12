package project12.DifferentialSolver.OldSolverGUI;
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
        Parent scene = FXMLLoader.load(getClass().getResource("eulerLV.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToEurlerFHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eulerFHN.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToCustomEuler() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("customEuler.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToCustomRK() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("customRK.fxml"));
        root.setCenter(scene);
    }

    @FXML
    private void switchToEurlerSIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("eulerSIR.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToRK4LV() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4LV.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToRK4FHN() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4FHN.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToRK4SIR() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("rk4SIR.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToAbout() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("about.fxml"));
        root.setCenter(scene);
    }
    @FXML
    private void switchToGraphView() throws IOException {
        Parent scene = FXMLLoader.load(getClass().getResource("GraphView.fxml"));
        root.setCenter(scene);
    }


}
