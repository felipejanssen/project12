package project12.DifferentialSolver.OldSolverGUI;
import java.util.Arrays;

import Backend.ODE.ODEsolver;
import Backend.TestModels.FitzHughNagumo;
import Backend.TestModels.LotkaVolterra;
import Backend.TestModels.SIRmodel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;


public class EulerController {
    @FXML
    private Label label;
    @FXML
    private TextField x;
    @FXML
    private TextField y;
    @FXML
    private TextField z;
    @FXML
    private TextField param1;
    @FXML
    private TextField param2;
    @FXML
    private TextField param3;
    @FXML
    private TextField param4;
    @FXML
    private TextField stepSize;
    @FXML
    private TextField totalDuration;
    @FXML
    private TextField initialTime;
    @FXML
    private Button confirm;
    @FXML
    private TextField output;
    @FXML
    private TextField output2;
    @FXML
    private TextField output3;


    @FXML
    public void initialize() {
        output.setEditable(false);
        output2.setEditable(false);

    }
    @FXML
    public void confirmAction(ActionEvent event) {
        boolean flag = false;
        ArrayList<TextField> fields = new ArrayList<>();

        if (label.getText().equals("Lotka-Volterra")) {
            fields.addAll(Arrays.asList(x, y, param1, param2, param3, param4, stepSize, initialTime, totalDuration));
        } else if (label.getText().equals("SIR-Model")) {
            fields.addAll(Arrays.asList(x, y, z, param1, param2, param3, stepSize, initialTime, totalDuration));
        } else if (label.getText().equals("FitzHugh-Nagumo")){
            fields.addAll(Arrays.asList(x, y, param1, param2, param3, param4, stepSize, initialTime, totalDuration));
        }
        for (TextField field : fields) {
            String input = field.getText();
            if (input.isEmpty()) {
                flag = true;
                output.setText("Missing ODE solver parameter");
                return;
            }
            try {
                double value = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                flag = true;
                output.setText("Invalid type for ODE solver parameter");
                return;
            }
        }

        if (!flag){
            ODEsolver solver;
            if (label.getText().equals("Lotka-Volterra")){
                double[] parameters = new double[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    parameters[i] = Double.parseDouble(fields.get(i).getText());
                }
                LotkaVolterra function = new LotkaVolterra(parameters[2], parameters[3], parameters[4], parameters[5]);
                double[] inputVector = {parameters[0], parameters[1]};
                solver = new ODEsolver(function.getODEFunction());
                double[] result = solver.eulerSolve((int) parameters[8], parameters[7], inputVector, parameters[6]);
                output.setText(String.valueOf(result[0]));
                output2.setText(String.valueOf(result[1]));

            } else if (label.getText().equals("FitzHugh-Nagumo")){
                double[] parameters = new double[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    parameters[i] = Double.parseDouble(fields.get(i).getText());
                }
                FitzHughNagumo function = new FitzHughNagumo(parameters[2], parameters[3], parameters[4], parameters[5]);
                solver = new ODEsolver(function.getODEFunction());
                double[] inputVector = {parameters[0], parameters[1]};
                double[] result = solver.eulerSolve((int) parameters[8], parameters[7], inputVector, parameters[6]);
                output.setText(String.valueOf(result[0]));
                output2.setText(String.valueOf(result[1]));

            } else if (label.getText().equals("SIR")){
                double[] parameters = new double[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    parameters[i] = Double.parseDouble(fields.get(i).getText());
                }
                SIRmodel function = new SIRmodel(parameters[3], parameters[4], parameters[5]);
                solver = new ODEsolver(function.getODEFunction());
                double[] inputVector = {parameters[0], parameters[1], parameters[2]};
                double[] result = solver.eulerSolve((int) parameters[8], parameters[7], inputVector, parameters[6]);
                output.setText(String.valueOf(result[0]));
                output2.setText(String.valueOf(result[1]));
                output3.setText(String.valueOf(result[2]));
            }
        }
    }
}
