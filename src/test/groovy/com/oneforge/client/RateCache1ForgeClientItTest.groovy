package com.oneforge.client

import static com.oneforge.util.Constants.ERROR_INVALID_API_KEY_START

import org.cache2k.integration.CacheLoaderException;

import com.oneforge.BaseTest
import com.oneforge.client.RateCache1ForgeClient
import com.oneforge.client.Simple1ForgeClient
import com.oneforge.exception.OneForgeException

import spock.lang.Ignore


public class RateCache1ForgeClientItTest extends BaseTest {

   private static final long ALLOWED_LAG_IN_SECOND = 3;

   def """Given invalid API_KEY
        When invoking the getRate
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      when: "Creating a rateCache1ForgeClient instance"
      def dummyRateCache1ForgeClient = new OneForgeClientBuilder()
            .buildRateCacheClient(dummyApiKey, ALLOWED_LAG_IN_SECOND, "EURUSD")
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given a non supported pair symbols
        When invoking the getRate
        Then Exception should be thrown"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: 'An instance of RateCache1ForgeClient'
      def rateCache1ForgeClient = new OneForgeClientBuilder()
            .buildRateCacheClient(TEST_API_KEY, ALLOWED_LAG_IN_SECOND, false, "EURUSD");
      and: "Wrong pair Symbols"
      def from = "EUR"
      def to = "JPY"
      when: "When invoking the getRate method"
      rateCache1ForgeClient.getRate(from, to)
      then:
      OneForgeException ex = thrown()
      ex.message == 'This Cache1ForgeClient is not configured to support the ' + from + to + ' rate conversion.'
      cleanup:
      if (rateCache1ForgeClient)
         rateCache1ForgeClient.close()
   }

   def """Given valid API_KEY and from to
        When invoking the getRate
        Then get valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: 'An instance of RateCache1ForgeClient'
      def rateCache1ForgeClient = new OneForgeClientBuilder()
            .buildRateCacheClient(TEST_API_KEY, ALLOWED_LAG_IN_SECOND, false, "EURUSD");
      and: "A valid from to"
      def from = "EUR"
      def to = "USD"
      when: "When invoking the getRate method"
      def result = rateCache1ForgeClient.getRate(from, to)
      then:
      result >= 0
      cleanup:
      if (rateCache1ForgeClient)
         rateCache1ForgeClient.close()
   }

   def """Given valid API_KEY and from to
        When invoking the getRate successively
        Then should not see the quota goes down"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: 'An instance of RateCache1ForgeClient'
      def rateCache1ForgeClient = new OneForgeClientBuilder()
            .buildRateCacheClient(TEST_API_KEY, ALLOWED_LAG_IN_SECOND, false, "EURUSD");
      and: "A valid from to"
      def from = "EUR"
      def to = "USD"
      and: "A Simple1ForgeClient instance"
      def simple1ForgeClient = new Simple1ForgeClient(TEST_API_KEY)
      and: "Sleep for $ALLOWED_LAG_IN_SECOND ms to make sure next call will make a request"
      Thread.currentThread().sleep(ALLOWED_LAG_IN_SECOND * 1000l);
      and:
      def quotaBefore = simple1ForgeClient.getQuota()
      when: "When invoking the getRate method"
      1.upto(50) {
         rateCache1ForgeClient.getRate(from, to)
      }
      and:
      def quotaAfter = simple1ForgeClient.getQuota()
      then: "Used Quota should remain same"
      quotaAfter.quotaUsed == quotaBefore.quotaUsed
      and: "Remaining Quota should remain same"
      quotaAfter.quotaRemaining == quotaBefore.quotaRemaining
      cleanup:
      if (rateCache1ForgeClient)
         rateCache1ForgeClient.close()
   }

   def """Given valid API_KEY and from to
        When invoking the getRate successively
        And max allowed delay is exceeded
        Then should trigger new request"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: 'An instance of RateCache1ForgeClient'
      def rateCache1ForgeClient = new OneForgeClientBuilder()
            .buildRateCacheClient(TEST_API_KEY, ALLOWED_LAG_IN_SECOND, false, "EURUSD");
      and: "A valid from to"
      def from = "EUR"
      def to = "USD"
      and: "A Simple1ForgeClient instance"
      def simple1ForgeClient = new Simple1ForgeClient(TEST_API_KEY)
      and: "Sleep for $ALLOWED_LAG_IN_SECOND ms to make sure next call will make a request"
      Thread.currentThread().sleep(ALLOWED_LAG_IN_SECOND * 1000l);
      and:
      def quotaBefore = simple1ForgeClient.getQuota()
      when: "When invoking the getRate method"
      rateCache1ForgeClient.getRate(from, to)
      and: "Sleep for $ALLOWED_LAG_IN_SECOND ms to make sure next call will make a request"
      Thread.currentThread().sleep(ALLOWED_LAG_IN_SECOND * 1000l);
      and: "A second invocation to the getRate method after 5 second"
      rateCache1ForgeClient.getRate(from, to)
      and:
      def quotaAfter = simple1ForgeClient.getQuota()
      then: "Used Quota should have gone up"
      quotaAfter.quotaUsed - 1 == quotaBefore.quotaUsed
      and: "Remaining Quota should have gone down"
      quotaAfter.quotaRemaining + 1 == quotaBefore.quotaRemaining
      cleanup:
      if (rateCache1ForgeClient)
         rateCache1ForgeClient.close()
   }
}
