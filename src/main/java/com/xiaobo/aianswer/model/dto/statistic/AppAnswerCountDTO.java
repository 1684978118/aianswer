package com.xiaobo.aianswer.model.dto.statistic;

import lombok.Data;

/**
 * App 用户提交答案数统计
 */
@Data
public class AppAnswerCountDTO {

    private Long appId;

    /**
     *用户提交答案数统计
     */
    private Long answerCount;

}
