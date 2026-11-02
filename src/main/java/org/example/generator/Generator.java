package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        } else if (clazz == List.class) {
            ArrayList<String> result = new ArrayList<>();
            int length = random.nextInt(1, 6);

            for (int i = 0; i < length; i++) {
                String value = this.generateValueOfType(String.class);
                result.add(value);
            }

            return clazz.cast(result);
        } else if (clazz == Map.class) {
            Map<String, Integer> result = new HashMap<>();
            int length = random.nextInt(1, 6);

            while (result.size() < length) {
                String key = this.generateValueOfType(String.class);
                Integer value = this.generateValueOfType(Integer.class);
                result.put(key, value);
            }

            return clazz.cast(result);
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot generate value: " + clazz.getName() + " has no public constructors"
            );
        }
        int randomConstructorIndex = random.nextInt(constructors.length);
        Constructor<?> randomConstructor = constructors[randomConstructorIndex];

        Type[] parameterTypes = randomConstructor.getGenericParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = this.generateValueOfType(parameterTypes[i]);
        }

        return clazz.cast(randomConstructor.newInstance(arguments));
    }

    private Object generateValueOfType(Type type) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type instanceof Class<?> clazz) {
            return generateValueOfType(clazz);
        } else if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            if (rawType == List.class) {
                Type[] arguments = parameterizedType.getActualTypeArguments();
                Type elementType = arguments[0];

                List<Object> result = new ArrayList<>();
                int length = random.nextInt(1, 6);
                for (int i = 0; i < length; i++) {
                    result.add(this.generateValueOfType(elementType));
                }

                return result;
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getTypeName());
    }

}
