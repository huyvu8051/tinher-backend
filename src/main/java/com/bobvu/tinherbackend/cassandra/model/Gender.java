package com.bobvu.tinherbackend.cassandra.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Gender {
    MALE, FEMALE;

    private static final List<Gender> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Gender getRandom() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}