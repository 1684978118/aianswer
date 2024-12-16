package com.xiaobo.aianswer.mapper;

import com.xiaobo.aianswer.model.dto.statistic.AppAnswerCountDTO;
import com.xiaobo.aianswer.model.dto.statistic.AppAnswerResultCountDTO;
import com.xiaobo.aianswer.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chen
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2024-10-21 17:25:33
* @Entity generator.domain.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    @Select("select appId ,count(userId) as answerCount from user_answer group by appId order by answerCount desc limit 10;")
    List<AppAnswerCountDTO> doAppAnswerCount();

    @Select("select resultName, count(resultName) as resultCount from user_answer where appId = #{appId} group by resultName order by resultCount desc;")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);

}




