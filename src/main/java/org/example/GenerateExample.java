package org.example;


import org.example.classes.Example;
import org.example.generator.Generator;

public class GenerateExample {
    public static void main(String[] args) {
        Generator generator = new Generator();
        try {
            Object generated = generator.generateValueOfType(Example.class);
            System.out.println(generated);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}