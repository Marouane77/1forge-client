package com.oneforge

import spock.lang.Shared
import spock.lang.Specification

abstract class BaseTest extends Specification {
   
   public static final String TEST_API_KEY
   static { 
      Properties props = new Properties();
      props.load(Thread.currentThread().getContextClassLoader()
         .getResourceAsStream('config.properties'));
      TEST_API_KEY = props.getProperty('1forge.test.api.key');     
      println 'Testing API key : ' + TEST_API_KEY
   }
}