# 1Forge-Client

This project is a client for the 1Forge Quote API.
It provides two client classes:
* Simple1ForgeClient
* RateCache1ForgeClient

## Simple1ForgeClient
Is a simple straightforward client that provides methods to invoke all 1Forge endpoints, it can be done as below:
```
Simple1ForgeClient client = new OneForgeClientBuilder().buildSimpleClient(yourApiKey);
List<String> symbols = client.getSymbols();
boolean marketOpen = client.isMarketOpen();
Quota quota = client.getQuota();
List<Quote> quotes = client.getQuotes("JPYEUR", "USDJPY" /* ... */);
Conversion conversion = client.convert("JPY", "EUR", 30000);
```

## RateCache1ForgeClient
Beside your API key, to use this client you need to provide a list of pairs Symbols to be supported(ex: "EURUSD").
Two other optional parameters are available:
- **maxAcceptableLag** : the maximum time for cached quotes to be considered valid before it need to refreshed, if not specified, then the client after a call to the "/quota" endpoint try to determine the best value for this parameter based on the remaining quota and remaining time till next reset
- **useSmallerLagIfPossible** : default to false, if specified then even if the "maxAcceptableLag" is given, the system will to see if it can use a smaller lag based on the same mechanism explained previously.
Only one method is available, to get rate from a currency to another, it can be used as below:
```
RateCache1ForgeClient client = new OneForgeClientBuilder()
   .buildRateCacheClient(yourApiKey, 60 * 5, false, "EURJPY", "EURUSD" /* ... */);

Double destRate1 = client.getRate("EUR", "JPY");   // Request made to 1Forge
Double destRate2 = client.getRate("EUR", "USD");   // No request made as the quotes
                                                      have already fetched and cached
// After 5 minutes
Double destRate3 = client.getRate("EUR", "USD");   // Request made to 1Forge and cache refreshed
```

## Dependencies
To include this library in your project you need to include:
- jersey-client
- jackson
- cache2k

## Installation
The build.gradle has been adapted so if you run execute below command, the jar will be installed in your local maven repository, so you can later on add it as dependency to your projects.

`gradle test install`

## Tests
Spock and Groovy have been used for the tests, please expect some tests to fail if you don't provide your 1Forge API key(test/groovy/resources/config.properties)

## References
https://1forge.com/forex-data-api/api-documentation