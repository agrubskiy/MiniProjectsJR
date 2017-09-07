package urlshortener.tests;

import urlshortener.Helper;
import urlshortener.Shortener;
import urlshortener.strategy.HashBiMapStorageStrategy;
import urlshortener.strategy.HashMapStorageStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SpeedTest {
    public long getTimeForGettingIds(Shortener shortener, Set<String> strings, Set<Long> ids) {


        long s = new Date().getTime();
        for (String string : strings) {
            ids.add(shortener.getId(string));
        }
        long e = new Date().getTime();

        return e - s;

    }

    public long getTimeForGettingStrings(Shortener shortener, Set<Long> ids, Set<String> strings) {

        long s = new Date().getTime();
        for (Long l : ids) {
            strings.add(shortener.getString(l));
        }
        long e = new Date().getTime();

        return e - s;
    }

    @Test
    public void testHashMapStorage() {

        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            origStrings.add(Helper.generateRandomString());
        }

        Set<Long> origLong = new HashSet<>();
        long l1 = getTimeForGettingIds(shortener1, origStrings, origLong);
        origLong.clear();
        long l2 = getTimeForGettingIds(shortener2, origStrings, origLong);
        Assert.assertTrue(l1 > l2);

        Set<String> stringSet = new HashSet<>();
        l1 = getTimeForGettingStrings(shortener1, origLong, stringSet);
        stringSet.clear();
        l2 = getTimeForGettingStrings(shortener2, origLong, origStrings);
        Assert.assertEquals(l1, l2, 30);

    }

}
