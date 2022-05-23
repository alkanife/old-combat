package fr.alkanife.oldcombat.modules;

import fr.alkanife.oldcombat.events.OCMEntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class CriticalHitModule implements Listener {

    public static boolean ENABLED = false;

    private boolean isMultiplierRandom = true;
    private boolean allowSprinting = true;
    private boolean roundDown = true;
    private double multiplier = 1.5;
    private double addend = 1;
    private Random random = new Random();

    @EventHandler
    public void onOCMDamage(OCMEntityDamageByEntityEvent e) {
        if (!ENABLED) return;

        // In 1.9 a critical hit requires the player not to be sprinting
        if (e.was1_8Crit() && (allowSprinting || !e.wasSprinting())) {
            // Recalculate according to 1.8 rules: https://minecraft.fandom.com/wiki/Damage?oldid=706258#Critical_hits
            // That is, the attack deals a random amount of additional damage, up to 50% more (rounded down) plus one heart.
            // Bukkit 1.8_r3 code:    i += this.random.nextInt(i / 2 + 2);
            // We instead generate a random multiplier between 1 and 1.5 (or user configured)
            double actualMultiplier = isMultiplierRandom ? (1 + random.nextDouble() * (multiplier - 1)) : multiplier;
            e.setCriticalMultiplier(actualMultiplier);
            e.setCriticalAddend(addend);
            e.setRoundCritDamage(roundDown);
        }
    }
}