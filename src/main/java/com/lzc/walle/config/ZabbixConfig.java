package com.lzc.walle.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zabbix.config")
@Data
public class ZabbixConfig {
    private String url;
    private String username;
    private String password;
}
