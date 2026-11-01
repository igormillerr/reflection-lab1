package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

@SuppressWarnings("unchecked")
public class Generator {

    private final Random random;

    public Generator(Random random) {
        this.random = random;
    }

    public Generator() {
        this(new Random());
    }

    public <T> T generateValueOfType(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (clazz == int.class || clazz == Integer.class) {
            Integer value = random.nextInt(-100, 101);
            return (T) value;
        } else if (clazz == short.class || clazz == Short.class) {
            int value = random.nextInt(-100, 101);
            Short result = (short) value;
            return (T) result;
        } else if (clazz == byte.class || clazz == Byte.class) {
            int value = random.nextInt(-100, 101);
            Byte result = (byte) value;
            return (T) result;
        } else if (clazz == long.class || clazz == Long.class) {
            Long value = random.nextLong(-100, 101);
            return (T) value;
        } else if (clazz == float.class || clazz == Float.class) {
            Float value = random.nextFloat(-100.0f, 100.0f);
            return (T) value;
        } else if (clazz == double.class || clazz == Double.class) {
            Double value = random.nextDouble(-100.0, 100.0);
            return (T) value;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            Boolean value = random.nextBoolean();
            return (T) value;
        } else if (clazz == char.class || clazz == Character.class) {
            int code = random.nextInt('a', 'z' + 1);
            Character value = (char) code;
            return (T) value;
        } else if (clazz == String.class) {
            int length = random.nextInt(1, 16);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                int code = random.nextInt('a', 'z' + 1);
                builder.append((char) code);
            }
            return (T) builder.toString();
        } else if (clazz.isEnum()) {
            T[] constants = clazz.getEnumConstants();
            if (constants.length == 0) {
                throw new IllegalArgumentException(
                        "Cannot generate value: enum " + clazz.getName() + " has no constants"
                );
            }
            int value = random.nextInt(0, constants.length);
            return constants[value];
        } else if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            int length = random.nextInt(1, 6);
            Object result = Array.newInstance(componentType, length);

            for (int i = 0; i < length; i++) {
                Array.set(result, i, this.generateValueOfType(componentType));
            }

            return clazz.cast(result);
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        int randomConstructorIndex = random.nextInt(constructors.length);
        Constructor<?> randomConstructor = constructors[randomConstructorIndex];
        return clazz.cast(randomConstructor.newInstance(0));
    }


}
