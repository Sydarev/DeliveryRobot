package ru.netology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                String str = generateRoute("RLRFR", 100);
                int count = 0;
                for (int j = 0; j < str.length(); j++) {
                    if (str.charAt(j) == 'R') count++;
                }
//                System.out.println("Число встреченных поворотов направо: " + count);
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey(count)) sizeToFreq.put(count, 1);
                    else sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                }
            }).start();
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int max = 0;
        int keyMax = 0;
        synchronized (sizeToFreq) {
            for (Integer cnt : sizeToFreq.keySet()) {
                if (sizeToFreq.get(cnt) > max) {
                    max = sizeToFreq.get(cnt);
                    keyMax = cnt;
                }
            }
        }

        System.out.println("Самое частое количество повторений " + keyMax + " (встретилось " + max + " раз)");
        System.out.println("Другие размеры:");
        synchronized (sizeToFreq) {
            for (Integer cnt : sizeToFreq.keySet()) {
                if (!(sizeToFreq.get(cnt) == keyMax))
                    System.out.println("- " + cnt + "(" + sizeToFreq.get(cnt) + "раз)");
            }
        }
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