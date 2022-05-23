package fr.alkanife.oldcombat.modules;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.nio.Buffer;

public class AttackSpeedModule implements Listener {

    public static boolean ENABLED = false;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent playerJoinEvent){
        adjustAttackSpeed(playerJoinEvent.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent){
        adjustAttackSpeed(playerChangedWorldEvent.getPlayer());
    }

    public static void adjustAttackSpeed(Player player) {
        int value = 4;
        if (ENABLED)
            value = 16;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);

        if (attribute == null)
            return;

        double baseValue = attribute.getBaseValue();

        if (baseValue != value) {
            attribute.setBaseValue(value);
            player.saveData();
        }
    }

    public static void resetAll() {
        for (Player player : Bukkit.getOnlinePlayers())
            adjustAttackSpeed(player);
    }

}
