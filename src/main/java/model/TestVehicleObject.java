package model;

import org.bukkit.entity.Player;

public class TestVehicleObject extends VehicleObject{

    public TestVehicleObject(Player player,
                             int seatCount,
                             int carType){
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
                carType);

    }

    public TestVehicleObject(float nowForwardSpeed,
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
                carType);
    }
}
