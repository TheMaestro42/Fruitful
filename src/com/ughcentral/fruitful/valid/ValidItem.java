package com.ughcentral.fruitful.valid;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.ughcentral.fruitful.Keyword;

public class ValidItem extends ValidDrop {
    
    private final Material material;
    private final Integer[] data;
    private static final Integer[] ZERO = { 0 };
    
    public ValidItem(final String name, final HashSet<Keyword> keywords, final Material material) {
        this(name, keywords, material, ZERO);
    }
    
    public ValidItem(final String name, final HashSet<Keyword> keywords, final Material material, final Integer[] data) {
        super(name, keywords);
        this.material = material;
        this.data = data;
    }
    
    public ItemStack getItemStack(final int number) {
        if (data.length == 1) {
            return new ItemStack(material, number, (short) (int) data[0]);
        }
        final Random random = new Random();
        return new ItemStack(material, number, (short) (int) data[random.nextInt(data.length)]);
    }
    
    @Override
    public DropType dropType() {
        return DropType.ITEM;
    }
    
}
