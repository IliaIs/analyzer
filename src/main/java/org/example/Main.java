package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queueCharA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueCharB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueCharC = new ArrayBlockingQueue<>(100);
    public static Thread generate;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        generate = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueCharA.put(text);
                    queueCharB.put(text);
                    queueCharC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        generate.start();
        Thread charA = new Thread(() -> {
            countChar(queueCharA, 'a');
        });
        threads.add(charA);
        Thread charB = new Thread(() -> {
            countChar(queueCharB, 'b');
        });
        threads.add(charB);
        Thread charC = new Thread(() -> {
            countChar(queueCharC, 'c');
        });
        threads.add(charC);

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void countChar(BlockingQueue<String> queue, char letter) {
        int max = 0;
        while (generate.isAlive()) {
            try {
                String text = queue.take();
                int count = text.length() - text.replace(String.valueOf(letter), "").length();
                if (max < count) {
                    max = count;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Максимальное кол-во символов " + letter + " : " + max);
    }
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}