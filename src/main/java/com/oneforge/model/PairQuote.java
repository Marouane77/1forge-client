package com.oneforge.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PairQuote {
   private String pairSymbols;
   private BigDecimal price;
   private BigDecimal bid;
   private BigDecimal ask;
   private Date timestamp;
   @JsonProperty("symbol")
   public String getPairSymbols() {
      return pairSymbols;
   }
   public void setPairSymbols(String pairSymbols) {
      this.pairSymbols = pairSymbols;
   }
   public BigDecimal getPrice() {
      return price;
   }
   public void setPrice(BigDecimal price) {
      this.price = price;
   }
   public BigDecimal getBid() {
      return bid;
   }
   public void setBid(BigDecimal bid) {
      this.bid = bid;
   }
   public BigDecimal getAsk() {
      return ask;
   }
   public void setAsk(BigDecimal ask) {
      this.ask = ask;
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
      return "pairSmbols : " + pairSymbols
            + " | price : " + price
            + " | bid : " + bid
            + " | ask : " + ask
            + " | time : " + timestamp;
   }
}
