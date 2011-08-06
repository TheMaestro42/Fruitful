package com.ughcentral.fruitful.valid;

import java.util.HashSet;

import org.bukkit.entity.CreatureType;

import com.ughcentral.fruitful.Keyword;

public class ValidCreature extends ValidDrop {
    
    private final CreatureType creature;
    
    public ValidCreature(final String name, final HashSet<Keyword> keywords, final CreatureType creature) {
        super(name, keywords);
        this.creature = creature;
    }
    
    public CreatureType getCreature() {
        return creature;
    }
    
    @Override
    public DropType dropType() {
        return DropType.CREATURE;
    }
    
}
