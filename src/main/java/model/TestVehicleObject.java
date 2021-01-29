package model;

public class TestVehicleObject extends VehicleObject{

    public TestVehicleObject(){
        super(0,
                0.2f,
                0.5f,
                0.1f,
                -0.2f,
                0.1f,
                7,
                0.7f);

    }

    public TestVehicleObject(float nowForwardSpeed, float deceleration, float maxForwardSpeed, float forwardAccelerationSpeed, float maxBackwardSpeed, float backwardAccelerationSpeed, float turnningAcceleration, float gravity) {
        super(nowForwardSpeed, deceleration, maxForwardSpeed, forwardAccelerationSpeed, maxBackwardSpeed, backwardAccelerationSpeed, turnningAcceleration, gravity);
    }
}
