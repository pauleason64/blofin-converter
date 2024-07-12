package com.peason.krakenhandler.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KrakenResult {

    @JsonProperty("error")
    private String[] errors;


    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }



}
