package fr.alkanife.oldcombat.listeners;

import fr.alkanife.oldcombat.OldCombat;
import fr.alkanife.oldcombat.events.OCMEntityDamageByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityDamageByEntityListener implements Listener {

    private OldCombat oldCombat;

    public EntityDamageByEntityListener(OldCombat oldCombat) {
        this.oldCombat = oldCombat;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        OCMEntityDamageByEntityEvent e = new OCMEntityDamageByEntityEvent
                (damager, event.getEntity(), event.getCause(), event.getDamage());

        oldCombat.getServer().getPluginManager().callEvent(e);

        if (e.isCancelled()) return;

        //Re-calculate modified damage and set it back to original event
        // Damage order: base + potion effects + critical hit + enchantments + armour effects
        double newDamage = e.getBaseDamage();

        //Weakness potion
        double weaknessModifier = e.getWeaknessModifier();
        if (e.isWeaknessModifierMultiplier()) newDamage *= weaknessModifier;
        else newDamage += weaknessModifier;

        //Strength potion
        double strengthModifier = e.getStrengthModifier() * e.getStrengthLevel();
        if (!e.isStrengthModifierMultiplier()) newDamage += strengthModifier;
        else if (e.isStrengthModifierAddend()) newDamage *= ++strengthModifier;
        else newDamage *= strengthModifier;

        // Critical hit: 1.9 is *1.5, 1.8 is *rand(0%,50%) + 1
        // Bukkit 1.8_r3 code:     i += this.random.nextInt(i / 2 + 2);
        if (e.was1_8Crit() && !e.wasSprinting()) {
            newDamage *= e.getCriticalMultiplier();
            if (e.RoundCritDamage()) newDamage = (int) newDamage;
            newDamage += e.getCriticalAddend();
        }

        //Enchantments
        newDamage += e.getMobEnchantmentsDamage() + e.getSharpnessDamage();

        if (newDamage < 0) {
            newDamage = 0;
        }



        event.setDamage(newDamage);
    }

    /**
     * Set entity's last damage 1 tick after event. For some reason this is not updated to the final damage properly.
     * (Maybe a Spigot bug?) Hopefully other plugins vibe with this. Otherwise can store this just for OCM.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void afterEntityDamage(EntityDamageByEntityEvent e) {
        final Entity damagee = e.getEntity();
        if (damagee instanceof LivingEntity) {
            final double damage = e.getFinalDamage();

            new BukkitRunnable() {
                @Override
                public void run() {
                    ((LivingEntity) damagee).setLastDamage(damage);
                }
            }.runTaskLater(oldCombat, 1);

        }
    }
}
