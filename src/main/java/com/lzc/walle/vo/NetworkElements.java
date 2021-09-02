package com.lzc.walle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LZC
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkElements {
    /**
     * CPU使用率
     */
    private String cpuUsage;
    /**
     * 内存使用率
     */
    private String memoryUsage;
    /**
     * 硬盘繁忙度
     */
    private String diskBusy;
    /**
     * 分区占用率
     */
    private String partitionUsage;
    /**
     * 虚拟内存占用率
     */
    private String swapUsage;
}
