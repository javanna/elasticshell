package org.elasticsearch.shell.client;

import java.util.Set;

/**
 * @author Luca Cavanna
 */
public class Index {

    private final String name;
    private final Set<String> types;
    private final Set<String> aliases;

    public Index(String name, Set<String> types, Set<String> aliases) {
        this.name = name;
        this.types = types;
        this.aliases = aliases;
    }

    public Set<String> types() {
        return types;
    }

    public String name() {
        return name;
    }

    public Set<String> aliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return name + " : " + types;
    }
}
