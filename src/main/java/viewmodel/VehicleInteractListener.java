package viewmodel;

import VO.ConfigVehicleVO;
import main.Main;
import model.BigVehicleObject;
import model.SmallVehicleObject;
import model.VehicleObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
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
import java.util.logging.Level;


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


        if (Objects.equals(event.getHand(), EquipmentSlot.HAND) && !player.hasCooldown(event.getMaterial())) {
            if (action == Action.RIGHT_CLICK_AIR &&
                    maybeItem
                            .map(ItemStack::getItemMeta)
                            .map(ItemMeta::getLore)
                            .map(list -> list.get(0))
                            .map(lore -> Main.TYPE_LIST.contains(lore))
                            .orElse(false) &&
                    !VehicleObject.vehicles.containsKey(player)) {

                player.setCooldown(event.getMaterial(), Main.COOL_DOWN * 20);

                maybeItem
                        .map(ItemStack::getItemMeta)
                        .map(ItemMeta::getLore)
                        .map(list -> list.get(0))
                        .ifPresent(lore -> {
                            if (plugin.getConfig().get(lore, null) != null) {
                                ConfigVehicleVO vo = new ConfigVehicleVO(plugin.getConfig(), lore);
                                if (vo.isSmall()) {
                                    VehicleObject.vehicles.put(player, new SmallVehicleObject(vo, player));
                                } else {
                                    VehicleObject.vehicles.put(player, new BigVehicleObject(vo, player));
                                }
                                event.setCancelled(true);
                            }
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
                VehicleObject vehicle = maybeTargetPlayer.map(vehicles::get).get();
                if (!vehicle.seatPlayer(player)) {
                    player.sendTitle("빈자리가 없습니다.", "남은자리 0", 20, 40, 20);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBetweenDamageIgnore(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if ("EntityDamageEvent".equals(event.getEventName())) {
//                Bukkit.getLogger().log(Level.INFO, "데미지가 무효화를 시작합니다.");
                Player player = (Player) event.getEntity();
//                Bukkit.getLogger().log(Level.INFO, "무효화 대상 탐색중입니다.");
                for (Map.Entry<Player, VehicleObject> vehicle : VehicleObject.vehicles.entrySet()) {
//                    Bukkit.getLogger().log(Level.INFO, vehicle.getKey().getDisplayName() + "의 차량을 탐색합니다.");
//                    Bukkit.getLogger().log(Level.INFO, player.getDisplayName() + "해당유저를 탐색합니다.");
//                    Bukkit.getLogger().log(Level.INFO, vehicle.getValue().getSeaterList().toString());

                    if (vehicle.getValue().getSeaterList().containsKey(player)) {
                        event.setCancelled(true);
//                        Bukkit.getLogger().log(Level.INFO, "데미지가 무효화 되었습니다.");
                        return ;
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
                    if (vehicle.seatLeavePlayer(player) <= 0
                            || player.getUniqueId().toString().equals(ownerPlayer.getUniqueId().toString())) {
                        vehicle.getSeatList().forEach((integer, armorStand) -> armorStand.remove());
                        VehicleObject.vehicles.remove(ownerPlayer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(Entity::remove);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerBedLeaveEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(Entity::remove);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerQuitEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(Entity::remove);
    }
}
