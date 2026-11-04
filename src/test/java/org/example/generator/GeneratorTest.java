
package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Color;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    private static final Generator generator = new Generator(new Random(42));

    private enum EmptyEnum {
    }

    public static class MixedConstructors {
        private MixedConstructors(String value) {
        }

        public MixedConstructors(int value) {
        }
    }

    public static class PrivateConstructorOnly {
        private PrivateConstructorOnly() {
        }
    }

    public static class NoArgsConstructor {
        public NoArgsConstructor() {
        }
    }


    public static class ProductMapHolder {
        private final Map<Integer, Product> products;

        public ProductMapHolder(Map<Integer, Product> products) {
            this.products = products;
        }

        public Map<Integer, Product> getProducts() {
            return products;
        }
    }

    public static class BooleanMapHolder {
        private final Map<Boolean, String> values;

        public BooleanMapHolder(Map<Boolean, String> values) {
            this.values = values;
        }

        public Map<Boolean, String> getValues() {
            return values;
        }
    }

    public static class RecursiveA {
        public final RecursiveB next;

        public RecursiveA(RecursiveB next) {
            this.next = next;
        }
    }

    public static class RecursiveB {
        public final RecursiveA next;

        public RecursiveB(RecursiveA next) {
            this.next = next;
        }
    }

    @Generatable(implementations = {Dog.class})
    public static abstract class Animal {
    }

    public static class Dog extends Animal {
        public Dog() {
        }
    }

    @Generatable(implementations = {String.class})
    public interface InvalidShape {
    }

    public static class FailingConstructor {
        public FailingConstructor() {
            throw new IllegalArgumentException("Invalid constructor arguments");
        }
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("integralTypes")
    void shouldGenerateIntegralNumbers(
            Class<?> requestedType,
            Class<?> expectedType
    ) throws Exception {
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
        for (int i = 0; i < 30; i++) {
            String result = generator.generateValueOfType(String.class);

            assertNotNull(result);
            assertTrue(
                    !result.isEmpty() && result.length() <= 15,
                    "String length must be between 1 and 15"
            );
            assertTrue(result.matches("[a-z]+"), "String must contain only lowercase Latin letters");
        }
    }

    @Test
    void shouldGenerateDifferentStrings() throws Exception {
        Set<String> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            generatedValues.add(generator.generateValueOfType(String.class));
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different strings");
    }

    @Test
    void shouldGenerateEnum() throws Exception {
        for (int i = 0; i < 30; i++) {
            Color result = generator.generateValueOfType(Color.class);

            assertNotNull(result);
            assertTrue(EnumSet.allOf(Color.class).contains(result), "Result must be a declared enum constant");
        }
    }

    @Test
    void shouldGenerateDifferentEnums() throws Exception {

        Set<Color> generatedValues = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            generatedValues.add(generator.generateValueOfType(Color.class));
        }

        assertTrue(generatedValues.size() > 1, "Generator should produce different enum constants");
    }

    @Test
    void shouldRejectEmptyEnum() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateValueOfType(EmptyEnum.class)
        );

        assertTrue(
                exception.getMessage().contains(EmptyEnum.class.getName()),
                "Exception message should contain the enum class name"
        );
    }


    @Test
    void shouldGenerateIntArray() throws Exception {
        int[] result = generator.generateValueOfType(int[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (int value : result) {
            assertTrue(value >= -100 && value <= 100);
        }
    }

    @Test
    void shouldGenerateStringArray() throws Exception {
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
        Boolean[] result = generator.generateValueOfType(Boolean[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (Boolean value : result) {
            assertNotNull(value);
        }
    }

    @Test
    void shouldGenerateCharArray() throws Exception {
        char[] result = generator.generateValueOfType(char[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (char value : result) {
            assertTrue(value >= 'a' && value <= 'z');
        }
    }

    @Test
    void shouldGenerateEnumArray() throws Exception {
        Color[] result = generator.generateValueOfType(Color[].class);

        assertNotNull(result);
        assertTrue(result.length >= 1 && result.length <= 5);

        for (Color value : result) {
            assertNotNull(value);
            assertTrue(EnumSet.allOf(Color.class).contains(value));
        }
    }


    @ParameterizedTest(name = "Generate {0}")
    @ValueSource(classes = {
            Example.class,
            Rectangle.class,
            Triangle.class,
            Product.class
    })
    void shouldGenerateUserDefinedClass(Class<?> clazz) throws Exception {
        Object result = generator.generateValueOfType(clazz);

        assertNotNull(result);
        assertInstanceOf(clazz, result);
    }


    @Test
    void shouldUseOnlyPublicConstructors() throws Exception {
        for (int i = 0; i < 30; i++) {
            MixedConstructors result =
                    generator.generateValueOfType(MixedConstructors.class);

            assertNotNull(result);
        }
    }

    @Test
    void shouldRejectClassWithoutPublicConstructors() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateValueOfType(PrivateConstructorOnly.class)
        );

        assertTrue(
                exception.getMessage().contains(
                        PrivateConstructorOnly.class.getName()
                )
        );
    }


    @Test
    void shouldGenerateList() throws Exception {
        List<?> result = generator.generateValueOfType(List.class);

        assertNotNull(result);
        assertTrue(!result.isEmpty() && result.size() <= 5);
        assertTrue(result.stream().allMatch(String.class::isInstance));
    }

    @Test
    void shouldGenerateMap() throws Exception {
        Map<?, ?> result = generator.generateValueOfType(Map.class);

        assertNotNull(result);
        assertTrue(!result.isEmpty() && result.size() <= 5);
        assertTrue(result.keySet().stream().allMatch(String.class::isInstance));
        assertTrue(result.values().stream().allMatch(Integer.class::isInstance));
    }

    @Test
    void shouldGenerateCartWithProducts() throws Exception {
        Cart cart = generator.generateValueOfType(Cart.class);

        assertNotNull(cart);

        List<Product> items = cart.getItems();

        assertNotNull(items);
        assertTrue(!items.isEmpty() && items.size() <= 5);
        assertTrue(items.stream().allMatch(Product.class::isInstance));
    }

    @Test
    void shouldGenerateParameterizedMap() throws Exception {
        ProductMapHolder holder = generator.generateValueOfType(ProductMapHolder.class);

        Map<?, ?> products = holder.getProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.size() <= 5);

        for (Map.Entry<?, ?> entry : products.entrySet()) {
            assertInstanceOf(Integer.class, entry.getKey());
            assertInstanceOf(Product.class, entry.getValue());
        }
    }

    @Test
    void shouldGenerateMapWithLimitedUniqueKeys() throws Exception {
        BooleanMapHolder holder = generator.generateValueOfType(BooleanMapHolder.class);

        Map<?, ?> values = holder.getValues();

        assertNotNull(values);
        assertFalse(values.isEmpty());
        assertTrue(values.size() <= 2);

        for (Map.Entry<?, ?> entry : values.entrySet()) {
            assertInstanceOf(Boolean.class, entry.getKey());
            assertInstanceOf(String.class, entry.getValue());
        }
    }



    @Test
    void shouldGenerateBinaryTreeNode() throws Exception {
        BinaryTreeNode root = generator.generateValueOfType(BinaryTreeNode.class);

        assertTreeStructure(root, 3);
    }

    private static void assertTreeStructure(BinaryTreeNode node, int remainingLevels) {
        assertNotNull(node);

        if (remainingLevels == 1) {
            assertNull(node.getLeft());
            assertNull(node.getRight());
            return;
        }

        assertTreeStructure(node.getLeft(), remainingLevels - 1);
        assertTreeStructure(node.getRight(), remainingLevels - 1);
    }


    @Test
    void shouldHandleMutualRecursion() throws Exception {
        RecursiveA root = generator.generateValueOfType(RecursiveA.class);

        assertNotNull(root);
        assertNotNull(root.next);
        assertNotNull(root.next.next);
        assertNotNull(root.next.next.next);
        assertNotNull(root.next.next.next.next);
        assertNotNull(root.next.next.next.next.next);

        assertNull(root.next.next.next.next.next.next);
    }

    @Test
    void shouldGenerateInterfaceImplementation() throws Exception {
        Shape shape = generator.generateValueOfType(Shape.class);

        assertNotNull(shape);
        assertTrue(shape instanceof Rectangle || shape instanceof Triangle);
    }

    @Test
    void shouldGenerateAbstractClassImplementation() throws Exception {
        Animal animal = generator.generateValueOfType(Animal.class);

        assertInstanceOf(Dog.class, animal);
    }

    @Test
    void shouldRejectInvalidImplementation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generateValueOfType(InvalidShape.class)
        );
    }

    @Test
    void shouldPreserveConstructorExceptionCause() {
        Generator generator = new Generator(new Random(42));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> generator.generateValueOfType(FailingConstructor.class)
        );

        assertTrue(exception.getMessage().contains(FailingConstructor.class.getName()));

        Throwable cause = exception.getCause();
        assertInstanceOf(IllegalArgumentException.class, cause);
        assertEquals("Invalid constructor arguments", cause.getMessage());
    }
}
