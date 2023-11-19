package ru.netology;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        Thread checkThread = new Thread(() -> {
            int max = 0;
            int keyMax = 0;
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (int i : sizeToFreq.keySet())
                        if (sizeToFreq.get(i) > max) {
                            max = sizeToFreq.get(i);
                            keyMax = i;
                        }
                    System.out.println("Текущий лидер: " + keyMax + " - " + max);
                }
            }
        });
        checkThread.start();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threadList.add(new Thread(() -> {
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
        for (Thread thread : threadList) thread.start();
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (checkThread.isAlive()) checkThread.interrupt();

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