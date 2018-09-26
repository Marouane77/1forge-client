package com.oneforge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quota {
   private int quotaUsed;
   private int quotaLimit;
   private int quotaRemaining;
   private int hoursUntilReset;

   public Quota() {}

   public Quota(Quota sourceQuota) {
      this.quotaUsed = sourceQuota.quotaUsed;
      this.quotaLimit = sourceQuota.quotaLimit;
      this.quotaRemaining = sourceQuota.quotaRemaining;
      this.hoursUntilReset = sourceQuota.hoursUntilReset;
   }

   @JsonProperty("quota_used")
   public int getQuotaUsed() {
      return quotaUsed;
   }
   public void setQuotaUsed(int quotaUsed) {
      this.quotaUsed = quotaUsed;
   }
   @JsonProperty("quota_limit")
   public int getQuotaLimit() {
      return quotaLimit;
   }
   public void setQuotaLimit(int quotaLimit) {
      this.quotaLimit = quotaLimit;
   }
   @JsonProperty("quota_remaining")
   public int getQuotaRemaining() {
      return quotaRemaining;
   }
   public void setQuotaRemaining(int quotaRemaining) {
      this.quotaRemaining = quotaRemaining;
   }
   @JsonProperty("hours_until_reset")
   public int getHoursUntilReset() {
      return hoursUntilReset;
   }
   public void setHoursUntilReset(int hoursUntilReset) {
      this.hoursUntilReset = hoursUntilReset;
   }

   @Override
   public String toString() {
      return "quotaUsed : " + quotaUsed
            + " | quotaLimit : " + quotaLimit
            + " | quotaRemaining : " + quotaRemaining
            + " | hoursUntilReset : " + hoursUntilReset;
   }
}
