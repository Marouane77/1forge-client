package com.oneforge.client;

import org.cache2k.Cache;
import org.cache2k.CacheManager;

import com.oneforge.exception.OneForgeException;

/**
 * 1Forge Client builder, allow creating
 * Simple1ForgeClient and RateCache1ForgeClient.
 * Note it's not possible to create multiple RateCache1ForgeClient using
 * the same API key. 
 */
public class OneForgeClientBuilder {

   /**
    * Create a Simple1ForgeClient client.
    * @param apiKey
    * @return The simple client
    */
   public Simple1ForgeClient buildSimpleClient(String apiKey) {
      return new Simple1ForgeClient(apiKey);
   }

   /**
    * Create a RateCache1ForgeClient client.
    * @param apiKey
    * @param targetPairs pairs symbols to be supported
    * @return A rate caching client
    */
   public RateCache1ForgeClient buildRateCacheClient(
         String apiKey, String ...targetPairs) {
      return this.buildRateCacheClient(apiKey, 0, targetPairs);
   }

   /**
    * Create a RateCache1ForgeClient client.
    * @param apiKey
    * @param maxAcceptableLag The maximum duration to keep rates before refreshing, in seconds
    * @param targetPairs pairs symbols to be supported
    * @return A rate caching client
    */
   public RateCache1ForgeClient buildRateCacheClient(
         String apiKey, long maxAcceptableLag, String ...targetPairs) {
      return this.buildRateCacheClient(apiKey, maxAcceptableLag, false, targetPairs);
   }

   /**
    * Create a RateCache1ForgeClient client.
    * @param apiKey
    * @param maxAcceptableLag The maximum duration to keep rates before refreshing, in seconds
    * @param useSmallerLagIfPossible If true and the quota remaining allows a more frequent
    *                                 rates refresh, then it will override the given maxAcceptableLag
    * @param targetPairs pairs symbols to be supported
    * @return A rate caching client
    */
   public RateCache1ForgeClient buildRateCacheClient(
         String apiKey, long maxAcceptableLag, boolean useSmallerLagIfPossible, String ...targetPairs) {
      final String cacheName = "cache_" + apiKey;
      final Cache<String, Double>ratesCache = CacheManager.getInstance().getCache(cacheName);
      if (ratesCache != null) {
         throw new OneForgeException("A cache for the same 1Forge api key '"
               + apiKey + "' already exist!");
      }
      return new RateCache1ForgeClient(apiKey, maxAcceptableLag, useSmallerLagIfPossible, targetPairs);
   }
}
