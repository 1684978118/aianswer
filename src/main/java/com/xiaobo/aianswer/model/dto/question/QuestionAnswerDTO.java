package com.xiaobo.aianswer.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目答案封装类(用于AI评分)
 */
@Data
public class QuestionAnswerDTO implements Serializable {

    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;


    private static final long serialVersionUID = 1L;

}
