package model;

import VO.ConfigVehicleVO;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SmallVehicleObject extends VehicleObject {

    public SmallVehicleObject(Player player,
                              int seatCount,
                              int carType) {
        super(0,
                0.2f,
                0.5f,
                0.1f,
                -0.2f,
                0.1f,
                7,
                0.7f,
                player,
                seatCount,
                carType, true);

    }

    public SmallVehicleObject(float nowForwardSpeed,
                              float deceleration,
                              float maxForwardSpeed,
                              float forwardAccelerationSpeed,
                              float maxBackwardSpeed,
                              float backwardAccelerationSpeed,
                              float turnningAcceleration,
                              float gravity,
                              Player player,
                              int seatCount,
                              int carType) {
        super(nowForwardSpeed,
                deceleration,
                maxForwardSpeed,
                forwardAccelerationSpeed,
                maxBackwardSpeed,
                backwardAccelerationSpeed,
                turnningAcceleration,
                gravity,
                player,
                seatCount,
                carType, true);
    }

    public SmallVehicleObject(ConfigVehicleVO vehicleVO, Player player) {
        super(vehicleVO.getNowForwardSpeed(),
                vehicleVO.getDeceleration(),
                vehicleVO.getMaxForwardSpeed(),
                vehicleVO.getForwardAccelerationSpeed(),
                vehicleVO.getMaxBackwardSpeed(),
                vehicleVO.getBackwardAccelerationSpeed(),
                vehicleVO.getTurningAcceleration(),
                vehicleVO.getGravity(),
                player,
                vehicleVO.getSeatCount(),
                CarType.valueOf(vehicleVO.getCarType()).getType(),
                vehicleVO.isSmall()
        );
    }


    @Override
    public void seatSorting(Entity vehicle) {
        seatList.forEach((location, seat) -> {
            if (location != 0) {
                if (location % 2 == 0) {
                    seat.setVelocity(getBehindSide(seatList.get(location - 2).getLocation(), 1).toVector()
                            .subtract(seat.getLocation().toVector()));
                } else {
                    seat.setVelocity(getRightSide(seatList.get(location - 1).getLocation(), 1).toVector()
                            .subtract(seat.getLocation().toVector()));
                }
                ((CraftArmorStand) seat).getHandle().yaw = vehicle.getLocation().getYaw();
            }

        });
    }
}
