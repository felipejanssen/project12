package project12.application;
import java.util.Vector;
import java.util.function.BiFunction;
import Backend.ODE.ODEsolver;

import Backend.ODE.ODEParser;

import Backend.ODE.StringVectorParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class customController {
    @FXML private AnchorPane root;
    @FXML private TextField inputVector;
    @FXML private TextField output;
    @FXML private TextField stepSize;
    @FXML private TextField initial;
    @FXML private TextField totalSteps;
    @FXML private TextArea equations;
    @FXML private Label label;

    @FXML
    public void initialize(){
        output.setEditable(false);
    }

    @FXML
    public void confirmAction(ActionEvent event){
        boolean flag = false;
        TextField[] fields = {stepSize, initial, totalSteps};
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
            double[] parameters = {Double.parseDouble(stepSize.getText()), Double.parseDouble(initial.getText()), Double.parseDouble(totalSteps.getText())};

            //Parsing the input vector to store the input vector string as an array of doubles
            Vector<String> stringVector = new Vector<>();
            stringVector.add(inputVector.getText());
            double[] vector = StringVectorParser.parseVector(stringVector);

            //Parsing the textArea for the system of differential equations
            String[] lines = equations.getText().split("\n");
            Vector<String> stringEquations = new Vector<>();
            for (String line: lines){
                stringEquations.add(line);
            }
            BiFunction<Double, double[], double[]> odeFunction = ODEParser.parseEquations(stringEquations);
            ODEsolver solver = new ODEsolver(odeFunction);
            if (label.getText().equals("Euler ODE Solver")) {
                double[] result = solver.eulerSolve((int) parameters[2], parameters[1], vector, parameters[0]);
                String outputString = "";
                for (double str : result){
                    outputString = outputString + Double.toString(str) + ", ";
                }
                output.setText(outputString);
            } else if (label.getText().equals("Runge-Kutta 4 ODE Solver")) {
                double[] result = solver.RK4Solve((int) parameters[2], parameters[1], vector, parameters[0]);
                String outputString = "";
                for (double str : result){
                    outputString = outputString + Double.toString(str) + ", ";
                }
                output.setText(outputString);
            }
        }
    }
}
