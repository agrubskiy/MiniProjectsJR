package urlshortener;

import urlshortener.strategy.HashMapStorageStrategy;
import urlshortener.strategy.StorageStrategy;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main(String... args) {
        testStrategy(new HashMapStorageStrategy(), 10000);
    }

    public static Set<Long> getIds(Shortener shortener, Set<String> strings) {

        Set<Long> result = new HashSet<>();

        for (String string : strings) {
            result.add(shortener.getId(string));
        }

        return result;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {

        Set<String> result = new HashSet<>();

        for (Long l : keys) {
            result.add(shortener.getString(l));
        }

        return result;
    }

    public static void testStrategy(StorageStrategy strategy, long elementNumbers) {

        Helper.printMessage(strategy.getClass().getSimpleName());


        Set<String> strings = new HashSet<>();
        for (int i = 0; i < elementNumbers; i++) {
            strings.add(Helper.generateRandomString());
        }

        Shortener shortener = new Shortener(strategy);

        long s = new Date().getTime();
        Set<Long> setIds = getIds(shortener, strings);
        long e = new Date().getTime();

        String result = String.valueOf(e - s);
        Helper.printMessage(result);

        s = new Date().getTime();
        Set<String> setStrings = getStrings(shortener, setIds);
        e = new Date().getTime();

        result = String.valueOf(e - s);
        Helper.printMessage(result);

        if (setStrings.size() == strings.size()) {
            Helper.printMessage("Тест пройден.");
        }else {
            Helper.printMessage("Тест не пройден.");
        }
    }
}
