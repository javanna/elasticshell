package org.elasticsearch.shell.client;

import java.util.Set;

/**
 * @author Luca Cavanna
 */
public class Index {

    private final String name;
    private final String[] types;
    private final String[] aliases;

    public Index(String name, Set<String> types, Set<String> aliases) {
        this.name = name;
        this.types = types.toArray(new String[types.size()]);
        this.aliases = aliases.toArray(new String[aliases.size()]);
    }

    public String[] types() {
        return types;
    }

    public String name() {
        return name;
    }

    public String[] aliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return name + " : " + types;
    }
}
