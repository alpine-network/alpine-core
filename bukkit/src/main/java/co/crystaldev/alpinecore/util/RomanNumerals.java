package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Utility for converting strings to and from roman numerals.
 *
 * @see <a href="https://stackoverflow.com/a/19759564">Converting Integers to Roman Numerals</a>
 * @author Ben-Hur Langoni Junior
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@UtilityClass
public final class RomanNumerals {
    // This pattern is very simple, does not check the validity of the numerals just that the characters are valid
    private static final Pattern REGEX = Pattern.compile("[IVXLCDM]+");
    private static final TreeMap<Integer, String> toNumeral = new TreeMap<>();
    private static final HashMap<String, Integer> fromNumeral = new HashMap<>();

    static {
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
    }

    /**
     * Convert a number to its roman numeral representation.
     *
     * @param number the number
     * @return the roman numeral representation
     */
    public static @NotNull String convertTo(int number) {
        Validate.isTrue(number > 0 && number < 4000, "number has no roman numeral equivalent");
        int l = toNumeral.floorKey(number);
        if (number == l) {
            return toNumeral.get(number);
        }
        return toNumeral.get(l) + convertTo(number - l);
    }

    /**
     * Convert roman numerals to its numeric representation.
     *
     * @param number the roman numerals
     * @return the numeric representation
     */
    // TODO: can we find a better way to do this?
    public static int convertFrom(@NotNull String number) {
        Validate.isTrue(REGEX.matcher(number).matches(), "string not valid roman numeral");
        int result = 0;
        int i = 0;

        while (i < number.length()) {
            char currentChar = number.charAt(i);
            int currentVal = valueOf(currentChar);

            if (i + 1 < number.length()) {
                // Look ahead to account for subtraction values
                char nextChar = number.charAt(i + 1);
                int nextVal = valueOf(nextChar);

                if (currentVal < nextVal) {
                    result += nextVal - currentVal;
                    i += 2;
                }
                else {
                    result += currentVal;
                    i += 1;
                }
            }
            else {
                result += currentVal;
                i += 1;
            }
        }

        Validate.isTrue(result > 0 && result < 4000, "number has no roman numeral equivalent");
        return result;
    }

    private static int valueOf(char character) {
        return fromNumeral.get(String.valueOf(character));
    }

    private static void put(int numeric, String numeral) {
        toNumeral.put(numeric, numeral);
        fromNumeral.put(numeral, numeric);
    }
}
