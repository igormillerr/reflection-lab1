package org.example;


import org.example.classes.Example;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenerateExample {
    public static void main(String[] args) {
        Generator generator = new Generator();
        try {
            Object generated = generator.generateValueOfType(Example.class);
            Integer number = generator.generateValueOfType(Integer.class);
            String text = generator.generateValueOfType(String.class);
            int[] numbers = generator.generateValueOfType(int[].class);
            List<?> items = generator.generateValueOfType(List.class);
            Map<?, ?> entries = generator.generateValueOfType(Map.class);
            Triangle triangle = generator.generateValueOfType(Triangle.class);
            Shape shape = generator.generateValueOfType(Shape.class);

            System.out.println("Object: " + generated);
            System.out.println("Integer: " + number);
            System.out.println("String: " + text);
            System.out.println("int[]: " + Arrays.toString(numbers));
            System.out.println("List<?>: " + items);
            System.out.println("Map<?, ?>: " + entries);
            System.out.println("Triangle: " + triangle.getPerimeter());
            System.out.println("Shape: " + shape.getArea());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}