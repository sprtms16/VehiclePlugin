package viewmodel;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import model.VehicleObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VehicleMovingAdapter extends PacketAdapter {

    JavaPlugin plugin;
    public VehicleMovingAdapter(JavaPlugin plugin, PacketType steerVehicle) {
        super(plugin, steerVehicle);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        Player player = e.getPlayer();
        if (VehicleObject.vehicles.containsKey(player)) {
            Bukkit.getScheduler().runTask(plugin, () -> VehicleObject.vehicles.get(player).moveEvent(e));
        }
    }

}
