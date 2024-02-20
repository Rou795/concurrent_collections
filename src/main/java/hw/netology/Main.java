package hw.netology;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static ArrayBlockingQueue queue1 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue queue2 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue queue3 = new ArrayBlockingQueue<>(100);

    public static final int ROW_NUM = 100_000;
    public static final int SYM_NUM = 10_000;
    public static final String LETTERS = "abc";

    public static void main(String[] args) throws InterruptedException {
        Thread threadFill = new Thread(() -> {
            for (int i = 0; i < ROW_NUM; i++) {
                String row = generateText(LETTERS, SYM_NUM);
                try {
                    queue1.put(row);
                    queue2.put(row);
                    queue3.put(row);
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.println("Поток наполнения закончил");
        });

        threadFill.start();
        System.out.println("Поток наполнения стартовал");
        List<Thread> threads = new ArrayList<>();

        Thread threadA = new Thread(() -> {
            String maxRow = null;
            try {
                maxRow = maxCountFinder("a", queue1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print("Поток А завершился. Макимальная частота: ");
            System.out.println(maxRow.length() - maxRow.replaceAll("a", "").length());
            System.out.println("Строка для 'a': " + maxRow);
        });
        Thread threadB = new Thread(() -> {
            String maxRow = null;
            try {
                maxRow = maxCountFinder("b", queue2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print("Поток В завершился. Макимальная частота: ");
            System.out.println(maxRow.length() - maxRow.replaceAll("b", "").length());
            System.out.println("Строка для 'b': " + maxRow);
        });
        Thread threadC = new Thread(() -> {
            String maxRow = null;
            try {
                maxRow = maxCountFinder("c", queue3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print("Поток С завершился. Макимальная частота: ");
            System.out.println(maxRow.length() - maxRow.replaceAll("c", "").length());
            System.out.println("Строка для 'c': " + maxRow);
        });

        threadA.start();
        System.out.println("Поток А стартовал");
        threadB.start();
        System.out.println("Поток B стартовал");
        threadC.start();
        System.out.println("Поток C стартовал");
        threads.add(threadA);
        threads.add(threadB);
        threads.add(threadC);
        threadFill.join();
        for (Thread thread : threads) {
            thread.join();
        }
    }

// метод для генерации текста

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

// метод для определения строки с наибольшим кол-вом опр. символа.

    public static String maxCountFinder(String letter, ArrayBlockingQueue queue) throws InterruptedException {
        int maxCount = Integer.MIN_VALUE;
        String maxRow = "";
        int count;
        for (int i = 0; i < ROW_NUM; i++) {
            String row = (String) queue.take();
            String modRow = row.replaceAll(letter, "");
            count = row.length() - modRow.length();
            if (count > maxCount) {
                maxRow = row;
                maxCount = count;
            }
        }
        return maxRow;
    }
}