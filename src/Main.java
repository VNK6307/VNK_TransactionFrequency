import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    final static String LETTERS = "RLRFR";
    final static int ROUTE_LENGTH = 100;
    final static int ROUTS_AMOUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        List<Thread> routsList = new ArrayList<>();
        Thread leaderOutput = new Thread(() -> {
            int k = 1;
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                System.out.println("Лидер после итерации " + k + ":");
                k++;
                int tempKey = outputLeader();
            }
        });
        leaderOutput.start();

        for (int i = 0; i < ROUTS_AMOUNT; i++) {
            routsList.add(createThread());
        }

        for (Thread route : routsList) {
            route.start();
        }

        for (Thread route : routsList) {
            route.join();
        }

        leaderOutput.interrupt();

        System.out.println("Окончательный результат:");
        int maxKey = outputLeader();
        sizeToFreq.remove(maxKey);
        System.out.println("Другие размеры:");
        sizeToFreq.keySet()
                .forEach(key -> System.out.println("- " + key + " (" + sizeToFreq.get(key) + " раз)"));

    }// main

    public static int outputLeader() {
        Integer maxKey = null;
        for (Integer key : sizeToFreq.keySet()) {
            if (maxKey == null || sizeToFreq.get(key) > sizeToFreq.get(maxKey)) {
                maxKey = key;
            }
        }
        System.out.println("Самое частое количество повторений " + maxKey + " (встретилось " + sizeToFreq.get(maxKey) + " раз)");
        return maxKey;
    }

    public static String generateRoute(String letters, int routeLength) {
        Random random = new Random();
        StringBuilder sbRoute = new StringBuilder();
        for (int i = 0; i < routeLength; i++) {
            sbRoute.append(letters.charAt(random.nextInt(letters.length())));
        }
        return sbRoute.toString();
    }// generateRoute

    public static Thread createThread() {
        return new Thread(() -> {
            String route = generateRoute(LETTERS, ROUTE_LENGTH);
            int quantity = symbolCounting(route);
            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(quantity)) {
                    sizeToFreq.put(quantity, sizeToFreq.get(quantity) + 1);
                } else {
                    sizeToFreq.put(quantity, 1);
                }
                sizeToFreq.notify();
            }
        });
    }// createThread

    public static int symbolCounting(String text) {
        int quantity = 0;
        for (char letter : text.toCharArray()) {
            if (letter == 'R') {
                quantity++;
            }
        }
        return quantity;
    }// symbolCounting
}// class