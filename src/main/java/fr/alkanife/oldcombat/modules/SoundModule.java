package fr.alkanife.oldcombat.modules;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.alkanife.oldcombat.OldCombat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A module to disable the new attack sounds.
 */
public class SoundModule {

    public static boolean ENABLED = false;

    public SoundModule(OldCombat oldCombat) {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(oldCombat, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (!ENABLED)
                            return;

                        List<String> blockedSounds = new ArrayList<>();
                        blockedSounds.add("minecraft:entity.player.attack.strong");
                        blockedSounds.add("minecraft:entity.player.attack.sweep");
                        blockedSounds.add("minecraft:entity.player.attack.nodamage");
                        blockedSounds.add("minecraft:entity.player.attack.knockback");
                        blockedSounds.add("minecraft:entity.player.attack.crit");
                        blockedSounds.add("minecraft:entity.player.attack.weak");

                        for (Sound sound : event.getPacket().getSoundEffects().getValues())
                            if (blockedSounds.contains(sound.getKey().toString()))
                                event.setCancelled(true);
                    }
                });
    }
}