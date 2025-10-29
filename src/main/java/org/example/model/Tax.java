package org.example.model;

import java.math.BigDecimal;

public class Tax {
    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;

    // Getters and Setters
    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public String toString() {
        return stateName + " (" + stateAbbreviation + ") - " + taxRate + "%";
    }
}
