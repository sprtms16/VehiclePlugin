package viewmodel;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProtocolAdapter extends PacketAdapter {
    public ProtocolAdapter(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
        super(plugin, listenerPriority, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        ItemStack stack = packet.getItemModifier().read(0);
        Logger log = plugin.getLogger();
        log.log(Level.INFO,stack.getType().name());

        // Only modify leather armor
        if (stack != null && stack.getType().name().contains("LEATHER")) {

//             The problem turned out to be that certain Minecraft
//             functions update every player with the same packet for
//             an equipment, whereas other methods update the
//             equipment with a different packet per player.
//
//             To fix this, we'll simply clone the packet before we
//             modify it
            packet = event.getPacket().deepClone();
            event.setPacket(packet);
            stack = packet.getItemModifier().read(0);

            // Color that depends on the player's name
            String recieverName = event.getPlayer().getName();
            int color = recieverName.hashCode() & 0xFFFFFF;

            // Update the color
            LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
            meta.setColor(Color.fromBGR(color));
            stack.setItemMeta(meta);
        }
    }

}
