
package org.example.generator;

import org.example.classes.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    private enum EmptyEnum {
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("integralTypes")
    void shouldGenerateIntegralNumbers(
            Class<?> requestedType,
            Class<?> expectedType
    ) throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<Object> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            Object result = generator.generateValueOfType(requestedType);

            assertNotNull(result);
            assertInstanceOf(expectedType, result);

            long value = ((Number) result).longValue();
            assertTrue(value >= -100 && value <= 100, "Value must be in range [-100, 100]");

            generatedValues.add(result);
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different values");
    }

    static Stream<Arguments> integralTypes() {
        return Stream.of(
                Arguments.of(int.class, Integer.class),
                Arguments.of(Integer.class, Integer.class),
                Arguments.of(byte.class, Byte.class),
                Arguments.of(Byte.class, Byte.class),
                Arguments.of(short.class, Short.class),
                Arguments.of(Short.class, Short.class),
                Arguments.of(long.class, Long.class),
                Arguments.of(Long.class, Long.class)
        );
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("floatingPointTypes")
    void shouldGenerateFloatingPointNumbers(
            Class<?> requestedType,
            Class<?> expectedType
    ) throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<Object> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            Object result = generator.generateValueOfType(requestedType);

            assertNotNull(result);
            assertInstanceOf(expectedType, result);

            double value = ((Number) result).doubleValue();
            assertTrue(value >= -100.0 && value < 100.0, "Value must be in range [-100.0, 100.0)");

            generatedValues.add(result);
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different values");
    }

    static Stream<Arguments> floatingPointTypes() {
        return Stream.of(
                Arguments.of(float.class, Float.class),
                Arguments.of(Float.class, Float.class),
                Arguments.of(double.class, Double.class),
                Arguments.of(Double.class, Double.class)
        );
    }


    @ParameterizedTest
    @ValueSource(classes = {boolean.class, Boolean.class})
    void shouldGenerateBoolean(Class<?> clazz) throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<Boolean> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            Object result = generator.generateValueOfType(clazz);

            Boolean value = assertInstanceOf(Boolean.class, result);
            generatedValues.add(value);
        }

        assertEquals(Set.of(true, false), generatedValues, "Generator should produce both true and false");
    }

    @ParameterizedTest
    @ValueSource(classes = {char.class, Character.class})
    void shouldGenerateCharacter(Class<?> clazz) throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<Character> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            Object result = generator.generateValueOfType(clazz);

            Character value = assertInstanceOf(Character.class, result);

            assertTrue(value >= 'a' && value <= 'z', "Character must be between 'a' and 'z'");

            generatedValues.add(value);
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different characters");
    }

    @Test
    void shouldGenerateString() throws Exception {
        Generator generator = new Generator(new Random(42));

        for (int i = 0; i < 30; i++) {
            String result = generator.generateValueOfType(String.class);

            assertNotNull(result);
            assertTrue(!result.isEmpty() && result.length() <= 15, "String length must be between 1 and 15");
            assertTrue(result.matches("[a-z]+"), "String must contain only lowercase Latin letters");
        }
    }

    @Test
    void shouldGenerateDifferentStrings() throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<String> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            generatedValues.add(generator.generateValueOfType(String.class));
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different strings");
    }

    @Test
    void shouldGenerateEnum() throws Exception {
        Generator generator = new Generator(new Random(42));

        for (int i = 0; i < 30; i++) {
            Color result = generator.generateValueOfType(Color.class);

            assertNotNull(result);
            assertTrue(EnumSet.allOf(Color.class).contains(result), "Result must be a declared enum constant");
        }
    }

    @Test
    void shouldGenerateDifferentEnums() throws Exception {
        Generator generator = new Generator(new Random(42));
        Set<Color> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            generatedValues.add(generator.generateValueOfType(Color.class));
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different enum constants");
    }

    @Test
    void shouldRejectEmptyEnum() {
        Generator generator = new Generator(new Random(42));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateValueOfType(EmptyEnum.class)
        );

        assertTrue(exception.getMessage().contains(EmptyEnum.class.getName()), "Exception message should contain the enum class name");
    }


    @Test
    void shouldGenerateIntArray() throws Exception {
        Generator generator = new Generator(new Random(42));

        int[] result = generator.generateValueOfType(int[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (int value : result) {
            assertTrue(value >= -100 && value <= 100);
        }
    }

    @Test
    void shouldGenerateStringArray() throws Exception {
        Generator generator = new Generator(new Random(42));

        String[] result = generator.generateValueOfType(String[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (String value : result) {
            assertNotNull(value);
            assertTrue(value.matches("[a-z]+"));
            assertTrue(!value.isEmpty() && value.length() <= 15);
        }
    }

    @Test
    void shouldGenerateBooleanArray() throws Exception {
        Generator generator = new Generator(new Random(42));

        Boolean[] result = generator.generateValueOfType(Boolean[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (Boolean value : result) {
            assertNotNull(value);
        }
    }

    @Test
    void shouldGenerateCharArray() throws Exception {
        Generator generator = new Generator(new Random(42));

        char[] result = generator.generateValueOfType(char[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (char value : result) {
            assertTrue(value >= 'a' && value <= 'z');
        }
    }

    @Test
    void shouldGenerateEnumArray() throws Exception {
        Generator generator = new Generator(new Random(42));

        Color[] result = generator.generateValueOfType(Color[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (Color value : result) {
            assertNotNull(value);
            assertTrue(EnumSet.allOf(Color.class).contains(value));
        }
    }

}
