package com.ughcentral.fruitful.valid;

import java.util.HashSet;
import com.ughcentral.fruitful.Keyword;

public class ValidGroup extends ValidDrop {
    
    private HashSet<ValidDrop> drops = new HashSet<ValidDrop>();
    
    public ValidGroup(final String name, final HashSet<Keyword> keywords, final HashSet<ValidDrop> drops) {
        super(name, keywords);
        this.drops = drops;
    }
    
    public HashSet<ValidDrop> getDrops() {
        return drops;
    }
    
    @Override
    public DropType dropType() {
        return DropType.GROUP;
    }
    
}
