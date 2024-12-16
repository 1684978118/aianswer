package com.xiaobo.aianswer.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaobo.aianswer.model.dto.question.QuestionContentDTO;
import com.xiaobo.aianswer.model.entity.App;
import com.xiaobo.aianswer.model.entity.Question;
import com.xiaobo.aianswer.model.entity.ScoringResult;
import com.xiaobo.aianswer.model.entity.UserAnswer;
import com.xiaobo.aianswer.model.vo.QuestionVO;
import com.xiaobo.aianswer.service.QuestionService;
import com.xiaobo.aianswer.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choice, App app) throws Exception {
        Long appId = app.getId();
        // 1.根据id 查询到题目和题目结果信息(按分数降序排序)
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        // 2.统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            QuestionContentDTO questionContentDTO = questionContent.get(i);
            String answer = choice.get(i);
            // 遍历题目中的选项
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                // 如果答案和选项的key匹配
                if (option.getKey().equals(answer)) {
                    int score = Optional.of(option.getScore()).orElse(0);
                    totalScore += score;
                }
            }
        }
        // 3.遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4.返回最终结果
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choice));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);

        return userAnswer;
    }
}
