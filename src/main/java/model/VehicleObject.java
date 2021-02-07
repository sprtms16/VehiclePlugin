package model;

import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.PacketPlayInSteerVehicle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
public abstract class VehicleObject {

    private float nowForwardSpeed;
    private float deceleration;
    private float maxForwardSpeed;
    private float forwardAccelerationSpeed;
    private float maxBackwardSpeed;
    private float backwardAccelerationSpeed;
    private float turningAcceleration;
    private float gravity;

    public static Map<Player, VehicleObject> vehicles = new HashMap<>();

    Map<Integer, ArmorStand> seatList;
    private int carType;

    private boolean isSmall;

    public VehicleObject(float nowForwardSpeed,
                         float deceleration,
                         float maxForwardSpeed,
                         float forwardAccelerationSpeed,
                         float maxBackwardSpeed,
                         float backwardAccelerationSpeed,
                         float turningAcceleration,
                         float gravity,
                         Player player,
                         int seatCount,
                         int carType,
                         boolean isSmall) {
        this.nowForwardSpeed = nowForwardSpeed;
        this.deceleration = deceleration;
        this.maxForwardSpeed = maxForwardSpeed;
        this.forwardAccelerationSpeed = forwardAccelerationSpeed;
        this.maxBackwardSpeed = maxBackwardSpeed;
        this.backwardAccelerationSpeed = backwardAccelerationSpeed;
        this.turningAcceleration = turningAcceleration;
        this.gravity = gravity;
        vehicles.put(player, this);
        seatList = new HashMap<>();
        this.isSmall = isSmall;
        Stream.iterate(0, n -> n + 1)
                .limit(seatCount)
                .forEach(seat -> seatList.put(seat, makeArmorStand(player)));
        ArmorStand seatFirst = seatList.get(0);
        Objects.requireNonNull(seatFirst.getEquipment()).setHelmet(setItemStacksDurability(Material.FLINT_AND_STEEL, carType));
        seatFirst.addPassenger(player);
    }

    private void releaseAcceleration() {
        if (nowForwardSpeed != 0) {
            if (nowForwardSpeed > 0) {
                if (nowForwardSpeed - deceleration <= 0)
                    nowForwardSpeed = 0;
                else
                    nowForwardSpeed -= deceleration;
            } else if (nowForwardSpeed < 0) {
                if (nowForwardSpeed + deceleration >= 0)
                    nowForwardSpeed = 0;
                else
                    nowForwardSpeed += deceleration;
            }
        }
    }

    private void moveForward() {
        nowForwardSpeed += forwardAccelerationSpeed;
    }

    private void moveBackward() {
        nowForwardSpeed -= backwardAccelerationSpeed;
    }

    private void groundVehicle(@NotNull VehicleObject vo, @NotNull Entity vehicle, @NotNull Location vLoc) {
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

    private static void getRelative(Block block, int x, int z, Entity vehicle) {
        boolean relativeType1 = block.getRelative(x, 1, z).getType().isSolid();
        boolean relativeType2 = block.getRelative(x, 0, z).getType().isSolid();
        boolean relativeType3 = block.getRelative(x, 2, z).getType().isSolid();
        if ((relativeType1 || relativeType2) && !relativeType3)
            vehicle.setVelocity(new Vector(0, 1.01F, 0));
    }

    Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    private Location getLeftSide(Location location) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply((double) 1));
    }

    Location getBehindSide(Location location, double distance) {
        double yawRadians = Math.PI * location.getYaw() / 180;
        return location.clone().add(distance * Math.sin(yawRadians), 0, -1 * distance * Math.cos(yawRadians));
    }

    private ArmorStand makeArmorStand(Player player) {
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        //stand.setSmall(true);
        stand.setVisible(false);
        stand.setSmall(isSmall);
        //stand.setGlowing(true);
        stand.setCustomName(player.getUniqueId().toString());
        return stand;
    }

    private ItemStack setItemStacksDurability(Material material, int damage) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            ((Damageable) meta).setDamage((short) damage);
            item.setItemMeta(meta);
        }
        return item;
    }


    public void moveEvent(PacketEvent e) {
        PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) e.getPacket().getHandle();
        Entity vehicle = e.getPlayer().getVehicle();
        Player player = e.getPlayer();

        // 플레이어가 탑승 중인 엔티티가 vehicles 리스트에 포함되어 있을 경우

        //boolean leftRight = packet.a();
        float forwardBackward = packet.c();

        //ItemStack item = vehicle.getHelmet();
        VehicleObject vo = vehicles.get(player);//main.MainVariables.vehicles.get(vehicle);
        assert vehicle != null;
        Location vLoc = vehicle.getLocation();

//        if (forwardBackward == 0) {
//            // 정지 모델링, 사운드 적용
//            //item.setDurability((short) 35);
//            // player.playSound(vLoc, Sound.BLOCK_ANVIL_STEP, 0.5F, 0.4F);
//        } else {
//            // 이동 모델링, 사운드 적용
//            //item.setDurability((short) 40);
//            // player.playSound(vLoc, Sound.BLOCK_ANVIL_STEP, 0.5F, 1.2F);
//        }

        // 위 아래 관련
        groundVehicle(vo, vehicle, vLoc);

        if (forwardBackward == 0) {
            // 앞이든 뒤든 안누르면 감속 시작.
            vo.releaseAcceleration();
        }
        // W키 - 앞으로
        if (packet.c() > 0 && vo.getNowForwardSpeed() < vo.getMaxForwardSpeed()) {
            //log.log(Level.INFO, "속도 : " + vo.getNowForwardSpeed());
            vo.moveForward();
        }
        // S키 - 뒤로
        if (packet.c() < 0 && vo.getNowForwardSpeed() > vo.getMaxBackwardSpeed()) {
            //log.log(Level.INFO, "속도 : " + vo.getNowForwardSpeed());
            vo.moveBackward();
        }
        // A키 - 좌측
        if (packet.b() > 0) {
            //log.log(Level.INFO, "좌측으로");
            ((CraftArmorStand) vehicle).getHandle().yaw = (vLoc.getYaw() + (vo.getTurningAcceleration() * -1));
        }
        // D키 - 우측
        if (packet.b() < 0) {
            //log.log(Level.INFO, "우측으로");
            ((CraftArmorStand) vehicle).getHandle().yaw = (vLoc.getYaw() + vo.getTurningAcceleration());
        }

        //((CraftArmorStand) vehicle).getHandle().yaw = player.getLocation().getYaw();


        seatSorting(vehicle);
        // 최종적으로 모델링 적용
        //vehicle.setHelmet(item);
    }

    public void seatSorting(Entity vehicle) {
        seatList.forEach((location, seat) -> {
            if (location != 0) {
                if (location % 2 == 0) {
                    seat.setVelocity(getBehindSide(seatList.get(location - 2).getLocation(), 2).toVector()
                            .subtract(seat.getLocation().toVector()));
                } else {
                    seat.setVelocity(getRightSide(seatList.get(location - 1).getLocation(), 3).toVector()
                            .subtract(seat.getLocation().toVector()));
                }
                ((CraftArmorStand) seat).getHandle().yaw = vehicle.getLocation().getYaw();
            }

        });
    }
}
