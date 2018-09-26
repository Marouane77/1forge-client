package com.oneforge.exception;

public class OneForgeException extends RuntimeException {
   private static final long serialVersionUID = -1936830487471907948L;

   public OneForgeException(String message) {
      super(message);
   }

   public OneForgeException(String message, Throwable exception) {
      super(message, exception);
   }
}
