package viewmodel;

import VO.ConfigVehicleVO;
import main.Main;
import model.BigVehicleObject;
import model.SmallVehicleObject;
import model.VehicleObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static main.Main.killVehicleEntityByCustomNameIsUUID;


public class VehicleInteractListener implements Listener {
    JavaPlugin plugin;

    public VehicleInteractListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRaidingVehicle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Optional<ItemStack> maybeItem = Optional.ofNullable(event.getItem());
        plugin.reloadConfig();

        if (Objects.equals(event.getHand(), EquipmentSlot.HAND) && !player.hasCooldown(event.getMaterial())) {
            if (action == Action.RIGHT_CLICK_AIR &&
                    maybeItem
                            .map(ItemStack::getItemMeta)
                            .map(ItemMeta::getLore)
                            .map(list -> list.get(0))
                            .map(lore -> plugin.getConfig().getStringList("TypeList").stream().anyMatch(s -> s.equals(lore)))
                            .orElse(false) &&
                    !VehicleObject.vehicles.containsKey(player)) {

                player.setCooldown(event.getMaterial(), Main.COOL_DOWN * 20);

                maybeItem
                        .map(ItemStack::getItemMeta)
                        .map(ItemMeta::getLore)
                        .map(list -> list.get(0))
                        .filter(lore -> plugin.getConfig().getStringList("TypeList").stream().anyMatch(s -> s.equals(lore)))
                        .ifPresent(lore -> {
                            ConfigVehicleVO vo = new ConfigVehicleVO(plugin.getConfig(), lore);
                            if (vo.isSmall()) {
                                VehicleObject.vehicles.put(player, new SmallVehicleObject(vo, player));
                            } else {
                                VehicleObject.vehicles.put(player, new BigVehicleObject(vo, player));
                            }
                            event.setCancelled(true);
                        });
            }
        }
    }

    @EventHandler
    public void onInteractiveEntity(PlayerInteractAtEntityEvent event) {
        if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
            Player player = event.getPlayer();
            Optional<Player> maybeTargetPlayer = Optional.empty();
            Map<Player, VehicleObject> vehicles = VehicleObject.vehicles;
            if (event.getRightClicked() instanceof Player) {
                maybeTargetPlayer = Optional.of((Player) event.getRightClicked());

            } else if (event.getRightClicked() instanceof ArmorStand) {
                ArmorStand targetArmorStand = (ArmorStand) event.getRightClicked();
                UUID uuid = UUID.fromString(Objects.requireNonNull(targetArmorStand.getCustomName()));
                maybeTargetPlayer = Optional.ofNullable(Bukkit.getPlayer(uuid));
            }
            if (maybeTargetPlayer.map(vehicles::containsKey).orElse(false)) {
                if (maybeTargetPlayer.map(vehicles::get).isPresent()) {
                    VehicleObject vehicle = maybeTargetPlayer.map(vehicles::get).get();
                    if (!vehicle.seatPlayer(player)) {
                        player.sendTitle("빈자리가 없습니다.", "남은자리 0", 20, 40, 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBetweenDamageIgnore(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if ("EntityDamageEvent".equals(event.getEventName())) {
                Player player = (Player) event.getEntity();
                for (Map.Entry<Player, VehicleObject> vehicle : VehicleObject.vehicles.entrySet()) {
                    if (vehicle.getValue().getSeaterList().containsKey(player)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerOffPassenger(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDismounted() instanceof ArmorStand) {
                ArmorStand targetArmorStand = (ArmorStand) event.getDismounted();
                UUID uuid = UUID.fromString(Objects.requireNonNull(targetArmorStand.getCustomName()));
                Player ownerPlayer = Bukkit.getPlayer(uuid);
                if (VehicleObject.vehicles.containsKey(ownerPlayer)) {
                    VehicleObject vehicle = VehicleObject.vehicles.get(ownerPlayer);
                    if (ownerPlayer != null && (vehicle.seatLeavePlayer(player) <= 0
                            || player.getUniqueId().toString().equals(ownerPlayer.getUniqueId().toString()))) {
                        vehicle.getSeatList().forEach((integer, armorStand) -> armorStand.remove());
                        VehicleObject.vehicles.remove(ownerPlayer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void portalEvent(EntityPortalEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) event.getEntity();
            UUID uuid = UUID.fromString(Objects.requireNonNull(stand.getCustomName()));
            if (!Objects.requireNonNull(Bukkit.getPlayer(uuid)).isEmpty()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Optional.ofNullable(event.getPlayer().getVehicle())
                .ifPresent(entity -> killVehicleEntityByCustomNameIsUUID(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event) {
        Optional.ofNullable(event.getPlayer().getVehicle())
                .ifPresent(entity -> killVehicleEntityByCustomNameIsUUID(event.getPlayer()));

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Optional.ofNullable(event.getPlayer().getVehicle())
                .ifPresent(entity -> killVehicleEntityByCustomNameIsUUID(event.getPlayer()));
    }
}
