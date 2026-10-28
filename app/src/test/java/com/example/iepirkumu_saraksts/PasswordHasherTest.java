package com.example.iepirkumu_saraksts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {
    @Test
    public void verify_acceptsCorrectPassword() {
        assertTrue(PasswordHasher.verify("secret", PasswordHasher.hash("secret")));
    }

    @Test
    public void verify_rejectsWrongPasswordAndPlaintext() {
        String hash = PasswordHasher.hash("secret");
        assertFalse(PasswordHasher.verify("other", hash));
        assertFalse(PasswordHasher.verify("secret", "secret"));
    }

    @Test
    public void hash_isSalted() {
        assertNotEquals(PasswordHasher.hash("secret"), PasswordHasher.hash("secret"));
    }
}
