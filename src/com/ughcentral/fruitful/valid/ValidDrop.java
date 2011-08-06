package com.ughcentral.fruitful.valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.ughcentral.fruitful.Keyword;

public abstract class ValidDrop {
    
    public static final HashMap<String, ValidDrop> dropList = new HashMap<String, ValidDrop>();
    public final HashSet<Keyword> keywords;
    private final String name;
    
    public ValidDrop(final String name, final HashSet<Keyword> keywords) {
        this.name = name;
        this.keywords = keywords;
        register(name, this);
    }
    
    public static void unRegister(final String name) {
        dropList.remove(name);
    }
    
    private static void register(final String name, final ValidDrop drop) {
        dropList.put(name, drop);
    }
    
    public static ValidDrop getByName(final String name) {
        if (name.equalsIgnoreCase("keywords")) {
            return null;
        }
        return dropList.get(name);
    }
    
    public static void unRegisterAll() {
        final Set<String> keySet = dropList.keySet();
        final Set<String> keys = new HashSet<String>();
        keys.addAll(keySet);
        for (final String key : keys) {
            unRegister(key);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public abstract DropType dropType();
    
    public boolean hasKeyword(final Keyword keyword) {
        return keywords.contains(keyword);
    }
    
}
