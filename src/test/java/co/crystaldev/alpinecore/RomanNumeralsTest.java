package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.util.RomanNumerals;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Thomas Wearmouth
 */
class RomanNumeralsTest {
    // region convertTo()
    @Test
    void convertTo_withNormalNotation_returnsCorrect() {
        assertEquals(RomanNumerals.convertTo(1), "I");
        assertEquals(RomanNumerals.convertTo(2), "II");
        assertEquals(RomanNumerals.convertTo(3), "III");
        assertEquals(RomanNumerals.convertTo(5), "V");
        assertEquals(RomanNumerals.convertTo(10), "X");
        assertEquals(RomanNumerals.convertTo(11), "XI");
        assertEquals(RomanNumerals.convertTo(20), "XX");
        assertEquals(RomanNumerals.convertTo(30), "XXX");
        assertEquals(RomanNumerals.convertTo(50), "L");
        assertEquals(RomanNumerals.convertTo(100), "C");
        assertEquals(RomanNumerals.convertTo(200), "CC");
        assertEquals(RomanNumerals.convertTo(300), "CCC");
        assertEquals(RomanNumerals.convertTo(500), "D");
        assertEquals(RomanNumerals.convertTo(1000), "M");
        assertEquals(RomanNumerals.convertTo(2000), "MM");
        assertEquals(RomanNumerals.convertTo(3000), "MMM");
    }

    @Test
    void convertTo_withSubtractiveNotation_returnsCorrect() {
        assertEquals(RomanNumerals.convertTo(4), "IV");
        assertEquals(RomanNumerals.convertTo(9), "IX");
        assertEquals(RomanNumerals.convertTo(40), "XL");
        assertEquals(RomanNumerals.convertTo(90), "XC");
        assertEquals(RomanNumerals.convertTo(400), "CD");
        assertEquals(RomanNumerals.convertTo(900), "CM");
    }

    @Test
    void convertTo_withInvalidNumber_thenFail() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertTo(0));
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertTo(-1));
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertTo(4000));
    }
    // endregion

    // region convertFrom()
    @Test
    void convertFrom_withNormalNotation_returnsCorrect() {
        assertEquals(RomanNumerals.convertFrom("I"), 1);
        assertEquals(RomanNumerals.convertFrom("II"), 2);
        assertEquals(RomanNumerals.convertFrom("III"), 3);
        assertEquals(RomanNumerals.convertFrom("V"), 5);
        assertEquals(RomanNumerals.convertFrom("X"), 10);
        assertEquals(RomanNumerals.convertFrom("XI"), 11);
        assertEquals(RomanNumerals.convertFrom("XX"), 20);
        assertEquals(RomanNumerals.convertFrom("XXX"), 30);
        assertEquals(RomanNumerals.convertFrom("L"), 50);
        assertEquals(RomanNumerals.convertFrom("C"), 100);
        assertEquals(RomanNumerals.convertFrom("CC"), 200);
        assertEquals(RomanNumerals.convertFrom("CCC"), 300);
        assertEquals(RomanNumerals.convertFrom("D"), 500);
        assertEquals(RomanNumerals.convertFrom("M"), 1000);
        assertEquals(RomanNumerals.convertFrom("MM"), 2000);
        assertEquals(RomanNumerals.convertFrom("MMM"), 3000);
    }

    @Test
    void convertFrom_withSubtractiveNotation_returnsCorrect() {
        assertEquals(RomanNumerals.convertFrom("IV"), 4);
        assertEquals(RomanNumerals.convertFrom("IX"), 9);
        assertEquals(RomanNumerals.convertFrom("XL"), 40);
        assertEquals(RomanNumerals.convertFrom("XC"), 90);
        assertEquals(RomanNumerals.convertFrom("CD"), 400);
        assertEquals(RomanNumerals.convertFrom("CM"), 900);
    }

    @Test
    void convertFrom_withInvalidNumeral_thenFail() {
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertFrom("Test"));
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertFrom("MMXXIIIA"));
        assertThrows(IllegalArgumentException.class, () -> RomanNumerals.convertFrom("MMMM"));
    }
    // endregion
}
