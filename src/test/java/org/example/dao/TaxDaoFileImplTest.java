package org.example.dao;

import org.example.model.Tax;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaxDaoFileImplTest {

    private TaxDao dao;

    @BeforeAll
    void setup() {
        dao = new TaxDaoFileImpl();
    }

    @Test
    void testGetAllTaxes() throws FlooringException {
        List<Tax> taxes = dao.getAllTaxes();
        assertNotNull(taxes, "Tax list should not be null");
        assertTrue(taxes.size() >= 3, "There should be multiple tax entries");

        assertTrue(taxes.stream().anyMatch(t -> t.getStateAbbreviation().equals("TX")));
        assertTrue(taxes.stream().anyMatch(t -> t.getStateAbbreviation().equals("CA")));
    }

    @Test
    void testGetTaxByState() throws FlooringException {
        Tax tx = dao.getTax("TX");
        assertNotNull(tx, "Texas should exist");
        assertEquals("TX", tx.getStateAbbreviation());
        assertTrue(tx.getTaxRate().doubleValue() > 0);
    }

    @Test
    void testInvalidStateReturnsNull() throws FlooringException {
        Tax invalid = dao.getTax("NJ");
        assertNull(invalid, "New Jersey shouldn't exist in the dataset");
    }
}
