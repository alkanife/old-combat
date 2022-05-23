package fr.alkanife.oldcombat;

import fr.alkanife.oldcombat.commands.OldCombatCommand;
import fr.alkanife.oldcombat.listeners.EntityDamageByEntityListener;
import fr.alkanife.oldcombat.modules.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OldCombat extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new AttackSpeedModule(), this);

        Bukkit.getPluginManager().registerEvents(new CriticalHitModule(), this);

        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);

        Bukkit.getPluginManager().registerEvents(new KnockbackModule(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRegenModule(this), this);

        new SoundModule(this);
        new SweepModule(this);

        if (getConfig().getBoolean("attack-speed")) {
            getLogger().info("AttackSpeed module enabled");
            AttackSpeedModule.ENABLED = true;
        }

        if (getConfig().getBoolean("critical-hits")) {
            getLogger().info("CriticalHit module enabled");
            CriticalHitModule.ENABLED = true;
        }

        if (getConfig().getBoolean("knockback")) {
            getLogger().info("Knockback module enabled");
            KnockbackModule.ENABLED = true;
        }

        if (getConfig().getBoolean("regen")) {
            getLogger().info("PlayerRegen module enabled");
            PlayerRegenModule.ENABLED = true;
        }

        if (getConfig().getBoolean("sound")) {
            getLogger().info("Sound module enabled");
            SoundModule.ENABLED = true;
        }

        if (getConfig().getBoolean("sweep")) {
            getLogger().info("Sweep module enabled");
            SweepModule.ENABLED = true;
        }

        OldCombatCommand.register();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
