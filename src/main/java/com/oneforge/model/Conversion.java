package com.oneforge.model;

import java.math.BigDecimal;
import java.util.Date;

public class Conversion {
   private BigDecimal value;
   private String text;
   private Date timestamp;
   public BigDecimal getValue() {
      return value;
   }
   public void setValue(BigDecimal value) {
      this.value = value;
   }
   public String getText() {
      return text;
   }
   public void setText(String text) {
      this.text = text;
   }
   public Date getTimestamp() {
      return timestamp;
   }
   public void setTimestamp(Date timestamp) {
      this.timestamp = timestamp;
   }
   public void setTimestamp(long timestamp) {
      this.timestamp = new Date(timestamp);
   }

   @Override
   public String toString() {
      return "Value : " + value
            + " | text : " + text
            + " | time : " + timestamp;
   }
}
