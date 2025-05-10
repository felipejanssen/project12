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

    public static double[] substract(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("The input vectors must have the same length");
        }
        int n = vector1.length;
        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            result[i] = vector1[i] - vector2[i];
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

    public static double[] normalize(double[] v) {
        double mag = magnitude(v);
        if (mag == 0) {
            // avoid division by zero; return zero‐vector or throw
            return new double[v.length];
        }
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] / mag;
        }
        return result;
    }

    public static double magnitude(double[] vec) {
        double sum = 0;

        for (int i = 0; i < vec.length; i++) {
            sum += vec[i] * vec[i];
        }
        return Math.sqrt(sum);
    }

    public static double euclideanDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }
}
