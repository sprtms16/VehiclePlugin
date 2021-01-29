import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import model.TestVehicleObject;
import model.VehicleObject;
import net.minecraft.server.v1_16_R3.PacketPlayInSteerVehicle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    Logger log = getLogger();
    static Map<Player, VehicleObject> vehicles = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerBoxing(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                Inventory inv = player.getInventory();
                Action action = event.getAction();
                Optional<ItemStack> maybeItem = Optional.ofNullable(event.getItem());
                if (action == Action.RIGHT_CLICK_AIR && maybeItem
                        .map(itemStack -> itemStack.isSimilar(new ItemStack(Material.STICK)))
                        .orElse(false)) {
                    event.setCancelled(true);

                    ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                    //stand.setSmall(true);
                    stand.setVisible(false);
                    ItemStack stack = new ItemStack(Material.FLINT_AND_STEEL);
                    ItemMeta meta = stack.getItemMeta();
                    stand.getEquipment().setHelmet(new ItemStack(Material.FLINT_AND_STEEL , 1, (short) (64 - 5)));
                    player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL , 1, (short) (64 - 5)));
                    stand.addPassenger(player);
                    //stand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 0));
                    vehicles.put(player, new TestVehicleObject());
//                    Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
//                    horse.setAdult();
//                    horse.setTamed(true);
//                    horse.setOwner(player);
//                    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
//                    horse.setCustomName(ChatColor.RED + "Horse");
//                    horse.addPassenger(player);
                }
            }

            @EventHandler
            public void onPlayerOffPassenger(VehicleExitEvent event) {
                if (event.getExited() instanceof Player) {
                    log.log(Level.FINE, "Player exited");
                    event.getVehicle().remove();
                    if (vehicles.get(event.getExited()) != null) {
                        removeArmorstandsInRange(event.getExited().getLocation());
                    }
                } else {
                    log.log(Level.FINE, "not Player exited");
                }

            }

            public void removeArmorstandsInRange(Location loc) {
                for (Entity en : loc.getWorld().getEntities()) {
                    if (en instanceof ArmorStand) {
                        if (en.getLocation().distance(loc) < 3f) {
                            en.remove();
                        }
                    }
                }
            }
        }, this);

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this,
                        PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) e.getPacket().getHandle();
                        Entity vehicle = e.getPlayer().getVehicle();
                        Player player = e.getPlayer();

                        // 플레이어가 탑승 중인 엔티티가 vehicles 리스트에 포함되어 있을 경우
                        if (vehicles.containsKey(player)) {
                            boolean leftRight = packet.a();
                            float forwardBackwrad = packet.c();

                            //ItemStack item = vehicle.getHelmet();
                            VehicleObject vo = vehicles.get(e.getPlayer());//main.MainVariables.vehicles.get(vehicle);
                            Location vLoc = vehicle.getLocation();

                            if (forwardBackwrad == 0) {
                                // 정지 모델링, 사운드 적용
                                //item.setDurability((short) 35);
                                // player.playSound(vLoc, Sound.BLOCK_ANVIL_STEP, 0.5F, 0.4F);
                            } else {
                                // 이동 모델링, 사운드 적용
                                //item.setDurability((short) 40);
                                // player.playSound(vLoc, Sound.BLOCK_ANVIL_STEP, 0.5F, 1.2F);
                            }

                            // 위 아래 관련
                            groundVehicle(e, vo, packet, vehicle, vLoc);

                            if (forwardBackwrad == 0) {
                                // 앞이든 뒤든 안누르면 감속 시작.
                                vo.releaseAcceleration();
                            }
                            // W키 - 앞으로
                            if (packet.c() > 0 && vo.getNowForwardSpeed() < vo.getMaxForwardSpeed()) {
                                log.log(Level.INFO, "속도 : " + vo.getNowForwardSpeed());
                                vo.moveForward();
                            }
                            // S키 - 뒤로
                            if (packet.c() < 0 && vo.getNowForwardSpeed() > vo.getMaxBackwardSpeed()) {
                                log.log(Level.INFO, "속도 : " + vo.getNowForwardSpeed());
                                vo.moveBackward();
                            }
                            // A키 - 좌측
                            if (packet.b() > 0) {
                                log.log(Level.INFO, "좌측으로");
                                ((CraftArmorStand) vehicle).getHandle().yaw = (vLoc.getYaw() + (vo.getTurnningAcceleration() * -1));
                            }
                            // D키 - 우측
                            if (packet.b() < 0) {
                                log.log(Level.INFO, "우측으로");
                                ((CraftArmorStand) vehicle).getHandle().yaw = (vLoc.getYaw() + vo.getTurnningAcceleration());
                            }
                            // 최종적으로 모델링 적용
                            //vehicle.setHelmet(item);
                        } else {
                            return;
                        }
                    }

                    private void groundVehicle(PacketEvent e, VehicleObject vo, PacketPlayInSteerVehicle packet, Entity vehicle,
                                               Location vLoc) {
                        // 중력 적용
                        vehicle.setVelocity(vLoc.getDirection().multiply(vo.getNowForwardSpeed()).setY(-1 * vo.getGravity()));
                        if (vo.getNowForwardSpeed() != 0) {
                            Block block = vLoc.getBlock();
                            // X 방향 블록 체크
                            if ((!block.getRelative(1, 1, 0).getType().isSolid() && block.getRelative(1, 0, 0).getType().isSolid()))
                                getRelative(block, 1, 0, vehicle);
                            // -X 방향 블록 체크
                            if ((!block.getRelative(-1, 1, 0).getType().isSolid() && block.getRelative(-1, 0, 0).getType().isSolid()))
                                getRelative(block, -1, 0, vehicle);
                            // Z 방향 블록 체크
                            if ((!block.getRelative(0, 1, 1).getType().isSolid() && block.getRelative(0, 0, 1).getType().isSolid()))
                                getRelative(block, 0, 1, vehicle);
                            // -Z 방향 블록 체크
                            if ((!block.getRelative(0, 1, -1).getType().isSolid() && block.getRelative(0, 0, -1).getType().isSolid()))
                                getRelative(block, 0, -1, vehicle);
                        }
                    }

                    private void getRelative(Block block, int x, int z, Entity vehicle) {
                        boolean relativeType1 = block.getRelative(x, 1, z).getType().isSolid();
                        boolean relativeType2 = block.getRelative(x, 0, z).getType().isSolid();
                        boolean relativeType3 = block.getRelative(x, 2, z).getType().isSolid();
                        if ((relativeType1 || relativeType2) && !relativeType3)
                            vehicle.setVelocity(new Vector(0, 1.01F, 0));
                        return;
                    }
                });
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }
}
