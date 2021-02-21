package model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
@ToString
public enum CarType {
    TYPE1(64 - 58, "1번 타입", false),
    TYPE2(64 - 53, "2번 타입", false),
    TYPE3(64 - 48, "3번 타입", false),
    TYPE4(64 - 42, "4번 타입", false),
    TYPE5(64 - 37, "5번 타입", true),
    TYPE6(64 - 32, "6번 타입", true),
    TYPE7(64 - 26, "7번 타입", true),
    TYPE8(64 - 21, "8번 타입", false),
    TYPE9(64 - 16, "9번 타입", false),
    TYPE10(64 - 10, "10번 타입", false),
    TYPE11(64 - 5, "11번 타입", true),
    TYPE12(64 - 0, "12번 타입", false);
    private int type;
    private String typeName;
    private boolean isSmall;

    CarType(int type, String typeName, boolean isSmall) {
        this.type = type;
        this.typeName = typeName;
        this.isSmall = isSmall;
    }

    public static Optional<CarType> findBy(String type) {
        for (CarType carType : values()) {
            if (carType.getTypeName().equals(type)) {
                return Optional.of(carType);
            }
        }
        return Optional.empty();
    }

    public static Optional<CarType> findByName(String type) {
        for (CarType carType : values()) {
            if (carType.name().equals(type)) {
                return Optional.of(carType);
            }
        }
        return Optional.empty();
    }
}
