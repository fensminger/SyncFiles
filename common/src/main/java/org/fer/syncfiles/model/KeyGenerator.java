package org.fer.syncfiles.model;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDGenerator;
import com.fasterxml.uuid.impl.RandomBasedGenerator;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by fer on 18/10/2014.
 */
public class KeyGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final RandomBasedGenerator generator = Generators.randomBasedGenerator(secureRandom);

    public synchronized static String generateUniqueId() {
        UUID uuid = generator.generate();

        return uuid.toString();
    }
}
