package com.example.iepirkumu_saraksts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AddProductActivityTest {
    @Test
    public void icon_matchesKeyword() {
        assertEquals("🥛", ProductIcons.forProduct(new Product("Piens", "1", "Pārtika")));
    }

    @Test
    public void category_guessedForKnownProducts() {
        assertEquals("Pārtika", ProductIcons.categoryFor("Maize"));
        assertEquals("Dzērieni", ProductIcons.categoryFor("kafija"));
        assertEquals("Sadzīve", ProductIcons.categoryFor("Tualetes papīrs"));
        assertEquals("Dzērieni", ProductIcons.categoryFor("Coca-Cola"));
        assertEquals("Dzērieni", ProductIcons.categoryFor("udens"));
        assertEquals("Pārtika", ProductIcons.categoryFor("abols"));
        assertNull(ProductIcons.categoryFor("Xyz"));
    }

    @Test
    public void icon_fallsBackToCategory() {
        assertEquals("🥤", ProductIcons.forProduct(new Product("Xyz", "1", "Dzērieni")));
    }
}
