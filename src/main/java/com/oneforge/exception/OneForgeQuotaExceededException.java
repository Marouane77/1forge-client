package com.oneforge.exception;

public class OneForgeQuotaExceededException extends OneForgeException {
   private static final long serialVersionUID = -8921232565036926290L;

   public OneForgeQuotaExceededException(String message) {
      super(message);
   }

}
