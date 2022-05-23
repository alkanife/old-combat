package fr.alkanife.oldcombat.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class KnockbackModule implements Listener {

    public static boolean ENABLED = false;

    private double knockbackHorizontal = 0.4;
    private double knockbackVertical = 0.4;
    private double knockbackVerticalLimit = 0.4;
    private double knockbackExtraHorizontal = 0.5;
    private double knockbackExtraVertical = 0.1;

    private final HashMap<UUID, Vector> playerKnockbackHashMap = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKnockbackHashMap.remove(e.getPlayer().getUniqueId());
    }

    // Vanilla does its own knockback, so we need to set it again.
    // priority = lowest because we are ignoring the existing velocity, which could break other plugins
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
        if (!ENABLED) return;

        final UUID uuid = event.getPlayer().getUniqueId();
        if (!playerKnockbackHashMap.containsKey(uuid)) return;
        event.setVelocity(playerKnockbackHashMap.get(uuid));
        playerKnockbackHashMap.remove(uuid);
    }

    // Monitor priority because we don't modify anything here, but apply on velocity change event
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!ENABLED) return;

        final Entity damager = event.getDamager();
        if (!(damager instanceof LivingEntity)) return;
        final LivingEntity attacker = (LivingEntity) damager;

        final Entity damagee = event.getEntity();
        if (!(damagee instanceof Player)) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) > 0) return;

        final Player victim = (Player) damagee;

        // Figure out base knockback direction
        Location attackerLocation = attacker.getLocation();
        Location victimLocation = victim.getLocation();
        double d0 = attackerLocation.getX() - victimLocation.getX();
        double d1;

        for (d1 = attackerLocation.getZ() - victimLocation.getZ();
             d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
            d0 = (Math.random() - Math.random()) * 0.01D;
        }

        final double magnitude = Math.sqrt(d0 * d0 + d1 * d1);

        // Get player knockback before any friction is applied
        final Vector playerVelocity = victim.getVelocity();

        // Apply friction, then add base knockback
        playerVelocity.setX((playerVelocity.getX() / 2) - (d0 / magnitude * knockbackHorizontal));
        playerVelocity.setY((playerVelocity.getY() / 2) + knockbackVertical);
        playerVelocity.setZ((playerVelocity.getZ() / 2) - (d1 / magnitude * knockbackHorizontal));

        // Calculate bonus knockback for sprinting or knockback enchantment levels
        final EntityEquipment equipment = attacker.getEquipment();
        if (equipment != null) {
            final ItemStack heldItem = equipment.getItemInMainHand().getType() == Material.AIR ?
                    equipment.getItemInOffHand() : equipment.getItemInMainHand();

            int bonusKnockback = heldItem.getEnchantmentLevel(Enchantment.KNOCKBACK);
            if (attacker instanceof Player && ((Player) attacker).isSprinting()) ++bonusKnockback;

            if (playerVelocity.getY() > knockbackVerticalLimit) playerVelocity.setY(knockbackVerticalLimit);

            if (bonusKnockback > 0) { // Apply bonus knockback
                playerVelocity.add(new Vector((-Math.sin(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) *
                        (float) bonusKnockback * knockbackExtraHorizontal), knockbackExtraVertical,
                        Math.cos(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) *
                                (float) bonusKnockback * knockbackExtraHorizontal));
            }
        }

        // Knockback is sent immediately in 1.8+, there is no reason to send packets manually
        playerKnockbackHashMap.put(victim.getUniqueId(), playerVelocity);
    }

}
