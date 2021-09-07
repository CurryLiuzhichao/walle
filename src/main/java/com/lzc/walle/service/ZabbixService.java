package com.lzc.walle.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzc.walle.config.ZabbixConfig;
import com.lzc.walle.util.ZabbixUtil;
import com.lzc.walle.vo.Score;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import io.swagger.models.auth.In;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LZC
 * @since 1.0
 */
@Service
public class ZabbixService extends AbstractZabbixService {
    @Autowired
    private ZabbixUtil zabbixUtil;
    @Autowired
    private ZabbixConfig zabbixConfig;


    /**
     * 获取主机列表
     *
     * @return JSONArray
     * @throws Exception
     */
    public JSONArray getHostList() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Request request = RequestBuilder.newBuilder().method("host.get").paramEntry("output", new String[]{"host", "name", "description", "hostid"}).paramEntry("selectGroups", "extend").build();
        JSONObject response = zabbixRequest(zabbixApi, request);
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
        String timeServerUrl = zabbixConfig.getNtpip();
        InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
        TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
        TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
        return (timeStamp.getTime() / 1000);
    }

    //获取当前时间戳  /1000将毫秒级时间戳转换为秒级
    public long getTime() {
        long time = (System.currentTimeMillis() / 1000);
        return time;
    }

    //获取问题个数
    public String getQuestionNumber(Integer level) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("severity", level);
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("countOutput", "true").paramEntry("filter", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
    }

    //获取问题个数
    public String getQuestionNumberByHostId(Integer level, String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("severity", level);
        Request request = RequestBuilder.newBuilder().method("problem.get").paramEntry("countOutput", "true").paramEntry("hostids", hostId).paramEntry("filter", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
    }

    //TODO 获取已经解决问题
    public String getProblemSolve() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("userid", "1");
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("message","Problem has been resolved");
        Request request = RequestBuilder.newBuilder().method("alert.get").paramEntry("countOutput", "true").paramEntry("filter", filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;
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
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("status", status);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("countOutput", "true").paramEntry("hostids", hostId).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        Integer result = response.getInteger("result");
        return result;
    }

    //TODO 获取监控项个数
    public Integer getTrigger(String hostId, String status) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("status", status);
        Request request = RequestBuilder.newBuilder().method("trigger.get").paramEntry("countOutput", "true").paramEntry("hostids", hostId).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        Integer result = response.getInteger("result");
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

    /**
     * 获取温度
     * @return
     */
    public String getTemperature() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("snmp_oid", ".1.3.6.1.4.1.100.100.0");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        Map map = JSONObject.parseObject(result.get(0).toString());
        String lastvalue = (String) map.get("lastvalue");
        System.out.println(lastvalue);
        return lastvalue;
    }

    /**
     * 获取湿度
     * @return
     */
    public String getHumidity() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("snmp_oid", ".1.3.6.1.4.1.100.101.0");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        Map map = JSONObject.parseObject(result.get(0).toString());
        String lastvalue = (String) map.get("lastvalue");
        System.out.println(lastvalue);
        return lastvalue;

    }
    /**
     * server端硬盘使用率
     * @return
     */
    public String getUtilization() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("key_", "vfs.fs.size[/,pused]");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("hostids","10084").paramEntry("search",search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        Map map = JSONObject.parseObject(result.get(0).toString());
        String lastvalue = (String) map.get("lastvalue");
        System.out.println(lastvalue);
        return lastvalue;
    }

    /**
     * 不可用
     * @return
     */
    public Integer gethost (Integer level) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("available", level);
        Request request = RequestBuilder.newBuilder().method("host.get").paramEntry("output", new String[]{"name"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result.size();


    }

    /**
     * 从此开始下方为健康度所需参数
     */

    //获取CPU使用率  system.cpu.util
    public Double getCpuUtilization(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("hostid", hostId);
        filter.put("key_", "system.cpu.util");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue", "hostid"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        Map map = JSONObject.parseObject(result.get(0).toString());
        double lastvalue = Double.parseDouble((String) map.get("lastvalue"));
        return lastvalue;
    }

    /**
     * CPU分数
     * 当CPU占用达到0%~75%为正常3
     * 当CPU占用达到75%~100%为不正常1
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public Score cpuScore(String hostId) throws Exception {
        Score score = new Score();
        Double rrd = getCpuUtilization(hostId) / 100;
        if (rrd > 0.75) {
            double sl = Math.pow(rrd - 0.75, 2) / Math.pow(1 - 0.75, 2) * (4 - 1) * 10;
            score.setScore(sl);
            score.setLimit(false);
            return score;
        }
        double sl = Math.pow(rrd - 0, 2) / Math.pow(0.75 - 0, 2) * (4 - 3) * 10;
        score.setScore(sl);
        score.setLimit(true);
        return score;
    }

    //获取内存占用率  vm.memory.util
    public double getMemoryUtilization(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("hostid", hostId);
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("key_", "vm.memory.util");
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("filter", filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        Map map = JSONObject.parseObject(result.get(0).toString());
        double lastvalue = Double.parseDouble((String) map.get("lastvalue"));
        return lastvalue;
    }

    /**
     * 内存分数
     * 当内存在60%~80%范围内时为正常3
     * 当内存在80%~100%范围时为不正常1
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public Score memoryScore(String hostId) throws Exception {
        Score score = new Score();
        Double rrd = getMemoryUtilization(hostId) / 100;
        if (rrd > 0.8) {
            double sl = Math.pow(rrd - 0.8, 2) / Math.pow(1 - 0.8, 2) * (4 - 1) * 10;
            score.setScore(sl);
            score.setLimit(true);
            return score;
        }
        double sl = Math.pow(rrd - 0, 2) / Math.pow(0.8 - 0, 2) * (4 - 3) * 10;
        score.setScore(sl);
        score.setLimit(false);
        return score;
    }

    //获取磁盘繁忙度  写速度  修改完
    public double getDiskWriteRate(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("name", "Disk write rate");
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("hostid", hostId);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("filter", filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");

        double value = 0;
        for (int i = 0; i < result.size(); i++) {
            String string = result.get(i).toString();
            JSONObject jsonObject = JSONObject.parseObject(string);
            Map map = JSONObject.parseObject(jsonObject.toJSONString());
            String lastvalue = (String) map.get("lastvalue");
            value += Double.parseDouble(lastvalue);
        }

        return value;
    }

    //获取磁盘繁忙度  读速度 修改完
    public Double getDiskReadRate(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> search = new HashMap<Object, Object>();
        search.put("name", "Disk read rate");
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("hostid", hostId);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("output", new String[]{"lastvalue"}).paramEntry("filter", filter).paramEntry("search", search).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");

        double value = 0;
        for (int i = 0; i < result.size(); i++) {
            String string = result.get(i).toString();
            JSONObject jsonObject = JSONObject.parseObject(string);
            Map map = JSONObject.parseObject(jsonObject.toJSONString());
            String lastvalue = (String) map.get("lastvalue");
            value += Double.parseDouble(lastvalue);
        }

        return value;
    }

    /**
     * 获取硬盘 每秒读写次数的总和（混合读取及写入测试）
     * 7200RPM  SATA  ~75-100IOPS
     * 10000RPM SATA  ~125-150IOPS
     * 10000RPM SAS   ~140IOPS
     * 15000RPM SAS   ~175-210IOPS
     *
     * @param
     * @return
     * @throws Exception
     */
    public Score getIOPS(String hostId) throws Exception {
        Score score = new Score();
        double rrd = getDiskReadRate(hostId) + getDiskWriteRate(hostId);
//        System.out.println(rrd);
        if (rrd > 100) {
            double sl = 20;
            score.setScore(sl);
            score.setLimit(false);
            return score;
        }
        double sl = Math.pow(rrd, 2) / Math.pow(100, 2) * (4 - 3) * 10;
        score.setScore(sl);
        score.setLimit(false);
        return score;
    }

    //分区占用率
    public void partitionUsage() {
        //TODO
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
//        RequestBuilder.newBuilder().method()
    }

    //判断系统 system.uname
    public String getSystem(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("key_", "system.uname");
        filter.put("hostid", hostId);
        Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("filter", filter).paramEntry("output", new String[]{"lastvalue"}).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        String result = response.getString("result");
        return result;

    }

    /**
     * 虚拟内存占用率由于Windows与Linux中的关键字不相同所以需要首先判断一下系统版本
     * Windows  Free swap space in %
     * Linux    Free inodes in %
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public double getSWAPUsage(String hostId) throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        String system = getSystem(hostId);
        String substring = system.substring(0);
        String L = "L";
        if (L.equals(substring)) {
            //linux系统
            Map<Object, Object> search = new HashMap<Object, Object>();
            search.put("name", "Free inodes in %");
            Map<Object, Object> filter = new HashMap<Object, Object>();
            filter.put("hostid", hostId);
            Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("filter", filter).paramEntry("search", search).build();
            JSONObject response = zabbixRequest(zabbixApi, request);
            JSONArray result = response.getJSONArray("result");
            Map map = JSONObject.parseObject(result.get(0).toString());
            double lastvalue = Double.parseDouble((String) map.get("lastvalue"));
            return lastvalue;
        } else {
            //windows系统
            Map<Object, Object> search = new HashMap<Object, Object>();
            search.put("name", "Free swap space in %");
            Map<Object, Object> filter = new HashMap<Object, Object>();
            filter.put("hostid", hostId);
            Request request = RequestBuilder.newBuilder().method("item.get").paramEntry("filter", filter).paramEntry("search", search).build();
            JSONObject response = zabbixRequest(zabbixApi, request);
            JSONArray result = response.getJSONArray("result");
            Map map = JSONObject.parseObject(result.get(0).toString());
            double lastvalue = Double.parseDouble((String) map.get("lastvalue"));
            return lastvalue;
        }
    }

    /**
     * 虚拟内存分数因为虚拟内存单位%所以rrd需要除以100
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public Score swapScore(String hostId) throws Exception {
        Score score = new Score();
        double rrd = 1 - getSWAPUsage(hostId) / 100;
        if (rrd > 0.8) {
            double sl = Math.pow(rrd - 0.8, 2) / Math.pow(1 - 0.8, 2) * (4 - 1) * 10;
            score.setScore(sl);
            score.setLimit(true);
            return score;
        }
        double sl = Math.pow(rrd - 0, 2) / Math.pow(0.8 - 0, 2) * (4 - 3) * 10;
        score.setScore(sl);
        score.setLimit(false);
        return score;
    }

    /**
     * 计算单台机器的健康度
     *
     * @param hostId
     * @return
     * @throws Exception
     */
    public Double health(String hostId) throws Exception {
        ArrayList<Score> scoreList = new ArrayList<>();
        ArrayList<Double> a = new ArrayList<>();
        ArrayList<Double> b = new ArrayList<>();
        scoreList.add(getIOPS(hostId));
        scoreList.add(swapScore(hostId));
        scoreList.add(memoryScore(hostId));
        scoreList.add(cpuScore(hostId));
        for (Score sc : scoreList) {
            if (sc.getLimit()) {
                //A类权重为1
                a.add(sc.getScore());
            } else {
                //B类权重为依次递减
                b.add(sc.getScore());
            }
        }
        //把B排序
        b.sort(Comparator.reverseOrder());
//        System.out.println(b);
        //按顺序计算B
        double bscore = 0;
        if (b.size() > 0) {
            for (int i = 0; i < b.size(); i++) {
                bscore += b.get(i) * (1 / (i + 1));
            }
        }
        double ascore = 0;
        if (a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                ascore += a.get(i) * (1);
            }
        }

//        System.out.println("A"+ascore);
//        System.out.println("B"+bscore);
        return 100 - ascore - bscore;
    }

    public JSONArray getEvaHostId() throws Exception {
        ZabbixApi zabbixApi = zabbixUtil.getZabbixApi();
        Map<Object, Object> filter = new HashMap<Object, Object>();
        filter.put("available", 1);
        Request request = RequestBuilder.newBuilder().method("host.get").paramEntry("output", new String[]{"hostid"}).paramEntry("filter", filter).build();
        JSONObject response = zabbixRequest(zabbixApi, request);
        JSONArray result = response.getJSONArray("result");
        return result;
    }





}
