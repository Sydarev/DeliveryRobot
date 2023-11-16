package ru.netology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.submit(new Thread(() -> {
            int max = 0;
            int keyMax = 0;
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i : sizeToFreq.keySet())
                        if (sizeToFreq.get(i) > max) {
                            max = sizeToFreq.get(i);
                            keyMax = i;
                        }
                    System.out.println("Текущий лидер: " + keyMax + " - " + max);
                }
            }
        }));

        for (int i = 0; i < 100; i++) {
            executorService.submit(new Thread(() -> {
                String str = generateRoute("RLRFR", 100);
                int count = 0;
                for (int j = 0; j < str.length(); j++) {
                    if (str.charAt(j) == 'R') count++;
                }
                System.out.println("Число встреченных поворотов направо: " + count);
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey(count)) sizeToFreq.put(count, 1);
                    else sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                    sizeToFreq.notify();
                }
            }));
        }
        while (true) {
            try {
                if (!executorService.awaitTermination(1, TimeUnit.MILLISECONDS)) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdownNow();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}