package config.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Db {
    private String name;
    private String user;
    private String password;
    private String url;
}
