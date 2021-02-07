package model;

import VO.ConfigVehicleVO;
import org.bukkit.entity.Player;

public class BigVehicleObject extends VehicleObject {

    public BigVehicleObject(Player player,
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
                carType, false);

    }

    public BigVehicleObject(float nowForwardSpeed,
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
                carType, false);
    }


    public BigVehicleObject(ConfigVehicleVO vehicleVO, Player player) {
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
}
