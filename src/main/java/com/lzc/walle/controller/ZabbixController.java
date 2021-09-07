package com.lzc.walle.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzc.walle.service.ZabbixService;
import com.lzc.walle.vo.Score;
import com.lzc.walle.vo.Top;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author LZC
 */
@CrossOrigin
@RestController
@RequestMapping("/zabbix")
public class ZabbixController {
    @Autowired
    private ZabbixService zabbixService;


    @ApiOperation(value = "获取主机列表")
    @PostMapping("GetHostList")
    public List<Top> GetHostList() throws Exception {
        JSONArray hostList = zabbixService.getHostList();
        List<Top> hostIds = new ArrayList<>();
        for (int i = 0; i < hostList.size(); i++) {
            JSONObject jsonObject = hostList.getJSONObject(i);
            String hostid = jsonObject.getString("hostid");
            //TODO 查询问题总数
            Integer problemByHostId = zabbixService.getProblemByHostId(hostid);
            String name = jsonObject.getString("name");
            Top top = new Top(i, hostid, problemByHostId, name, null);
            hostIds.add(top);
//            map.put(hostid,problemByHostId);

        }
//        排序并且截取前五
        List<Top> tops = hostIds.stream().sorted(Comparator.comparing(Top::getNumber).reversed()).collect(Collectors.toList()).subList(0, 5);

        for (Top top : tops) {
            String hostId = top.getHostId();
            List<String> list = new ArrayList<>();
            for (int j = 0; j <= 5; j++) {
                String questionNumberByHostId = zabbixService.getQuestionNumberByHostId(j, hostId);
                list.add(questionNumberByHostId);
            }
            top.setProblem(list);
        }


        return tops;
    }

    @ApiOperation(value = "获取主机监控项")
    @PostMapping("getMonitorItems/{hostId}")
    public JSONArray GetHostList(@PathVariable String hostId) throws Exception {
        JSONArray monitorItems = zabbixService.getMonitorItems(hostId);
        return monitorItems;
    }

    @ApiOperation(value = "通过 Key 查询 Items")
    @PostMapping("getItemByKey/{keyId}")
    public JSONArray getItemByKey(@PathVariable String keyId) throws Exception {
        JSONArray itemByKey = zabbixService.getItemByKey(keyId);
        return itemByKey;
    }

    @ApiOperation(value = "获取npt时间")
    @PostMapping("getNtpTime")
    public boolean getNtpTime() throws IOException {
        long ntpTime = zabbixService.getNtpTime();
        long time = zabbixService.getTime();
        System.out.println(ntpTime + "===" + time);
        long l = ntpTime - time;
        if (Math.abs(l) > 10) {
            return false;
        }
        return true;
    }

    @ApiOperation(value = "获取问题个数")
    @PostMapping("getTemperature")
    public String getTemperature(Integer level) throws Exception {
        String temperature = zabbixService.getQuestionNumber(level);
        return temperature;
    }

