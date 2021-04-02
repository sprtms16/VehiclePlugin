package VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ConfigVehicleVO implements Serializable {
    private String carType;
    private float nowForwardSpeed;
    private float deceleration;
    private float maxForwardSpeed;
    private float forwardAccelerationSpeed;
    private float maxBackwardSpeed;
    private float backwardAccelerationSpeed;
    private float turningAcceleration;
    private float gravity;
    private int seatCount;
    private boolean isSmall;

    public ConfigVehicleVO(FileConfiguration configuration, String type) {
        this.carType = configuration.getString(type + ".carType");
        this.nowForwardSpeed = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".nowForwardSpeed")));
        this.deceleration = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".deceleration")));
        this.maxForwardSpeed = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".maxForwardSpeed")));
        this.forwardAccelerationSpeed = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".forwardAccelerationSpeed")));
        this.maxBackwardSpeed = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".maxBackwardSpeed")));
        this.backwardAccelerationSpeed = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".backwardAccelerationSpeed")));
        this.turningAcceleration = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".turningAcceleration")));
        this.gravity = Float.parseFloat(Objects.requireNonNull(configuration.getString(type + ".gravity")));
        this.seatCount = configuration.getInt(type + ".seatCount");
        this.isSmall = configuration.getBoolean(type + ".small");
    }
}
