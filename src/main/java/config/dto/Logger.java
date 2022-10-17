package config.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Logger {
    private String path;
    private String encoding;
    private Integer limit;
    private Integer count;
}
