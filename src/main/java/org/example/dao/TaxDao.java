package org.example.dao;

import org.example.model.Tax;

import java.util.List;

public interface TaxDao {
    List<Tax> getAllTaxes() throws FlooringException;
    Tax getTax(String stateAbbr) throws FlooringException;
}
