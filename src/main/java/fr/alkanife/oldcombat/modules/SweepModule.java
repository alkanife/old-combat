package fr.alkanife.oldcombat.modules;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedParticle;
import fr.alkanife.oldcombat.OldCombat;
import org.bukkit.Particle;

public class SweepModule {

    public static boolean ENABLED = false;

    public SweepModule(OldCombat oldCombat) {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(oldCombat, ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_PARTICLES) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (!ENABLED)
                            return;

                        for (WrappedParticle p : event.getPacket().getNewParticles().getValues()) {
                            //getPlugin().getLogger().info(p.getParticle().name());
                            if (p.getParticle().name().contains("SWEEP"))
                                event.setCancelled(true);
                        }
                    }
                });
    }
}
