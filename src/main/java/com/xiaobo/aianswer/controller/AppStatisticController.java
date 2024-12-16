package com.xiaobo.aianswer.controller;

import com.xiaobo.aianswer.common.BaseResponse;
import com.xiaobo.aianswer.common.ErrorCode;
import com.xiaobo.aianswer.common.ResultUtils;

import com.xiaobo.aianswer.exception.ThrowUtils;

import com.xiaobo.aianswer.mapper.UserAnswerMapper;
import com.xiaobo.aianswer.model.dto.statistic.AppAnswerCountDTO;
import com.xiaobo.aianswer.model.dto.statistic.AppAnswerResultCountDTO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;

import java.util.List;

/**
 * APP 统计分析
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用及回答数统计(top 10)
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAnswerCount(){
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 莫应用回答结果分布统计
     * @param appId
     * @return
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAnswerResultCount(Long appId){
        ThrowUtils.throwIf(appId == null || appId <= 0 , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }

}
