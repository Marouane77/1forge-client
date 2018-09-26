package com.oneforge.client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import com.oneforge.exception.OneForgeException;
import com.oneforge.model.PairQuote;
import com.oneforge.model.Quota;

/**
 * RateCache1ForgeClient provide a method to get rates for a targeted list of
 * currencies, making use of the "/quotes" endpoint which accept a list of pairs
 * symbols as parameters. Also provide a possibility to cache result for a
 * specified duration.
 */
public class RateCache1ForgeClient {
   private static final Logger LOG = Logger
         .getLogger(RateCache1ForgeClient.class.getName());
   private Cache<String, Double> ratesCache;

   private final String apiKey;
   private final String[] targetPairs;
   private final Simple1ForgeClient forgeClient;
   private long maxAcceptableLag;

   RateCache1ForgeClient(String apiKey, long maxAcceptableLag,
         boolean useSmallerLagIfPossible, String... targetPairs) {
      this.apiKey = apiKey;
      this.maxAcceptableLag = Math.max(maxAcceptableLag, 0);
      this.targetPairs = targetPairs;
      this.forgeClient = new Simple1ForgeClient(apiKey);
      this.computeQuota(useSmallerLagIfPossible);
      this.initializeCache();
   }

   private void initializeCache() {
      final String cacheName = "cache_" + this.apiKey;
      this.ratesCache = new Cache2kBuilder<String, Double>() {
      }.name(cacheName).loader(this::loadQuotes)
            .expireAfterWrite(this.maxAcceptableLag, TimeUnit.SECONDS).build();
   }

   private void computeQuota(boolean useSmallerLagIfPossible) {
      final Quota initialQuota = this.forgeClient.getQuota();

      if (useSmallerLagIfPossible || this.maxAcceptableLag == 0) {
         final LocalDateTime now = LocalDateTime.now();
         // final long millisecond = now.get(ChronoField.MILLI_OF_SECOND);
         final int seconds = now.get(ChronoField.SECOND_OF_MINUTE);
         final int minutes = now.get(ChronoField.MINUTE_OF_HOUR);
         final int hours = initialQuota.getHoursUntilReset() - 1;
         final int remainingSecondsBeforeReset = hours * 60 * 60 + minutes * 60
               + seconds;
         final float possibleQueryPerSecond = (float) initialQuota
               .getQuotaRemaining() / remainingSecondsBeforeReset;
         if (this.maxAcceptableLag == 0) {
            this.maxAcceptableLag = (int) (1 / possibleQueryPerSecond);
         } else {
            final float chosenQueryPerSecond = 1f / this.maxAcceptableLag;
            if (possibleQueryPerSecond > chosenQueryPerSecond) {
               this.maxAcceptableLag = (int) (1 / possibleQueryPerSecond);
            }
         }
      }
      LOG.log(Level.INFO,
            "Refresh every " + this.maxAcceptableLag + " seconds");
   }

   /**
    * Used to ensure the refresh quotes(Request to 1Forge serve) is not executed
    * concurrently. For more info see:
    * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html
    **/
   private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private final Lock read = readWriteLock.readLock();
   private final Lock write = readWriteLock.writeLock();

   /**
    * Invoked when the quotes need to be refreshed.
    * @param pairSymbols
    * @return
    */
   private Double loadQuotes(String pairSymbols) {
      try {
         // Release the read lock before acquiring the write lock
         read.unlock();
         write.lock();
         final List<PairQuote> quotes = this.forgeClient
               .getQuotes(this.targetPairs);

         final PairQuote targetedPairQuote = new PairQuote();
         quotes.stream().forEach(pairQuote -> {
            if (pairQuote.getPairSymbols().equals(pairSymbols)) {
               targetedPairQuote.setPrice(pairQuote.getPrice());
            } else {
               this.ratesCache.put(pairQuote.getPairSymbols(),
                     pairQuote.getPrice().doubleValue());
            }
         });
         return targetedPairQuote.getPrice().doubleValue();
      } finally {
         read.lock();
         write.unlock();
      }
   }

   public Double getRate(String from, String to) {
      final String pairSymbols = from + to;
      if (!Arrays.stream(this.targetPairs).filter(p -> p.equals(pairSymbols))
            .findAny().isPresent()) {
         throw new OneForgeException(
               "This Cache1ForgeClient is not configured to support the "
                     + pairSymbols + " rate conversion.");
      }
      // Lock on read, in case a write is in progress, wait then
      // get the entry from cache to avoid querying 1Forge twice
      try {
         read.lock();
         return this.ratesCache.get(pairSymbols);
      } finally {
         read.unlock();
      }
   }

   public void close() {
      if (this.ratesCache != null) {
         this.ratesCache.close();
      }
   }
}