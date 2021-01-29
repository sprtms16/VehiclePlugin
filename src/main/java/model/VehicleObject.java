package model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class VehicleObject {

    private float nowForwardSpeed;
    private float deceleration;
    private float maxForwardSpeed;
    private float forwardAccelerationSpeed;
    private float maxBackwardSpeed;
    private float backwardAccelerationSpeed;
    private float turnningAcceleration;
    private float gravity;

    public VehicleObject(float nowForwardSpeed,
                         float deceleration,
                         float maxForwardSpeed,
                         float forwardAccelerationSpeed,
                         float maxBackwardSpeed,
                         float backwardAccelerationSpeed,
                         float turnningAcceleration,
                         float gravity) {
        this.nowForwardSpeed = nowForwardSpeed;
        this.deceleration = deceleration;
        this.maxForwardSpeed = maxForwardSpeed;
        this.forwardAccelerationSpeed = forwardAccelerationSpeed;
        this.maxBackwardSpeed = maxBackwardSpeed;
        this.backwardAccelerationSpeed = backwardAccelerationSpeed;
        this.turnningAcceleration = turnningAcceleration;
        this.gravity = gravity;
    }

    public void releaseAcceleration(){
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

    public void moveForward(){
        nowForwardSpeed += forwardAccelerationSpeed;
    }
    public void moveBackward(){
        nowForwardSpeed -= backwardAccelerationSpeed;
    }
}
