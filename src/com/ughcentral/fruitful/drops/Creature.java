package com.ughcentral.fruitful.drops;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;

import com.ughcentral.fruitful.Keyword;
import com.ughcentral.fruitful.valid.ValidCreature;

public class Creature extends Drop {
    
    private final ValidCreature creature;
    private boolean isAngry = false;
    
    public Creature(final ValidCreature creature, final int[] range, final double chance, final HashSet<Keyword> keywords) {
        super(creature.getName(), range, chance, keywords);
        this.creature = creature;
        if (super.hasKeyword(Keyword.ANGRY)) {
            isAngry = true;
        }
    }
    
    private static void goForTheEyes(final org.bukkit.entity.Creature attacker) {
        for (final Entity entity : attacker.getNearbyEntities(64, 32, 64)) {
            if (entity instanceof Player) {
                attacker.setTarget((LivingEntity) entity);
                break;
            }
        }
        
    }
    
    org.bukkit.entity.Creature spawnIt(final World world, final Location location) {
        final org.bukkit.entity.Creature spawnedCreature = (org.bukkit.entity.Creature) world.spawnCreature(location, creature.getCreature());
        if (super.hasKeyword(Keyword.BURNING)) {
            spawnedCreature.setFireTicks(2000);
        }
        switch (creature.getCreature()) {
        case SHEEP:
            final Sheep sheep = (Sheep) spawnedCreature;
            if (super.hasKeyword(Keyword.SHEARED)) {
                sheep.setSheared(true);
            }
            if (super.hasKeyword(Keyword.RAINBOW)) {
                sheep.setColor(DyeColor.getByData((byte) new Random().nextInt(16)));
            } else {
                sheep.setColor(SheepColor.getColor());
            }
            return sheep;
        case CREEPER:
            final Creeper creeper = (Creeper) spawnedCreature;
            if (super.hasKeyword(Keyword.ELECTRIC)) {
                creeper.setPowered(true);
            }
            return creeper;
        case PIG:
            final Pig pig = (Pig) spawnedCreature;
            if (super.hasKeyword(Keyword.SADDLED)) {
                pig.setSaddle(true);
            }
            return pig;
        case WOLF:
            final Wolf wolf = (Wolf) spawnedCreature;
            if (isAngry) {
                wolf.setAngry(true);
                goForTheEyes(wolf);
            }
            return wolf;
        case SPIDER:
            final Spider spider = (Spider) spawnedCreature;
            if (isAngry) {
                goForTheEyes(spider);
            }
            return spider;
        case PIG_ZOMBIE:
            final PigZombie pigzombie = (PigZombie) spawnedCreature;
            if (isAngry) {
                pigzombie.setAnger(4000);
                goForTheEyes(pigzombie);
            }
            return pigzombie;
        default:
            return spawnedCreature;
        }
    }
    
    @Override
    public void dropIt(final World world, final Location location, final Keyword event) {
        if (!checkEvent(event)) {
            return;
        }
        final int loops = getNumber();
        int loop = 0;
        while (loop < loops) {
            spawnIt(world, location);
            loop++;
        }
        
    }
}