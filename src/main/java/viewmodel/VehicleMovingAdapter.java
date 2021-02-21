package viewmodel;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import model.VehicleObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VehicleMovingAdapter extends PacketAdapter {

    public VehicleMovingAdapter(JavaPlugin plugin, PacketType steerVehicle) {
        super(plugin, steerVehicle);
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        Player player = e.getPlayer();
        if (VehicleObject.vehicles.containsKey(player)) {
            VehicleObject.vehicles.get(player).moveEvent(e);
        }
    }

}
