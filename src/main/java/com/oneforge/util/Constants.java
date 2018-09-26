package com.oneforge.util;

public class Constants {
   public static final int CONNETION_TIMEOUT = 5000;
   public static final int READ_TIMEOUT = 5000;

   public static final String ONE_FORGE_URL = "https://forex.1forge.com/1.0.3";
   public static final String QUOTES_ENDPOINT = "/quotes";
   public static final String CONVERT_ENDPOINT = "/convert";
   public static final String QUOTA_ENDPOINT = "/quota";
   public static final String MARKET_STATUS_ENDPOINT = "/market_status";
   public static final String SYMBOLS_ENDPOINT = "/symbols";
   
   /**
    * Full message : 
    * "API Key Not Valid. Please go to 1forge.com to get an API key. If you have any questions please email us at contact@1forge.com";
    */
   public static final String ERROR_INVALID_API_KEY_START = "API Key Not Valid.";
   /**
    * Full message : 
    * "Your quota has been used fully. Quotas reset at midnight EST. If you have any questions please email us at contact@1forge.com";
    */
   public static final String ERROR_QUOTA_EXCEEDED_START = "Your quota has been used fully.";
}