    @ApiOperation(value = "获取每个级别问题写入一个List集合")
    @PostMapping("getTemperatureArray")
    public List<String> getTemperatureArray() throws Exception {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <= 5; i++) {
            String temperature = getTemperature(i);
            list.add(temperature);
        }
        return list;
    }

    @ApiOperation(value = "获取指定主机的CPU使用率")
    @PostMapping("getCpuUtilization/{hostId}")
    public Double getCpuUtilization(@PathVariable String hostId) throws Exception {
        Double cpuUtilization = zabbixService.getCpuUtilization(hostId);
        return cpuUtilization;
    }

    @ApiOperation(value = "获取指定主机的内存使用率")
    @PostMapping("getMemoryUtilization/{hostId}")
    public double getMemoryUtilization(@PathVariable String hostId) throws Exception {
        double memoryUtilization = zabbixService.getMemoryUtilization(hostId);
        return memoryUtilization;
    }

    @ApiOperation(value = "获取指定主机的虚拟内存分数")
    @PostMapping("swapScore/{hostId}")
    public Score swapScore(@PathVariable String hostId) throws Exception {
        Score score = zabbixService.swapScore(hostId);
        return score;
    }

    @ApiOperation(value = "获取磁盘写速度")
    @PostMapping("getDiskWriteRate/{hostId}")
    public double getDiskWriteRate(@PathVariable String hostId) throws Exception {
        double diskWriteRate = zabbixService.getDiskWriteRate(hostId);
        return diskWriteRate;
    }

    @ApiOperation(value = "获取磁盘读速度")
    @PostMapping("getDiskReadRate/{hostId}")
    public Double getDiskReadRate(@PathVariable String hostId) throws Exception {
        Double diskReadRate = zabbixService.getDiskReadRate(hostId);
        return diskReadRate;
    }


    @ApiOperation(value = "获取IOPS")
    @PostMapping("gitISOP/{hostId}")
    public Score getIOPS(@PathVariable String hostId) throws Exception {
        Score iops = zabbixService.getIOPS(hostId);
        return iops;
    }

    @ApiOperation(value = "获取已/未解决问题个数")
    @PostMapping("/getProblem")
    public List<String> getProblem() throws Exception {
        String problemSolve = zabbixService.getProblemSolve();
        String problemNoSolve = zabbixService.getProblemNoSolve();
        List<String> list = new ArrayList<>();
        list.add(problemSolve);
        list.add(problemNoSolve);
        return list;
    }


    @ApiOperation(value = "获取各状态监控项数量")
    @PostMapping("/getItem")
    public List<Integer> getItem() throws Exception {
        JSONArray hostList = zabbixService.getHostList();
        List<Integer> list = new ArrayList<>();
        int itemNum0 = 0;
        int itemNum1 = 0;

        for (int i = 0; i < hostList.size(); i++) {
            JSONObject jsonObject = hostList.getJSONObject(i);
            String hostid = jsonObject.getString("hostid");

            Integer item0 = zabbixService.getItem(hostid, "0");
            itemNum0 = item0 + itemNum0;
            Integer item1 = zabbixService.getItem(hostid, "1");
            itemNum1 = item1 + itemNum1;
        }
        list.add(itemNum0);
        list.add(itemNum1);

        int triggerNum0 = 0;
        int triggerNum1 = 0;
        for (int i = 0; i < hostList.size(); i++) {
            JSONObject jsonObject = hostList.getJSONObject(i);
            String hostid = jsonObject.getString("hostid");

            Integer trigger0 = zabbixService.getTrigger(hostid, "0");
            triggerNum0 = trigger0 + triggerNum0;
            Integer trigger1 = zabbixService.getTrigger(hostid, "1");
            triggerNum1 = trigger1 + triggerNum1;
        }
        list.add(triggerNum0);
        list.add(triggerNum1);
        return list;
    }

    @ApiOperation(value = "获取各状态触发器数量")
    @PostMapping("/getTrigger")
    public List<Integer> getTrigger() throws Exception {
        JSONArray hostList = zabbixService.getHostList();
        List<Integer> list = new ArrayList<>();
        int triggerNum0 = 0;
        int triggerNum1 = 0;
        for (int i = 0; i < hostList.size(); i++) {
            JSONObject jsonObject = hostList.getJSONObject(i);
            String hostid = jsonObject.getString("hostid");

            Integer trigger0 = zabbixService.getTrigger(hostid, "0");
            triggerNum0 = trigger0 + triggerNum0;
            Integer trigger1 = zabbixService.getTrigger(hostid, "1");
            triggerNum1 = trigger1 + triggerNum1;
        }
        list.add(triggerNum0);
        list.add(triggerNum1);
        return list;
    }

    @ApiOperation(value = "单台机器健康度计算")
    @PostMapping("/getHealth")
    public Double getHealth(String hostId) throws Exception {
        Double health = zabbixService.health(hostId);
        return health;
    }

    @ApiOperation(value = "集群健康度计算")
    @PostMapping("/getAllHealth")
    public double getAllHealth() throws Exception {
        JSONArray hostList = zabbixService.getEvaHostId();
//        System.out.println(hostList);
        double sum = 0;
        for (int i = 0; i < hostList.size(); i++) {
            JSONObject jsonObject = hostList.getJSONObject(i);
            String hostid = jsonObject.getString("hostid");
//            double health = getHealth(hostid);
            sum += getHealth(hostid);
        }
        System.out.println(sum / hostList.size());
        int round = (int) Math.round(sum / hostList.size());
        return round;
    }

    @ApiOperation(value = "获取温度")
    @PostMapping("/getTem")
    public String getTem() throws Exception {
        String temperature = zabbixService.getTemperature();
        return temperature.substring(0,4);
    }

    @ApiOperation(value = "获取湿度")
    @PostMapping("/getHumidity")
    public String getHumidity() throws Exception {
        String humidity = zabbixService.getHumidity();
        return humidity.substring(0,4);
    }

    @ApiOperation(value = "获取Server端磁盘占用")
    @PostMapping("/getUtilization")
    public String getUtilization() throws Exception {
        String utilization = zabbixService.getUtilization();
        return utilization.substring(0,4);
    }

}
