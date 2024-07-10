package com.peason.krakenhandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

public class KrakenResult {

    @JsonProperty("error")
    private String[] errors;

    @JsonProperty("count")
    private int count;


    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
