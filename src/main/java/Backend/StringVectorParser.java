package Backend;
import java.util.Vector;

public class StringVectorParser {
    public static double[] parseVector(Vector<String> stringVector) {
        String joinedString = String.join(",", stringVector);
        String[] stringArray = joinedString.split(",");
        double[] result = new double[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            try {
                result[i] = Double.parseDouble(stringArray[i].trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format at index " + i + ": " + stringArray[i]);
                result[i] = Double.NaN; // Assign NaN for invalid values
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Vector<String> stringVector = new Vector<>();
        stringVector.add("3.14,2.71,1.618,not_a_number"); // Example input with comma-separated values

        double[] parsedDoubles = parseVector(stringVector);

        for (double num : parsedDoubles) {
            System.out.println(num);
        }
    }
}
