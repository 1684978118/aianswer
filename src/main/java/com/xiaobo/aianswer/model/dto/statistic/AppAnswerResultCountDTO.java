package com.xiaobo.aianswer.model.dto.statistic;

import lombok.Data;

/**
 * App 答案统计结果
 */
@Data
public class AppAnswerResultCountDTO {

    //结果名称
    private String resultName;

    //对应个数
    private String resultCount;

}
