package org.menesty.ikea.tablet.util;

import java.math.BigDecimal;

/**
 * Created by Menesty on 2/21/14.
 */
public class NumberUtil {
    public static String toString(double value) {
        value = round(value);

        if (value % 1 == 0)
            return (int) value + "";

        return value + "";
    }

    public static double round(double value) {
        return round(value, 3);
    }

    public static double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_CEILING).doubleValue();
    }

    public static double parse(String value) {
        try {
            return Double.valueOf(value.replaceAll("[^0-9.]+", ""));
        } catch (NumberFormatException e) {
            return 0d;
        }
    }
}
