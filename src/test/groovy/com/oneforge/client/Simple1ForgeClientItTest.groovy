package com.oneforge.client

import static com.oneforge.util.Constants.ERROR_INVALID_API_KEY_START

import com.oneforge.BaseTest
import com.oneforge.exception.OneForgeException

import org.junit.Assume

public class Simple1ForgeClientItTest extends BaseTest {

   def """Given invalid API_KEY
        When invoking the getSymbols
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(dummyApiKey)
      when: "When invoking the getSymbols method"
      simple1ForgeClient.getSymbols()
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given invalid API_KEY
        When invoking the getQuota
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(dummyApiKey)
      when: "When invoking the getQuota method"
      simple1ForgeClient.getQuota()
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given invalid API_KEY
        When invoking isMarketOpen
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(dummyApiKey)
      when: "When invoking the isMarketOpen method"
      simple1ForgeClient.isMarketOpen()
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given invalid API_KEY
        When invoking getQuotes
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(dummyApiKey)
      when: "When invoking the getQuotes method"
      simple1ForgeClient.getQuotes("EURUSD")
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given invalid API_KEY
        When invoking convert
        Then Exception should be thrown"""() {
      given: "A random dummy Api Key"
      def dummyApiKey = 'WrongApiKey'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(dummyApiKey)
      when: "When invoking the convert method"
      simple1ForgeClient.convert("EUR", "USD", BigDecimal.valueOf(1))
      then:
      OneForgeException ex = thrown()
      ex.message.startsWith(ERROR_INVALID_API_KEY_START)
   }

   def """Given invalid pair symbols
        When invoking convert
        Then Exception should be thrown"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "An incorrect pair of Symbols"
      def from = 'EUUR'
      def to = 'USD'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the convert method"
      simple1ForgeClient.convert(from, to, BigDecimal.valueOf(1))
      then:
      OneForgeException ex = thrown()
      ex.message == "An error occured while requesting from 1Forge server."
   }


   def """Given valid API_KEY
        When invoking the getSymbols
        Then get valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "A simple1ForgeClient instance"

      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the getSymbols method"
      def result = simple1ForgeClient.getSymbols()
      then:
      result.size() >= 0
      result.contains('JPYEUR')
   }

   def """Given valid API_KEY
        When invoking the getQuota
        Then get valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the getQuota method"
      def result = simple1ForgeClient.getQuota()
      then:
      result.quotaUsed >= 0
      result.quotaLimit == 1000
      result.quotaRemaining >= 0
      result.hoursUntilReset >= 0
   }

   def """Given valid API_KEY
        When invoking isMarketOpen
        Then get valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the isMarketOpen method"
      def marketIsOpen = simple1ForgeClient.isMarketOpen()
      then:
      marketIsOpen || !marketIsOpen
   }

   def """Given valid API_KEY
        When invoking getQuotes
        Then get valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the getQuotes method"
      def result = simple1ForgeClient.getQuotes("EURUSD")
      then:
      with (result[0]) {
         pairSymbols == 'EURUSD'
         bid >= 0
         ask >= 0
         price >= 0
         timestamp.before(new Date())
      }
   }

   def """Given valid API_KEY
        When invoking convert
        Then get a valid result"""() {
      given : "API key is provided"
      assert !TEST_API_KEY.trim().empty : 'Please set your API key in test/groovy/resource/config.properties'
      and: "A simple1ForgeClient instance"
      def simple1ForgeClient = new OneForgeClientBuilder().buildSimpleClient(TEST_API_KEY)
      when: "When invoking the convert method"
      def result = simple1ForgeClient.convert("EUR", "USD", BigDecimal.valueOf(27))
      then:
      result.value >= 0
      result.text == '27 EUR is worth ' + result.value + ' USD'
      result.timestamp.before(new Date())
   }
}
