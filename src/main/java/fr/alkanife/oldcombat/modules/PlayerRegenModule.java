package fr.alkanife.oldcombat.modules;

import fr.alkanife.oldcombat.OldCombat;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Establishes custom health regeneration rules.
 * Default values based on 1.8 from https://minecraft.gamepedia.com/Hunger?oldid=948685
 */
public class PlayerRegenModule implements Listener {

    public static boolean ENABLED = false;

    private OldCombat oldCombat;

    private final Map<UUID, Long> healTimes = new HashMap<>();
    private boolean spartanInstalled = false;

    public PlayerRegenModule(OldCombat oldCombat) {
        this.oldCombat = oldCombat;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegen(EntityRegainHealthEvent e) {
        if(e.isCancelled()) return; // In case some other plugin cancelled the event

        if (e.getEntityType() != EntityType.PLAYER
                || e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        final Player p = (Player) e.getEntity();

        if (!ENABLED) return;
        final UUID playerId = p.getUniqueId();

        // We cancel the regen, but saturation and exhaustion need to be adjusted separately
        // Exhaustion is modified in the next tick, and saturation in the tick following that (if exhaustion > 4)
        e.setCancelled(true);

        // Get exhaustion & saturation values before healing modifies them
        final float previousExhaustion = p.getExhaustion();
        final float previousSaturation = p.getSaturation();

        // Check that it has been at least x seconds since last heal
        final long currentTime = System.currentTimeMillis();
        final boolean hasLastHealTime = healTimes.containsKey(playerId);
        final long lastHealTime = healTimes.computeIfAbsent(playerId, id -> currentTime);


        // If we're skipping this heal, we must fix the exhaustion in the following tick
        if (hasLastHealTime && currentTime - lastHealTime <= 3990) {
            Bukkit.getScheduler().runTaskLater(oldCombat, () -> p.setExhaustion(previousExhaustion), 1L);
            return;
        }

        final double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        final double playerHealth = p.getHealth();

        if (playerHealth < maxHealth) {
            p.setHealth(clamp(playerHealth + 1, 0.0, maxHealth));
            healTimes.put(playerId, currentTime);
        }

        // Calculate new exhaustion value, must be between 0 and 4. If above, it will reduce the saturation in the following tick.
        final float exhaustionToApply = (float) 3;

        Bukkit.getScheduler().runTaskLater(oldCombat, () -> {
            // We do this in the next tick because bukkit doesn't stop the exhaustion change when cancelling the event
            p.setExhaustion(previousExhaustion + exhaustionToApply);
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        healTimes.remove(e.getPlayer().getUniqueId());
    }

    /**
     * Clamps a value between a minimum and a maximum.
     *
     * @param value The value to clamp.
     * @param min   The minimum value to clamp to.
     * @param max   The maximum value to clamp to.
     * @return The clamped value.
     */
    public static double clamp(double value, double min, double max){
        double realMin = Math.min(min, max);
        double realMax = Math.max(min, max);

        if(value < realMin){
            value = realMin;
        }

        if(value > realMax){
            value = realMax;
        }

        return value;
    }
}