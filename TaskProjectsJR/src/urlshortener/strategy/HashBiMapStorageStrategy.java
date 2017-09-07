package urlshortener.strategy;

import com.google.common.collect.HashBiMap;

public class HashBiMapStorageStrategy implements StorageStrategy {
    private HashBiMap data;

    public HashBiMapStorageStrategy() {

        data = HashBiMap.create();

    }

    @Override
    public boolean containsKey(Long key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
        return data.containsValue(value);
    }

    @Override
    public void put(Long key, String value) {
        data.put(key, value);
    }

    @Override
    public Long getKey(String value) {

        return (Long) data.inverse().get(value);

    }

    @Override
    public String getValue(Long key) {
        return (String) data.get(key);
    }
}
