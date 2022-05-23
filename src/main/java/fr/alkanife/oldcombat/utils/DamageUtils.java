package fr.alkanife.oldcombat.utils;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class DamageUtils {

    // Damage order: base + potion effects + critical hit + enchantments + armour effects
    // getDamage() returns without armour effects, getFinalDamage() returns with

    /**
     * Get sharpness damage multiplier for 1.9
     *
     * @param level The level of the enchantment
     * @return Sharpness damage multiplier
     */
    public static double getNewSharpnessDamage(int level) {
        return level >= 1 ? 1 + (level - 1) * 0.5 : 0;
    }

    /**
     * Get sharpness damage multiplier for 1.8
     *
     * @param level The level of the enchantment
     * @return Sharpness damage multiplier
     */
    public static double getOldSharpnessDamage(int level) {
        return level >= 1 ? level * 1.25 : 0;
    }

    /**
     * Check preconditions for critical hit
     *
     * @param le Living entity to perform checks on
     * @return Whether hit is critical
     */
    public static boolean isCriticalHit1_8(LivingEntity le) {
        /* Code from Bukkit 1.8_R3:
        boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.k_() && !this.V()
        && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && entity instanceof EntityLiving;
        Where k_() is being on ladders or vine, and V() is in water.
        In 1.9 the player must also not be sprinting
        */
        return le.getFallDistance() > 0.0F &&
                !le.isOnGround() && // Although deprecated, auto falls back to Entity method which is server-side
                !isLivingEntityClimbing(le) &&
                !le.isInWater() &&
                le.getActivePotionEffects().stream().noneMatch(pe -> pe.getType() == PotionEffectType.BLINDNESS) &&
                !le.isInsideVehicle();
    }

    private static boolean isLivingEntityClimbing(LivingEntity le) {
        final Material material = le.getLocation().getBlock().getType();
        return material == Material.LADDER || material == Material.VINE;
    }
}