package fr.alkanife.oldcombat.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class PotionEffects {

    private static boolean canUseGetPotionEffectsMethod;

    static{
        try{
            LivingEntity.class.getDeclaredMethod("getPotionEffect", PotionEffectType.class);

            canUseGetPotionEffectsMethod = true;
        } catch(NoSuchMethodException e){
            canUseGetPotionEffectsMethod = false;
        }
    }

    /**
     * Returns the {@link PotionEffect} of a given {@link PotionEffectType} for a given {@link LivingEntity}, if present.
     *
     * @param entity the entity to query
     * @param type   the type to search
     * @return the {@link PotionEffect} if present
     */
    public static Optional<PotionEffect> get(LivingEntity entity, PotionEffectType type){
        return Optional.ofNullable(getOrNull(entity, type));
    }

    /**
     * Returns the {@link PotionEffect} of a given {@link PotionEffectType} for a given {@link LivingEntity}, if present.
     *
     * @param entity the entity to query
     * @param type   the type to search
     * @return the {@link PotionEffect} or null if not present
     */
    public static PotionEffect getOrNull(LivingEntity entity, PotionEffectType type){
        if(canUseGetPotionEffectsMethod){
            return entity.getPotionEffect(type);
        }


        return entity.getActivePotionEffects().stream()
                .filter(potionEffect -> potionEffect.getType().equals(type))
                .findAny()
                .orElse(null);
    }
}