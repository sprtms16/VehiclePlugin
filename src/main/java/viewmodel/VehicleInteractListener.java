package viewmodel;

import VO.ConfigVehicleVO;
import main.Main;
import model.BigVehicleObject;
import model.GiveKeyGUI;
import model.SmallVehicleObject;
import model.VehicleObject;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class VehicleInteractListener implements Listener {
    JavaPlugin plugin;

    public VehicleInteractListener(JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRaidingVehicle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Optional<ItemStack> maybeItem = Optional.ofNullable(event.getItem());

        plugin.getLogger().log(Level.INFO, maybeItem
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getLore)
                .map(list -> list.get(0)).orElse(""));


        if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
            if (action == Action.RIGHT_CLICK_AIR &&
                    maybeItem
                            .map(ItemStack::getItemMeta)
                            .map(ItemMeta::getLore)
                            .map(list -> list.get(0))
                            .map(lore -> Main.TYPE_LIST.contains(lore))
                            .orElse(false) &&
                    !VehicleObject.vehicles.containsKey(player)) {
//                String lore = maybeItem
//                        .map(ItemStack::getItemMeta)
//                        .map(ItemMeta::getLore)
//                        .map(list -> list.get(0)).orElse("");

                //Main.TYPE_LIST.contains(itemStack.getItemMeta().getDisplayName())


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
//                maybeItem.ifPresent(itemStack -> {
//
//                });
//
//                ConfigVehicleVO vo =
//                        maybeItem.map(itemStack -> new ConfigVehicleVO(plugin.getConfig(), itemStack.getItemMeta().getDisplayName())).orElse(null);
//
//                boolean isKey = false;
//                CarType carType = null;
//                for (String lore : loreList) {
//                    if (CarType.findByName(lore).isPresent()) {
//                        carType = CarType.findByName(lore).get();
//                        isKey = (true);
//                    }
//                }
//                plugin.getLogger().log(Level.INFO, String.valueOf(maybeItem.map(itemStack -> Main.TYPE_LIST.contains(itemStack.getItemMeta().getDisplayName())).orElse(false)));
//                if (isKey) {
//                    plugin.getLogger().log(Level.INFO, String.valueOf(maybeItem.map(itemStack -> Main.TYPE_LIST.contains(itemStack.getItemMeta().getDisplayName())).orElse(false)));
//                    event.setCancelled(true);
//
//                    VehicleObject.vehicles.put(player, new SmallVehicleObject(vo, player));
//                }
            } else if (action == Action.RIGHT_CLICK_AIR &&
                    maybeItem
                            .map(itemStack -> itemStack.isSimilar(new ItemStack(Material.BLAZE_ROD)))
                            .orElse(false) && player.isOp()) {
                event.setCancelled(true);
                new GiveKeyGUI(event.getPlayer(), plugin);
            }
        }
    }


    @EventHandler
    public void onInteractiveEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player) {
            Player targetPlayer = (Player) event.getRightClicked();
            if (VehicleObject.vehicles.containsKey(targetPlayer)) {
                VehicleObject vehicle = VehicleObject.vehicles.get(targetPlayer);
                Map<Integer, ArmorStand> seatList = vehicle.getSeatList();
                if (seatList.size() > 1) {
                    int seatLocation = getVehicleListByArmorStand(seatList, seatList.get(0));
                    findSeatLocation(player, seatList, seatLocation);
                }
            }
        } else if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand targetArmorStand = (ArmorStand) event.getRightClicked();
            if (targetArmorStand.getPassengers().isEmpty()) {
                targetArmorStand.addPassenger(player);
            } else {
                UUID uuid = UUID.fromString(Objects.requireNonNull(targetArmorStand.getCustomName()));
                Player carOwner = getPlayerByUuid(uuid);
                if (VehicleObject.vehicles.containsKey(carOwner)) {
                    VehicleObject vehicle = VehicleObject.vehicles.get(carOwner);
                    Map<Integer, ArmorStand> seatList = vehicle.getSeatList();
                    int seatLocation = getVehicleListByArmorStand(seatList, targetArmorStand);
                    findSeatLocation(player, seatList, seatLocation);

                }
            }
        }
    }

    public int getVehicleListByArmorStand(Map<Integer, ArmorStand> vehicleList, ArmorStand stand) {
        for (Map.Entry<Integer, ArmorStand> vehicle : vehicleList.entrySet())
            if (vehicle.getValue().equals(stand)) {
                return vehicle.getKey();
            }
        return 2;
    }


    public Player getPlayerByName(String name) {
        for (Player p : getServer().getOnlinePlayers())
            if (p.getName().equals(name))
                return p;

        throw new IllegalArgumentException();
    }

    private void findSeatLocation(Player player, Map<Integer, ArmorStand> seatList, int seatLocation) {
        if (seatList.size() > 1) {
            int tryCount = 0;
            while (!seatList.get(seatLocation).getPassengers().isEmpty() && tryCount < seatList.size()) {
                tryCount++;
                seatLocation = ++seatLocation % seatList.size();
            }
            if (tryCount < seatList.size() - 1) {
                seatList.get(seatLocation).addPassenger(player);
            } else {
                player.sendTitle("빈자리가 없습니다.", "남은자리 0", 20, 40, 20);
            }
        } else {
            player.sendTitle("빈자리가 없습니다.", "남은자리 0", 20, 40, 20);
        }
    }

    @EventHandler
    public void onPlayerOffPassenger(EntityDismountEvent event) {
        if (VehicleObject.vehicles.containsKey(event.getEntity())) {
            VehicleObject vehicle = VehicleObject.vehicles.get(event.getEntity());
            vehicle.getSeatList().forEach((integer, armorStand) -> {
                armorStand.remove();
            });
            VehicleObject.vehicles.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(entity -> {
            getLogger().log(Level.INFO, "PlayerJoinEvent");
            entity.remove();
        });
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerBedLeaveEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(entity -> {
            getLogger().log(Level.INFO, "PlayerBedLeaveEvent");
            entity.remove();
        });
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerQuitEvent event) {
        Optional<Entity> maybeVehicle = Optional.ofNullable(event.getPlayer().getVehicle());
        maybeVehicle.ifPresent(entity -> {
            getLogger().log(Level.INFO, "PlayerQuitEvent");
            entity.remove();
        });
    }


    public Player getPlayerByUuid(UUID uuid) {
        for (Player p : getServer().getOnlinePlayers())
            if (p.getUniqueId().equals(uuid))
                return p;

        throw new IllegalArgumentException();
    }
}
