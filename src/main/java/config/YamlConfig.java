package config;

import config.dto.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class YamlConfig {
    private Api api;
    private Work work;
    private Db db;
    private DbPool dbPool;
    private Logger logger;
}
