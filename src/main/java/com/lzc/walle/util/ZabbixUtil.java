package com.lzc.walle.util;

import com.lzc.walle.config.ZabbixConfig;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZabbixUtil {

    private volatile ZabbixApi zabbixApi;

    @Autowired
    private ZabbixConfig zabbixConfig;

    public ZabbixApi getZabbixApi() {
        if (null == zabbixApi) {
            synchronized (ZabbixUtil.class) {
                if (null == zabbixApi) {
                    zabbixApi = new DefaultZabbixApi(zabbixConfig.getUrl());
                    zabbixApi.init();
                    login();
                }
            }
        }
        return zabbixApi;
    }

    private void login() {
        boolean login = zabbixApi.login(zabbixConfig.getUsername(), zabbixConfig.getPassword());
        if (!login){
            throw new RuntimeException("login failed!");
        }
        log.info("login success!");
    }
}
