package com.lzc.walle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzc.walle.util.ZabbixUtil;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZabbixService extends AbstractZabbixService {
    @Autowired
    private ZabbixUtil zabbixUtil;

    //获取主机列表
    public JSONArray getHostList() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("host.get").paramEntry("output", new String[]{"host", "name", "description", "hostid"}).paramEntry("selectGroups", "extend").build();
        JSONObject response = zabbixRequest(zabbixApi,request);
        zabbixError(response);
        JSONArray result = response.getJSONArray("result");
        return result;
//        return result.toJSONString();
    }

    //获取监控项
    public JSONArray getMonitorItems(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", "extend").paramEntry("hostids", hostId).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //通过 Key 查询 Items
    public JSONArray getItemByKey(String keyId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", "extend").paramEntry("hostids", keyId).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //获取ntp时间戳   /1000将毫秒级时间戳转换为秒级
    public long getNtpTime() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        String timeServerUrl = "192.168.0.19";
        InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
        TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
        TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        return dateFormat.format(timeStamp.getDate());
        return (timeStamp.getTime()/1000);
    }

    //获取当前时间戳  /1000将毫秒级时间戳转换为秒级
    public long getTime(){
        long time = (new Date().getTime()/1000);
        return time;
    }

    //获取问题个数
    public String getQuestionNumber( Integer level) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > search = new HashMap<Object,Object>();
        search.put("severity",level);
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("countOutput", "true").paramEntry("filter", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
    }

    //获取问题个数
    public String getQuestionNumberByHostId( Integer level,String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > search = new HashMap<Object,Object>();
        search.put("severity",level);
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("countOutput", "true").paramEntry("hostids",hostId).paramEntry("filter", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
    }

    //TODO 获取已经解决问题
    public String getProblemSolve (){
        return "189";
    }

    //未解决问题总数
    public String getProblemNoSolve() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("countOutput", "true").build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
    }

    //TODO 获取监控项个数
    public Integer getItem(String hostId, String status) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("status",status);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("countOutput", "true").paramEntry("hostids",hostId).paramEntry("filter",filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        Integer result = response.getInteger("result");
        return result;
    }

    //TODO 获取监控项个数
    public Integer getTrigger(String hostId, String status) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("status",status);
        Request request = RequestBuilder.newBuilder().method("trigger.get").paramEntry("countOutput", "true").paramEntry("hostids",hostId).paramEntry("filter",filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        Integer result = response.getInteger("result");
        return result;
    }

    //获取CPU使用率  system.cpu.util
    public JSONArray getCpuUtilization(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("hostid",hostId);
        filter.put("key_","system.cpu.util");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue", "hostid"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //获取内存占用率  vm.memory.util
    public JSONArray getMemoryUtilization(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("hostid",hostId);
        Map<Object ,Object > search = new HashMap<Object,Object>();
        search.put("key_","vm.memory.util");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue", "hostid"}).paramEntry("filter", filter).paramEntry("search",search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //获取磁盘繁忙度  写速度
    public JSONArray getDiskWriteRate(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > search = new HashMap<Object,Object>();
        search.put("name","Disk write rate");
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("hostid",hostId);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("filter",filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //获取磁盘繁忙度  读速度
    public JSONArray getDiskReadRate(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object ,Object > search = new HashMap<Object,Object>();
        search.put("name","Disk read rate");
        Map<Object ,Object > filter = new HashMap<Object,Object>();
        filter.put("hostid",hostId);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("filter",filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    //根据主机ID查找问题总数
    public Integer getProblemByHostId(String hostid) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("hostids", hostid).paramEntry("countOutput", "true").build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        Integer result = response.getInteger("result");
        return result;
    }




}
