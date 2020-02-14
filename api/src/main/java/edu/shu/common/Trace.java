package edu.shu.common;

import lombok.Data;

/**
 * @author liang
 * @create 2020/2/13 9:16 下午
 */
@Data
public class Trace {
    private String traceId;
    private Long parentId;
}
