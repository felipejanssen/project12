package Utils;

/**
 * Class for vector operations.
 */
public abstract class vec {

    public static double[] multiply(double[] vector, double scalar) {
        int n = vector.length;
        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            result[i] = vector[i] * scalar;
        }

        return result;
    }

    public static double[] add(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("The input vectors must have the same length");
        }
        int n = vector1.length;
        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            result[i] = vector1[i] + vector2[i];
        }

        return result;
    }

    public static double[] dot(double[] vector1, double[] vector2) {

        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("The input vectors must have the same length");
        }

        int n = vector1.length;
        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            result[i] = vector1[i] * vector2[i];
        }

        return result;
    }
}
