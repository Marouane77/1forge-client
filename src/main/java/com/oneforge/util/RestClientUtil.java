package com.oneforge.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.oneforge.exception.OneForgeException;
import com.oneforge.exception.OneForgeInvalidApiKeyException;
import com.oneforge.exception.OneForgeQuotaExceededException;

public class RestClientUtil {
   private static final Logger LOG = Logger.getLogger(RestClientUtil.class.getName());

   private static final WebTarget TARGET = ClientBuilder.newClient()
      .property(ClientProperties.CONNECT_TIMEOUT, Constants.CONNETION_TIMEOUT)
      .property(ClientProperties.READ_TIMEOUT, Constants.READ_TIMEOUT)
      .target(Constants.ONE_FORGE_URL);
   private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
   public static WebTarget getTarget() {
      return TARGET;
   }

   public static <T> T getJson(WebTarget target, Class<T> entityType) {
      final long startTime = System.currentTimeMillis();
      try {
         final JsonNode jsonNode = target.request(MediaType.APPLICATION_JSON)
               .get().readEntity(JsonNode.class);
         checkAndThrowExceptionIfError(jsonNode);
         return JSON_MAPPER.treeToValue(jsonNode, entityType);
      } catch (JsonProcessingException ex) {
         throw new OneForgeException(
               "Error occurred while parsing 1Forge response querying " + target.getUri());
      } finally {
         final long endTime = System.currentTimeMillis();
         LOG.log(Level.INFO, "Took " + (endTime - startTime) + "ms to query " + target.getUri());
      }

   }

   public static <T> T getJson(WebTarget target, TypeReference<T> typeReference) {
      final long startTime = System.currentTimeMillis();
      try {
         final JsonNode jsonNode = target.request(MediaType.APPLICATION_JSON)
               .get().readEntity(JsonNode.class);
         checkAndThrowExceptionIfError(jsonNode);
         final ObjectReader reader = JSON_MAPPER.readerFor(typeReference);
         return reader.readValue(jsonNode);
      } catch (JsonProcessingException ex) {
         throw new OneForgeException(
               "Error occurred while parsing 1Forge response querying " + target.getUri());
      } catch (IOException e) {
         throw new OneForgeException(
               "Error occurred while parsing 1Forge response querying " + target.getUri());
      } finally {
         final long endTime = System.currentTimeMillis();
         LOG.log(Level.INFO, "Took " + (endTime - startTime) + "ms to query " + target.getUri());
      }
   }

   private static void checkAndThrowExceptionIfError(JsonNode jsonResponse) {
      if (jsonResponse.has("error")) {
         if (jsonResponse.get("error").asBoolean()) {  // Do you really need this !
            final String errorMsg = jsonResponse.get("message").asText();
            if (errorMsg.startsWith(Constants.ERROR_INVALID_API_KEY_START)) {
               throw new OneForgeInvalidApiKeyException(errorMsg);
            } else if (errorMsg.startsWith(Constants.ERROR_QUOTA_EXCEEDED_START)) {
               throw new OneForgeQuotaExceededException(errorMsg);
            }
            throw new OneForgeException(errorMsg);
         }
      }
   }
}
