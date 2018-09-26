package com.oneforge.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oneforge.exception.OneForgeException;
import com.oneforge.model.Conversion;
import com.oneforge.model.PairQuote;
import com.oneforge.model.Quota;
import com.oneforge.util.Constants;
import com.oneforge.util.RestClientUtil;

/**
 * Simple 1Forge Client providing methods to query the 1Forge server for
 * the five available endpoints, "/quotes", "/convert", "/market_status",
 * "/quota" and "symbols".
 * 
 * All methods can throw a OneForgeException for cases:
 * - API_KEY is invalid
 * - Timeout, server down or oddly when querying with an invalid currency, the server hangs
 */
public class Simple1ForgeClient {

   private static final String EXCEPTION_TEXT = "An error occured while requesting from 1Forge server.";
   private final String apiKey;
   Simple1ForgeClient(String apiKey) {
      this.apiKey = apiKey;
   }

   public List<PairQuote> getQuotes(String ...pairs) {
      if (pairs == null || pairs.length == 0)
         return new ArrayList<>();
      try {
         final WebTarget target = RestClientUtil.getTarget()
               .path(Constants.QUOTES_ENDPOINT)
               .queryParam("api_key", apiKey)
               .queryParam("pairs", String.join(",", pairs));
         return RestClientUtil.getJson(target, new TypeReference<List<PairQuote>>() {});
      } catch (OneForgeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OneForgeException(EXCEPTION_TEXT, ex);
      }
   }

   public Conversion convert(String from, String to, BigDecimal amount) {
      try {
         final WebTarget taget = RestClientUtil.getTarget()
               .path(Constants.CONVERT_ENDPOINT)
               .queryParam("api_key", apiKey)
               .queryParam("from", from)
               .queryParam("to", to)
               .queryParam("quantity", amount);
         return RestClientUtil.getJson(taget, Conversion.class);
      } catch (OneForgeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OneForgeException(EXCEPTION_TEXT, ex);
      }
   }

   public boolean isMarketOpen() {
      try {
         final WebTarget target = RestClientUtil.getTarget()
               .path(Constants.MARKET_STATUS_ENDPOINT)
               .queryParam("api_key", apiKey);
         return (Boolean) RestClientUtil.getJson(
               target, new TypeReference<Map<String, Object>>() {})
               .get("market_is_open");
      } catch (OneForgeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OneForgeException(EXCEPTION_TEXT, ex);
      }
   }

   public List<String> getSymbols() {
      try {
         final WebTarget target = RestClientUtil.getTarget()
               .path(Constants.SYMBOLS_ENDPOINT)
               .queryParam("api_key", apiKey);
         return RestClientUtil.getJson(target, new TypeReference<List<String>>() {});
      } catch (OneForgeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OneForgeException(EXCEPTION_TEXT, ex);
      }
   }

   public Quota getQuota() {
      try {
         final WebTarget target = RestClientUtil.getTarget()
               .path(Constants.QUOTA_ENDPOINT)
               .queryParam("api_key", apiKey);
         return RestClientUtil.getJson(target, Quota.class);
      } catch (OneForgeException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OneForgeException(EXCEPTION_TEXT, ex);
      }
   }
}