package com.xiaobo.aianswer.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 *
 * @author <a href="https://github.com/lixiaobo">程序员小波</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class AiGenerateQuestionAddRequest implements Serializable {



    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     *  选项数
     */
    int optionNumber = 2;

    private static final long serialVersionUID = 1L;
}