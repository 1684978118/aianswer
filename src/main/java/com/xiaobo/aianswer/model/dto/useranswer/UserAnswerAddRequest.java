package com.xiaobo.aianswer.model.dto.useranswer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建用户答案请求
 *
 * @author <a href="https://github.com/lixiaobo">程序员小波</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class UserAnswerAddRequest implements Serializable {

    /**
     * id(用户答案id，用于保证提交答案的幂等性)
     */
    private Long id;


    /**
     * 应用 id
     */
    private Long appId;


    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;


    private static final long serialVersionUID = 1L;
}