package Backend.ODE;

import java.util.Arrays;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.Map;

public class ODEParser {

    public static BiFunction<Double, double[], double[]> parseEquations(Vector<String> equations) {
        Map<Integer, String> equationMap = new HashMap<>();

        // Extract right-hand side expressions from "dx/dt = expression"
        int index = 0;
        for (String eq : equations) {
            String[] parts = eq.split("=");
            if (parts.length == 2) {
                equationMap.put(index++, parts[1].trim());
            }
        }

        return (time, state) -> {
            double[] derivatives = new double[equationMap.size()];

            for (int i = 0; i < equationMap.size(); i++) {
                try {
                    String expression = equationMap.get(i);
                    derivatives[i] = evaluateExpression(expression, time, state);
                } catch (Exception e) {
                    System.err.println("Error evaluating equation at index " + i + ": " + e.getMessage());
                    derivatives[i] = Double.NaN;
                }
            }

            return derivatives;
        };
    }

    public static double evaluateExpression(String expression, double time, double[] state) {
        // Replace time variable
        expression = expression.replace("t", Double.toString(time));

        // Replace state variables (x0, x1, etc.)
        for (int i = 0; i < state.length; i++) {
            // Ensure we replace x0 before replacing parts of x10, etc.
            expression = expression.replaceAll("x" + i + "(?![0-9])", Double.toString(state[i]));
        }

        return evaluateMathExpression(expression);
    }

    private static double evaluateMathExpression(String expression) {
        expression = expression.replaceAll("\\s+", ""); // Remove all whitespace

        try {
            // First handle multiplication and division
            while (expression.contains("*") || expression.contains("/")) {
                int mulIndex = expression.indexOf('*');
                int divIndex = expression.indexOf('/');

                int opIndex;
                if (mulIndex == -1) opIndex = divIndex;
                else if (divIndex == -1) opIndex = mulIndex;
                else opIndex = Math.min(mulIndex, divIndex);

                char operator = expression.charAt(opIndex);

                // Find left operand
                int leftStart = opIndex - 1;
                while (leftStart >= 0 && (Character.isDigit(expression.charAt(leftStart)) || expression.charAt(leftStart) == '.')) {
                    leftStart--;
                }
                leftStart++;

                // Find right operand
                int rightEnd = opIndex + 1;
                while (rightEnd < expression.length() && (Character.isDigit(expression.charAt(rightEnd)) || expression.charAt(rightEnd) == '.')) {
                    rightEnd++;
                }

                // Extract operands
                double leftOperand = Double.parseDouble(expression.substring(leftStart, opIndex));
                double rightOperand = Double.parseDouble(expression.substring(opIndex + 1, rightEnd));

                // Perform operation
                double result;
                if (operator == '*') {
                    result = leftOperand * rightOperand;
                } else { // '/'
                    result = leftOperand / rightOperand;
                }

                // Replace the operation with the result
                expression = expression.substring(0, leftStart) + result + expression.substring(rightEnd);
            }

            // Then handle addition and subtraction
            double result = 0;
            String[] addParts = expression.split("\\+");

            for (String part : addParts) {
                if (part.contains("-")) {
                    String[] subParts = part.split("-");
                    double subResult = Double.parseDouble(subParts[0]);

                    for (int i = 1; i < subParts.length; i++) {
                        subResult -= Double.parseDouble(subParts[i]);
                    }

                    result += subResult;
                } else if (!part.isEmpty()) {
                    result += Double.parseDouble(part);
                }
            }

            return result;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing expression: " + expression);
            return Double.NaN;
        }
    }

    public static void main(String[] args) {
        // Example use
        Vector<String> equations = new Vector<>();
        equations.add("dy/dt = 0.1 * x0");

        BiFunction<Double, double[], double[]> odeFunction = parseEquations(equations);
        double[] state = {10};
        ODEsolver solver = new ODEsolver(odeFunction);
        double[] result = solver.eulerSolve(100, 0.0, state, 0.1);

        System.out.println(Arrays.toString(result));
    }
}