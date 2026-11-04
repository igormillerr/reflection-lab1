package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unchecked")
public class Generator {

    private static final int MAX_CLASS_REPETITIONS = 3;

    private final Random random;

    public Generator(Random random) {
        this.random = Objects.requireNonNull(random, "random must not be null");
    }

    public Generator() {
        this(new Random());
    }

    public <T> T generateValueOfType(Class<T> clazz)
            throws InvocationTargetException {
        Objects.requireNonNull(clazz, "clazz must not be null");
        return generateValueOfType(clazz, new ArrayDeque<>());
    }

    private Object generateValueOfType(Type type, Deque<Class<?>> path)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type instanceof Class<?> clazz) {
            return generateValueOfType(clazz, path);
        } else if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            if (rawType == List.class) {
                Type[] arguments = parameterizedType.getActualTypeArguments();
                Type elementType = arguments[0];

                List<Object> result = new ArrayList<>();
                int length = random.nextInt(1, 6);
                for (int i = 0; i < length; i++) {
                    result.add(this.generateValueOfType(elementType, path));
                }

                return result;
            } else if (rawType == Map.class) {
                Type[] arguments = parameterizedType.getActualTypeArguments();
                Type keyType = arguments[0];
                Type valueType = arguments[1];

                Map<Object, Object> result = new HashMap<>();
                int length = random.nextInt(1, 6);

                for (int i = 0; i < length; i++) {
                    Object key = generateValueOfType(keyType, path);
                    Object value = generateValueOfType(valueType, path);
                    result.put(key, value);
                }

                return result;
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getTypeName());
    }

    private <T> T generateValueOfType(Class<T> clazz, Deque<Class<?>> path)
            throws InvocationTargetException {
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
                Array.set(result, i, generateValueOfType(componentType, path));
            }

            return clazz.cast(result);
        } else if (clazz == List.class) {
            ArrayList<String> result = new ArrayList<>();
            int length = random.nextInt(1, 6);

            for (int i = 0; i < length; i++) {
                String value = this.generateValueOfType(String.class, path);
                result.add(value);
            }

            return clazz.cast(result);
        } else if (clazz == Map.class) {
            Map<String, Integer> result = new HashMap<>();
            int length = random.nextInt(1, 6);

            for (int i = 0; i < length; i++) {
                String key = generateValueOfType(String.class, path);
                Integer value = generateValueOfType(Integer.class, path);
                result.put(key, value);
            }

            return clazz.cast(result);
        } else if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            Generatable annotation = clazz.getAnnotation(Generatable.class);
            if (annotation == null) {
                throw new IllegalArgumentException(
                        "Missing @Generatable on " + clazz.getName()
                );
            }
            Class<?>[] implementations = annotation.implementations();
            if (implementations.length == 0) {
                throw new IllegalArgumentException("No implementations declared for " + clazz.getName());
            }

            boolean hasInvalidImplementation = Arrays.stream(implementations)
                    .anyMatch(implementation ->
                            !clazz.isAssignableFrom(implementation)
                                    || implementation.isInterface()
                                    || Modifier.isAbstract(implementation.getModifiers())
                    );
            if (hasInvalidImplementation) {
                throw new IllegalArgumentException("Invalid @Generatable implementations for " + clazz.getName());
            }

            int randomImplementationIndex = random.nextInt(implementations.length);
            return clazz.cast(generateValueOfType(implementations[randomImplementationIndex], path));
        }

        if (Collections.frequency(path, clazz) >= MAX_CLASS_REPETITIONS) {
            return null;
        }

        path.addLast(clazz);
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new IllegalArgumentException(
                        "Cannot generate value: " + clazz.getName() + " has no public constructors"
                );
            }

            int startIndex = random.nextInt(constructors.length);
            IllegalStateException failure = new IllegalStateException(
                    "Cannot generate value: " + clazz.getName() + " - all public constructors failed"
            );

            for (int i = 0; i < constructors.length; i++) {
                int index = (startIndex + i) % constructors.length;
                Constructor<?> constructor = constructors[index];

                try {
                    Type[] parameterTypes = constructor.getGenericParameterTypes();
                    Object[] arguments = new Object[parameterTypes.length];

                    for (int j = 0; j < parameterTypes.length; j++) {
                        arguments[j] = generateValueOfType(parameterTypes[j], path);
                    }

                    try {
                        return clazz.cast(constructor.newInstance(arguments));
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException(
                                "Constructor failed: " + constructor,
                                e.getCause()
                        );
                    }

                } catch (IllegalArgumentException | IllegalStateException
                         | InstantiationException | IllegalAccessException e) {
                    failure.addSuppressed(e);
                }
            }
            Throwable[] failures = failure.getSuppressed();
            if (failures.length == 1 && failures[0] instanceof IllegalStateException e) {
                Throwable cause = e.getCause();

                if (cause != null) {
                    throw new IllegalStateException(failure.getMessage(), cause);
                }
            }
            throw failure;

        } finally {
            path.removeLast();
        }
    }
}
