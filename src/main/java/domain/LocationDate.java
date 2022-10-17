package domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class LocationDate {
    private Integer id;

    private final Double posX;

    private final Double posY;

    private final String dateTime;

    public static class LocationDateEnum {
        public static final String ID = "id";
        public static final String POS_X = "posX";
        public static final String POS_Y = "posY";
        public static final String DATETIME = "dateTime";
    }
}
