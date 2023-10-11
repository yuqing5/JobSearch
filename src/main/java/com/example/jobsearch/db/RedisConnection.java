package com.example.jobsearch.db;
import redis.clients.jedis.Jedis;

public class RedisConnection {
    private static final String INSTANCE = "52.89.226.94";
    private static final int PORT = 6379; //redis default port
    private static final String PASSWORD = "12345678";
    private static final String SEARCH_KEY_TEMPLATE = "search:lat=%s&lon=%s&keyword=%s";
    private static final String FAVORITE_KEY_TEMPLATE = "history:userId=%s";

    //create and close connections to Redis
    private Jedis jedis; //Jedis, a client library in Java for Redis – the popular in-memory data structure store that can persist on disk as well

    public RedisConnection() {
        jedis = new Jedis(INSTANCE, PORT);
        jedis.auth(PASSWORD);
    }

    public void close() {
        jedis.close();
    }
    public String getSearchResult(double lat, double lon, String keyword) {
        if (jedis == null) {
            return null;
        }
        String key = String.format(SEARCH_KEY_TEMPLATE, lat, lon, keyword);
        return jedis.get(key);
    }

    public void setSearchResult(double lat, double lon, String keyword, String value) {
        if (jedis == null) {
            return;
        }
        String key = String.format(SEARCH_KEY_TEMPLATE, lat, lon, keyword);
        jedis.set(key, value);
        jedis.expire(key, 10);  //redis will fetch data from GitHub every 10s
    }

    public String getFavoriteResult(String userId) {
        if (jedis == null) {
            return null;
        }
        String key = String.format(FAVORITE_KEY_TEMPLATE, userId);
        return jedis.get(key);
    }

    public void setFavoriteResult(String userId, String value) {
        if (jedis == null) {
            return;
        }
        String key = String.format(FAVORITE_KEY_TEMPLATE, userId);
        jedis.set(key, value);
        jedis.expire(key, 10);
    }

    public void deleteFavoriteResult(String userId) {
        if (jedis == null) {
            return;
        }
        String key = String.format(FAVORITE_KEY_TEMPLATE, userId);
        jedis.del(key);
    }

        public static void main(String[] args) {
            RedisConnection connection = new RedisConnection();
            System.out.println("Get 123 before set it : " + connection.jedis.get("123"));
            connection.jedis.set("123", "456");
            System.out.println("Get 123 after set it : " + connection.jedis.get("123"));
            connection.jedis.expire("123", 10);
        }

}

