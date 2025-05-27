package GUIs.DifferentialSolver;

import java.util.Arrays;
import java.util.Vector;
import java.util.function.BiFunction;

import Backend.ODE.ODEParser;
import Backend.ODE.ODEsolver;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main GUI for the ODE Solver application.
 * Provides a user interface for entering and solving differential equations.
 */
public class ODESolverGUI extends Application {

    // Main components
    private TextArea equationInput;
    private TextField stepSizeField;
    private TextField initialTimeField;
    private TextField finalTimeField;
    private VBox initialConditionsBox;
    private Button solveButton;
    private Button clearButton;
    private ComboBox<String> solverMethodComboBox;
    private TextArea resultsArea;

    // Styling constants
    private final String DARK_BACKGROUND = "#1E1E1E";
    private final String MEDIUM_BACKGROUND = "#252526";
    private final String LIGHT_BACKGROUND = "#333333";
    private final String ACCENT_COLOR = "#007ACC";
    private final String TEXT_COLOR = "#FFFFFF";
    private final String FIELD_COLOR = "#FFFFFF";
    private final String TEXT_AREA_STYLE = "-fx-background-color: #000000" +
            "; -fx-text-fill: " + "#000000" +
            "; -fx-border-color: " + ACCENT_COLOR +
            "; -fx-border-radius: 3px;";
    private final String HEADER_STYLE = "-fx-text-fill: " + TEXT_COLOR + "; -fx-font-weight: bold; -fx-font-size: 16px;";
    private final String LABEL_STYLE = "-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 14px;";
    private final String FIELD_STYLE = "-fx-background-color: " + DARK_BACKGROUND +
            "; -fx-text-fill: " + FIELD_COLOR +
            "; -fx-border-color: " + ACCENT_COLOR +
            "; -fx-border-radius: 3px;";
    private final String BUTTON_STYLE = "-fx-background-color: " + ACCENT_COLOR +
            "; -fx-text-fill: white; -fx-font-weight: bold;";

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + DARK_BACKGROUND + ";");

        // Create the main equation input area
        VBox equationBox = createEquationInputArea();

        // Create the sidebar for parameters
        VBox sidebar = createParameterSidebar();

        // Create the results area
        VBox resultsBox = createResultsArea();

        // Add components to the root layout
        root.setCenter(equationBox);
        root.setRight(sidebar);
        root.setBottom(resultsBox);

        // Set up event handlers
        setupEventHandlers();

        // Create the scene
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("ODE Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates the equation input area where users can enter their ODE system.
     */
    private VBox createEquationInputArea() {
        Label equationLabel = new Label("Enter ODE System (one equation per line):");
        equationLabel.setStyle(HEADER_STYLE);

        equationInput = new TextArea();
        equationInput.setStyle(TEXT_AREA_STYLE);
        equationInput.setPrefRowCount(15);
        equationInput.setPrefColumnCount(50);
        equationInput.setWrapText(true);
        equationInput.setPromptText("Example:\ndx/dt = y\ndy/dt = -0.5*y - x");
        equationInput.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));

        Label syntaxHelpLabel = new Label("Syntax: Use x0, x1, x2 for variables (x0 is the first variable). Functions: sin(), cos(), exp(), etc.");
        syntaxHelpLabel.setStyle(LABEL_STYLE);

        VBox equationBox = new VBox(10, equationLabel, equationInput, syntaxHelpLabel);
        equationBox.setPadding(new Insets(20));
        equationBox.setAlignment(Pos.TOP_LEFT);

        return equationBox;
    }

    /**
     * Creates the parameter sidebar for solver settings and initial conditions.
     */
    private VBox createParameterSidebar() {
        // Create a title for the sidebar
        Label parametersLabel = new Label("Solver Parameters");
        parametersLabel.setStyle(HEADER_STYLE);

        // Solver method selection
        Label methodLabel = new Label("Solver Method:");
        methodLabel.setStyle(LABEL_STYLE);

        solverMethodComboBox = new ComboBox<>();
        solverMethodComboBox.getItems().addAll("Euler", "Runge-Kutta 4");
        solverMethodComboBox.setValue("Runge-Kutta 4");
        solverMethodComboBox.setStyle(FIELD_STYLE);
        solverMethodComboBox.setPrefWidth(200);

        // Step size
        Label stepSizeLabel = new Label("Step Size (h):");
        stepSizeLabel.setStyle(LABEL_STYLE);

        stepSizeField = new TextField("0.01");
        stepSizeField.setStyle(FIELD_STYLE);

        // Time range
        Label timeRangeLabel = new Label("Time Range:");
        timeRangeLabel.setStyle(LABEL_STYLE);

        HBox timeRangeBox = new HBox(10);
        initialTimeField = new TextField("0.0");
        initialTimeField.setStyle(FIELD_STYLE);
        initialTimeField.setPrefWidth(80);

        finalTimeField = new TextField("10.0");
        finalTimeField.setStyle(FIELD_STYLE);
        finalTimeField.setPrefWidth(80);

        Label fromLabel = new Label("From:");
        fromLabel.setStyle(LABEL_STYLE);

        Label toLabel = new Label("To:");
        toLabel.setStyle(LABEL_STYLE);

        timeRangeBox.getChildren().addAll(
                fromLabel, initialTimeField,
                toLabel, finalTimeField
        );
        timeRangeBox.setAlignment(Pos.CENTER_LEFT);

        // Initial conditions
        Label initialConditionsLabel = new Label("Initial Conditions:");
        initialConditionsLabel.setStyle(LABEL_STYLE);

        initialConditionsBox = new VBox(5);
        addInitialConditionField("x0 = ", "1.0");
        // Removed the second initial condition to start with just one variable

        Button addConditionButton = new Button("+ Add Variable");
        addConditionButton.setStyle(BUTTON_STYLE);
        addConditionButton.setOnAction(e -> {
            int nextVar = initialConditionsBox.getChildren().size();
            addInitialConditionField("x" + nextVar + " = ", "0.0");
        });

        // Action buttons
        solveButton = new Button("Solve ODE");
        solveButton.setStyle(BUTTON_STYLE);
        solveButton.setPrefWidth(200);
        solveButton.setPrefHeight(40);

        clearButton = new Button("Clear All");
        clearButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
        clearButton.setPrefWidth(200);

        // Assemble the sidebar
        VBox sidebar = new VBox(15,
                parametersLabel,
                methodLabel, solverMethodComboBox,
                stepSizeLabel, stepSizeField,
                timeRangeLabel, timeRangeBox,
                initialConditionsLabel, initialConditionsBox, addConditionButton,
                new Separator(),
                solveButton,
                clearButton
        );

        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + MEDIUM_BACKGROUND + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        return sidebar;
    }

    /**
     * Adds a new initial condition field to the initial conditions box.
     */
    private void addInitialConditionField(String label, String defaultValue) {
        HBox conditionBox = new HBox(5);
        Label condLabel = new Label(label);
        condLabel.setStyle(LABEL_STYLE);

        TextField condField = new TextField(defaultValue);
        condField.setStyle(FIELD_STYLE);
        condField.setPrefWidth(80);

        conditionBox.getChildren().addAll(condLabel, condField);
        initialConditionsBox.getChildren().add(conditionBox);
    }

    /**
     * Creates the results area where solver output is displayed.
     */
    private VBox createResultsArea() {
        Label resultsLabel = new Label("Results:");
        resultsLabel.setStyle(HEADER_STYLE);

        resultsArea = new TextArea();
        resultsArea.setStyle(TEXT_AREA_STYLE);
        resultsArea.setPrefRowCount(10);
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));

        VBox resultsBox = new VBox(10, resultsLabel, resultsArea);
        resultsBox.setPadding(new Insets(20));

        return resultsBox;
    }

    /**
     * Sets up event handlers for the buttons.
     */
    private void setupEventHandlers() {
        solveButton.setOnAction(e -> solveODE());

        clearButton.setOnAction(e -> {
            equationInput.clear();
            stepSizeField.setText("0.01");
            initialTimeField.setText("0.0");
            finalTimeField.setText("10.0");
            resultsArea.clear();

            // Reset initial conditions
            initialConditionsBox.getChildren().clear();
            addInitialConditionField("x0 = ", "1.0");
            addInitialConditionField("x1 = ", "0.0");
        });
    }

    /**
     * Solves the ODE system based on user input.
     */
    private void solveODE() {
        try {
            // Parse input values
            double stepSize = Double.parseDouble(stepSizeField.getText());
            double initialTime = Double.parseDouble(initialTimeField.getText());
            double finalTime = Double.parseDouble(finalTimeField.getText());

            // Get initial conditions
            int numVars = initialConditionsBox.getChildren().size();
            double[] initialValues = new double[numVars];

            for (int i = 0; i < numVars; i++) {
                HBox conditionBox = (HBox) initialConditionsBox.getChildren().get(i);
                TextField valueField = (TextField) conditionBox.getChildren().get(1);
                initialValues[i] = Double.parseDouble(valueField.getText());
            }

            // Get the ODE system from the text area
            String odeSystemText = equationInput.getText();
            Vector<String> equations = new Vector<>();
            for (String line : odeSystemText.split("\n")) {
                if (!line.trim().isEmpty()) {
                    equations.add(line.trim());
                }
            }

            // Use the ODEParser to create the function
            BiFunction<Double, double[], double[]> odeFunction = ODEParser.parseEquations(equations);

            // Create the solver
            ODEsolver solver = new ODEsolver(odeFunction);

            // Solve the ODE using the selected method
            String method = solverMethodComboBox.getValue();
            double[] result;

            // Calculate the number of steps
            int steps = (int) ((finalTime - initialTime) / stepSize);

            // Use the appropriate solver method
            if ("Euler".equals(method)) {
                result = solver.eulerSolve(steps, initialTime, initialValues, stepSize);
            } else {
                // Default to RK4
                result = solver.RK4Solve(steps, initialTime, initialValues, stepSize);
            }

            // Display the results
            StringBuilder resultText = new StringBuilder();
            resultText.append("Solving ODE system:\n").append(odeSystemText).append("\n\n");
            resultText.append("Initial conditions: ").append(Arrays.toString(initialValues)).append("\n");
            resultText.append("Method: ").append(method).append("\n");
            resultText.append("Step size: ").append(stepSize).append("\n");
            resultText.append("Time range: ").append(initialTime).append(" to ").append(finalTime).append("\n\n");

            resultText.append("Results (end state):\n");
            resultText.append(String.format("t = %.3f, state = %s\n", finalTime, formatState(result)));

            resultsArea.setText(resultText.toString());

        } catch (NumberFormatException ex) {
            resultsArea.setText("Error: Please check that all numerical inputs are valid.\n" + ex.getMessage());
        } catch (Exception ex) {
            resultsArea.setText("Error: " + ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Formats a state vector for display.
     */
    private String formatState(double[] state) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < state.length; i++) {
            sb.append(String.format("%.6f", state[i]));
            if (i < state.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Main method for running the GUI directly.
     */
    public static void main(String[] args) {
        launch(args);
    }
}