package urlshortener.tests;

import urlshortener.Helper;
import urlshortener.Shortener;
import urlshortener.strategy.*;
import org.junit.Assert;
import org.junit.Test;

public class FunctionalTest {
    public void testStorage(Shortener shortener) {

        String s1 = Helper.generateRandomString();
        String s2 = Helper.generateRandomString();
        String s3 = s1;

        long id1 = shortener.getId(s1);
        long id2 = shortener.getId(s2);
        long id3 = shortener.getId(s3);

        Assert.assertNotEquals(id2, id1);
        Assert.assertNotEquals(id2, id3);
        Assert.assertEquals(id1, id3);

        String s4 = shortener.getString(id1);
        String s5 = shortener.getString(id2);
        String s6 = shortener.getString(id3);

        Assert.assertEquals(s4, s1);
        Assert.assertEquals(s5, s2);
        Assert.assertEquals(s6, s3);
    }

    @Test
    public void testHashMapStorageStrategy() {

        HashMapStorageStrategy strategy = new HashMapStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);

    }

    @Test
    public void testOurHashMapStorageStrategy() {
        OurHashMapStorageStrategy strategy = new OurHashMapStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);
    }

    @Test
    public void testFileStorageStrategy() {
        FileStorageStrategy strategy = new FileStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);
    }

    @Test
    public void testHashBiMapStorageStrategy() {
        HashBiMapStorageStrategy strategy = new HashBiMapStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);
    }

    @Test
    public void testDualHashBidiMapStorageStrategy() {
        DualHashBidiMapStorageStrategy strategy = new DualHashBidiMapStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);
    }

    @Test
    public void testOurHashBiMapStorageStrategy() {
        OurHashBiMapStorageStrategy strategy = new OurHashBiMapStorageStrategy();
        Shortener shortener = new Shortener(strategy);
        testStorage(shortener);
    }
}
