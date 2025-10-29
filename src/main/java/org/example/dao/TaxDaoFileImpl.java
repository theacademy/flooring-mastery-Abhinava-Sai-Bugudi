package org.example.dao;

import org.example.model.Tax;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
@Repository
public class TaxDaoFileImpl implements TaxDao {
    private final String TAX_FILE = "src/main/resources/Data/Taxes.txt";
    private final String DELIMITER = ",";
    private Map<String, Tax> taxes = new HashMap<>();

    @Override
    public List<Tax> getAllTaxes() throws FlooringException {
        loadTaxes();
        return new ArrayList<>(taxes.values());
    }

    @Override
    public Tax getTax(String stateAbbr) throws FlooringException {
        if (stateAbbr == null || stateAbbr.isBlank()) return null;
        loadTaxes();
        return taxes.get(stateAbbr.trim().toLowerCase()); // Case Insensitive and whitespace resistant
    }

    private void loadTaxes() throws FlooringException {
        taxes.clear();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(TAX_FILE)))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // skipping header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(DELIMITER);
                if (tokens.length == 3) {
                    Tax tax = new Tax();
                    tax.setStateAbbreviation(tokens[0].trim());
                    tax.setStateName(tokens[1].trim());
                    tax.setTaxRate(new BigDecimal(tokens[2].trim()));
                    taxes.put(tax.getStateAbbreviation().toLowerCase(), tax); // storing lowercase
                }
            }
        } catch (IOException e) {
            throw new FlooringException("Could not load taxes file.", e);
        }
    }

}
