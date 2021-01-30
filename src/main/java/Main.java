import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import model.TestVehicleObject;
import model.VehicleObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    Logger log = getLogger();


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerBoxing(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();
                Optional<ItemStack> maybeItem = Optional.ofNullable(event.getItem());
                if (event.getHand().equals(EquipmentSlot.HAND)) {
                    if (action == Action.RIGHT_CLICK_AIR && maybeItem
                            .map(itemStack -> itemStack.isSimilar(new ItemStack(Material.STICK)))
                            .orElse(false)) {
                        event.setCancelled(true);
                        VehicleObject.vehicles.put(player, new TestVehicleObject(player, 4, 6));

                    }
                }
            }

            @EventHandler
            public void onInteractiveEntity(PlayerInteractAtEntityEvent event) {
                log.log(Level.INFO, "PlayerInteractEntityEvent");
                Player player = event.getPlayer();
                if (event.getRightClicked() instanceof Player) {
                    Player targetPlayer = (Player) event.getRightClicked();
                    if (VehicleObject.vehicles.containsKey(targetPlayer)) {
                        Random random = new Random();
                        VehicleObject vehicle = VehicleObject.vehicles.get(targetPlayer);
                        vehicle.getSeatList().get(random.nextInt(vehicle.getSeatList().size() - 1) + 1).addPassenger(player);
                    }
                }
            }

            @EventHandler
            public void onPlayerOffPassenger(EntityDismountEvent event) {
                if (VehicleObject.vehicles.containsKey(event.getEntity())) {
                    Random random = new Random();
                    VehicleObject vehicle = VehicleObject.vehicles.get(event.getEntity());
                    vehicle.getSeatList().forEach((integer, armorStand) -> {
                        armorStand.remove();
                    });
                    VehicleObject.vehicles.remove(event.getEntity());
                }
            }
        }, this);

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this,
                        PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        Player player = e.getPlayer();
                        if (VehicleObject.vehicles.containsKey(player)) {
                            VehicleObject.vehicles.get(player).moveEvent(e);
                        }
                    }
                });
    }
}
