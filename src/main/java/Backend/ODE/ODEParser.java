package Backend.ODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enhanced Parser for ODE equations entered as text.
 * Converts text-based differential equations into executable functions.
 */
public class ODEParser {

    // Supported mathematical functions
    private static final Map<String, Function<Double, Double>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("sin", Math::sin);
        FUNCTIONS.put("cos", Math::cos);
        FUNCTIONS.put("tan", Math::tan);
        FUNCTIONS.put("exp", Math::exp);
        FUNCTIONS.put("log", Math::log);
        FUNCTIONS.put("sqrt", Math::sqrt);
        FUNCTIONS.put("abs", Math::abs);
        FUNCTIONS.put("asin", Math::asin);
        FUNCTIONS.put("acos", Math::acos);
        FUNCTIONS.put("atan", Math::atan);
        FUNCTIONS.put("sinh", Math::sinh);
        FUNCTIONS.put("cosh", Math::cosh);
        FUNCTIONS.put("tanh", Math::tanh);
    }

    /**
     * Parses a system of ODEs from text input.
     *
     * @param equations The system of equations as text, one per line
     * @return A function that computes the derivatives for the ODE system
     */
    public static BiFunction<Double, double[], double[]> parseSystem(String equations) {
        // Split the input into individual equations
        String[] lines = equations.trim().split("\n");
        int numEquations = lines.length;

        // Create parsers for each equation
        @SuppressWarnings("unchecked")
        BiFunction<Double, double[], Double>[] parsedEquations = new BiFunction[numEquations];

        for (int i = 0; i < numEquations; i++) {
            parsedEquations[i] = parseEquation(lines[i], i);
        }

        // Return a function that computes all derivatives
        return (t, y) -> {
            double[] derivatives = new double[numEquations];
            for (int i = 0; i < numEquations; i++) {
                derivatives[i] = parsedEquations[i].apply(t, y);
            }
            return derivatives;
        };
    }

    /**
     * Parses a single ODE equation.
     *
     * @param equation The equation as text (e.g., "dx/dt = y + sin(x)")
     * @param equationIndex The index of the equation in the system (used for error reporting)
     * @return A function that computes the derivative for this equation
     */
    private static BiFunction<Double, double[], Double> parseEquation(String equation, int equationIndex) {
        String rhsExpression = extractRHS(equation);

        // Return a function that evaluates the RHS for given t and y values
        return (t, y) -> {
            try {
                return evaluateExpression(rhsExpression, t, y);
            } catch (Exception e) {
                System.err.println("Error evaluating equation " + equationIndex + ": " + equation);
                e.printStackTrace();
                return 0.0;
            }
        };
    }

    /**
     * Extracts the right-hand side of the equation.
     */
    private static String extractRHS(String equation) {
        String normalized = equation.trim().replaceAll("\\s+", " ");

        // Handle various formats of differential equations
        if (normalized.contains("=")) {
            return normalized.substring(normalized.indexOf('=') + 1).trim();
        }

        // In case the equation doesn't contain an equals sign, assume it's just the RHS
        return normalized;
    }

    /**
     * Evaluates a mathematical expression with variables t and x0, x1, x2, etc.
     *
     * @param expression The expression to evaluate
     * @param t The current time
     * @param y The current state
     * @return The evaluated result
     */
    private static double evaluateExpression(String expression, double t, double[] y) {
        // Tokenize the expression
        List<Token> tokens = tokenize(expression, t, y);

        // Convert to postfix notation (Shunting Yard algorithm)
        List<Token> postfix = toPostfix(tokens);

        // Evaluate the postfix expression
        return evaluatePostfix(postfix);
    }

    /**
     * Tokenizes an expression, replacing variable references with their values.
     */
    private static List<Token> tokenize(String expression, double t, double[] y) {
        List<Token> tokens = new ArrayList<>();

        // Pre-process the expression with spaces around operators for easier tokenization
        String processedExpr = expression.replaceAll("([+\\-*/^()\\[\\],])", " $1 ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] parts = processedExpr.split(" ");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // Check for special variable names
            if (part.equals("t")) {
                tokens.add(new Token(TokenType.NUMBER, t));
            }
            else if (part.equals("x") || part.equals("x0")) {
                if (y.length > 0) {
                    tokens.add(new Token(TokenType.NUMBER, y[0]));
                } else {
                    throw new IllegalArgumentException("Variable x referenced but no value provided");
                }
            }
            else if (part.equals("y") || part.equals("x1")) {
                if (y.length > 1) {
                    tokens.add(new Token(TokenType.NUMBER, y[1]));
                } else {
                    throw new IllegalArgumentException("Variable y referenced but no value provided");
                }
            }
            else if (part.equals("z") || part.equals("x2")) {
                if (y.length > 2) {
                    tokens.add(new Token(TokenType.NUMBER, y[2]));
                } else {
                    throw new IllegalArgumentException("Variable z referenced but no value provided");
                }
            }
            else if (part.matches("x[0-9]+")) {
                int index = Integer.parseInt(part.substring(1));
                if (index < y.length) {
                    tokens.add(new Token(TokenType.NUMBER, y[index]));
                } else {
                    throw new IllegalArgumentException("Variable " + part + " referenced but no value provided");
                }
            }
            // Check if it's a number
            else if (isNumeric(part)) {
                tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(part)));
            }
            // Check if it's a function
            else if (FUNCTIONS.containsKey(part)) {
                tokens.add(new Token(TokenType.FUNCTION, part));
            }
            // Operators and parentheses
            else if (part.equals("+")) {
                tokens.add(new Token(TokenType.OPERATOR, Operator.ADD));
            } else if (part.equals("-")) {
                tokens.add(new Token(TokenType.OPERATOR, Operator.SUBTRACT));
            } else if (part.equals("*")) {
                tokens.add(new Token(TokenType.OPERATOR, Operator.MULTIPLY));
            } else if (part.equals("/")) {
                tokens.add(new Token(TokenType.OPERATOR, Operator.DIVIDE));
            } else if (part.equals("^")) {
                tokens.add(new Token(TokenType.OPERATOR, Operator.POWER));
            } else if (part.equals("(")) {
                tokens.add(new Token(TokenType.LEFT_PAREN));
            } else if (part.equals(")")) {
                tokens.add(new Token(TokenType.RIGHT_PAREN));
            } else if (part.equals(",")) {
                tokens.add(new Token(TokenType.COMMA));
            } else {
                throw new IllegalArgumentException("Unknown token: " + part);
            }
        }

        return handleNegativeNumbers(tokens);
    }

    /**
     * Handle unary minus (negative numbers) by converting them to binary operations with 0.
     */
    private static List<Token> handleNegativeNumbers(List<Token> tokens) {
        List<Token> result = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Check for unary minus
            if (token.type == TokenType.OPERATOR && token.value.equals(Operator.SUBTRACT)) {
                // Unary minus if it's the first token or follows an operator, left parenthesis
                if (i == 0 ||
                        (result.size() > 0 && (
                                result.get(result.size() - 1).type == TokenType.OPERATOR ||
                                        result.get(result.size() - 1).type == TokenType.LEFT_PAREN ||
                                        result.get(result.size() - 1).type == TokenType.COMMA
                        ))) {
                    // Insert 0 and keep the minus to make it "0 - value"
                    result.add(new Token(TokenType.NUMBER, 0.0));
                }
            }

            result.add(token);
        }

        return result;
    }

    /**
     * Converts an infix expression to postfix notation using the Shunting Yard algorithm.
     */
    private static List<Token> toPostfix(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Stack<Token> operatorStack = new Stack<>();

        for (Token token : tokens) {
            switch (token.type) {
                case NUMBER:
                    output.add(token);
                    break;

                case FUNCTION:
                    operatorStack.push(token);
                    break;

                case COMMA:
                    // Process operators until left parenthesis
                    while (!operatorStack.isEmpty() && operatorStack.peek().type != TokenType.LEFT_PAREN) {
                        output.add(operatorStack.pop());
                    }
                    // If no left parenthesis, mismatched parentheses
                    if (operatorStack.isEmpty() || operatorStack.peek().type != TokenType.LEFT_PAREN) {
                        throw new IllegalArgumentException("Mismatched parentheses or commas");
                    }
                    break;

                case OPERATOR:
                    Operator currentOp = (Operator) token.value;

                    while (!operatorStack.isEmpty() && operatorStack.peek().type == TokenType.OPERATOR) {
                        Operator stackOp = (Operator) operatorStack.peek().value;

                        // Pop operators with higher precedence
                        if ((currentOp.precedence <= stackOp.precedence && currentOp != Operator.POWER) ||
                                (currentOp == Operator.POWER && currentOp.precedence < stackOp.precedence)) {
                            output.add(operatorStack.pop());
                        } else {
                            break;
                        }
                    }
                    operatorStack.push(token);
                    break;

                case LEFT_PAREN:
                    operatorStack.push(token);
                    break;

                case RIGHT_PAREN:
                    // Process operators until matching left parenthesis
                    while (!operatorStack.isEmpty() && operatorStack.peek().type != TokenType.LEFT_PAREN) {
                        output.add(operatorStack.pop());
                    }

                    // If the stack runs out without left parenthesis, mismatched parentheses
                    if (operatorStack.isEmpty()) {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }

                    // Discard the left parenthesis
                    operatorStack.pop();

                    // If the token at the top of the stack is a function, pop it onto the output queue
                    if (!operatorStack.isEmpty() && operatorStack.peek().type == TokenType.FUNCTION) {
                        output.add(operatorStack.pop());
                    }
                    break;
            }
        }

        // Pop any remaining operators from the stack to the output
        while (!operatorStack.isEmpty()) {
            Token token = operatorStack.pop();
            if (token.type == TokenType.LEFT_PAREN || token.type == TokenType.RIGHT_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(token);
        }

        return output;
    }

    /**
     * Evaluates a postfix expression.
     */
    private static double evaluatePostfix(List<Token> postfix) {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            switch (token.type) {
                case NUMBER:
                    stack.push((Double) token.value);
                    break;

                case OPERATOR:
                    // Need at least two operands for binary operations
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid expression: insufficient operands");
                    }

                    double b = stack.pop();
                    double a = stack.pop();

                    Operator op = (Operator) token.value;

                    switch (op) {
                        case ADD:
                            stack.push(a + b);
                            break;
                        case SUBTRACT:
                            stack.push(a - b);
                            break;
                        case MULTIPLY:
                            stack.push(a * b);
                            break;
                        case DIVIDE:
                            if (b == 0) {
                                throw new ArithmeticException("Division by zero");
                            }
                            stack.push(a / b);
                            break;
                        case POWER:
                            stack.push(Math.pow(a, b));
                            break;
                    }
                    break;

                case FUNCTION:
                    // Functions need one operand
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid expression: insufficient operands for function");
                    }

                    double arg = stack.pop();
                    String funcName = (String) token.value;

                    // Apply the function
                    Function<Double, Double> func = FUNCTIONS.get(funcName);
                    if (func != null) {
                        stack.push(func.apply(arg));
                    } else {
                        throw new IllegalArgumentException("Unknown function: " + funcName);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid token in postfix evaluation: " + token.type);
            }
        }

        // The final result should be the only element in the stack
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression: too many operands");
        }

        return stack.pop();
    }

    /**
     * Checks if a string is a valid number.
     */
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a system of ODEs from a vector of equation strings.
     *
     * @param equations Vector of equation strings, one equation per element
     * @return A function that computes the derivatives for the ODE system
     */
    public static BiFunction<Double, double[], double[]> parseEquations(Vector<String> equations) {
        StringBuilder sb = new StringBuilder();
        for (String eq : equations) {
            sb.append(eq).append("\n");
        }
        return parseSystem(sb.toString());
    }

    // Nested types for expression evaluation

    private enum TokenType {
        NUMBER, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN, COMMA
    }

    private enum Operator {
        ADD(1), SUBTRACT(1), MULTIPLY(2), DIVIDE(2), POWER(3);

        final int precedence;

        Operator(int precedence) {
            this.precedence = precedence;
        }
    }

    private static class Token {
        final TokenType type;
        final Object value;

        Token(TokenType type) {
            this.type = type;
            this.value = null;
        }

        Token(TokenType type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + (value != null ? "(" + value + ")" : "");
        }
    }
}