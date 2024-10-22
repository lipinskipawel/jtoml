package org.example.parser;

import static java.util.Objects.requireNonNull;

public record KeyValuePair<V>(String key, V value) implements Node {

    public KeyValuePair {
        requireNonNull(key);
        requireNonNull(value);
    }
}
