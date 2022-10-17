package config.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Work {
    private int workCount;
    private int timeUnit;
}
