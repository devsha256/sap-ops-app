package io.github.devsha256.saprfctest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for transfer eligibility check
 */
public class TransferRequest {

    @JsonProperty("fromEsiid")
    private String fromEsiid;

    @JsonProperty("kyp")
    private String kyp;

    @JsonProperty("mviDate")
    private String mviDate;

    @JsonProperty("partner")
    private String partner;

    @JsonProperty("toEsiid")
    private String toEsiid;

    @JsonProperty("oldProduct")
    private String oldProduct;

    // Getters and Setters
    public String getFromEsiid() {
        return fromEsiid;
    }

    public void setFromEsiid(String fromEsiid) {
        this.fromEsiid = fromEsiid;
    }

    public String getKyp() {
        return kyp;
    }

    public void setKyp(String kyp) {
        this.kyp = kyp;
    }

    public String getMviDate() {
        return mviDate;
    }

    public void setMviDate(String mviDate) {
        this.mviDate = mviDate;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getToEsiid() {
        return toEsiid;
    }

    public void setToEsiid(String toEsiid) {
        this.toEsiid = toEsiid;
    }

    public String getOldProduct() {
        return oldProduct;
    }

    public void setOldProduct(String oldProduct) {
        this.oldProduct = oldProduct;
    }
}
