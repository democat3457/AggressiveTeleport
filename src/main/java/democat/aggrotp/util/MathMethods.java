package democat.aggrotp.util;

public class MathMethods {
    public static double randomRange(double min, double max) {
        double offset = max - min;
        return min + (Math.random() * (offset + 1));
    }
    public static double randomRangeSec(double min, double max) {
        return randomRange(min * 20, max * 20);
    }
}