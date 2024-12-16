package com.xiaobo.aianswer.scoring;

import com.xiaobo.aianswer.model.entity.App;
import com.xiaobo.aianswer.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     * @param choice
     * @param app
     * @return
     * @throws Exception
     */
    UserAnswer doScore(List<String> choice, App app) throws Exception;

}
