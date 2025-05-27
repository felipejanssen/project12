package GUIs.DifferentialSolver.OldSolverGUI;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GraphController {
    @FXML
    private LineChart<Number, Number> chart;

    @FXML
    private ComboBox<String> methodSelector;

    public void initialize() {
        // Populate the combo box with available methods
        ObservableList<String> methods = FXCollections.observableArrayList("Euler", "Runge-Kutta 4");
        methodSelector.setItems(methods);
        methodSelector.setValue("Euler"); // Set the default method
        loadDataFromFile("src/main/java/Data/LVTest_Euler");
    }

    @FXML
    private void onMethodSelected() {
        String method = methodSelector.getValue();
        String filePath;

        // Determine which file to load based on the selected method
        if ("Euler".equals(method)) {
            filePath = "src/main/java/Data/LVTest_Euler";
        } else {
            filePath = "src/main/java/Data/LVTest_RK4";
        }

        chart.getData().clear(); // Clear previous data
        loadDataFromFile(filePath);
    }

    private void loadDataFromFile(String filePath) {
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Var1"); // First variable

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("Var2"); // Second variable

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header

            String line; // Skip the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double time = Double.parseDouble(values[0]);
                double var1 = Double.parseDouble(values[1]);
                double var2 = Double.parseDouble(values[2]);

                // Add data points to the chart series
                series1.getData().add(new XYChart.Data<>(time, var1));
                series2.getData().add(new XYChart.Data<>(time, var2));
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print error details if file reading fails
        }

        // Add both series to the chart
        chart.getData().addAll(List.of(series1, series2));
    }
}
