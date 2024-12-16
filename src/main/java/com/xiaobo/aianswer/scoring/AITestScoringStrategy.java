package com.xiaobo.aianswer.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaobo.aianswer.manager.AiManager;
import com.xiaobo.aianswer.model.dto.question.QuestionAnswerDTO;
import com.xiaobo.aianswer.model.dto.question.QuestionContentDTO;
import com.xiaobo.aianswer.model.entity.App;
import com.xiaobo.aianswer.model.entity.Question;
import com.xiaobo.aianswer.model.entity.ScoringResult;
import com.xiaobo.aianswer.model.entity.UserAnswer;
import com.xiaobo.aianswer.model.vo.QuestionVO;
import com.xiaobo.aianswer.service.QuestionService;
import com.xiaobo.aianswer.service.ScoringResultService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {


    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonClient redissonClient;
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * AI 评分结果本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存5分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();


    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";


    @Override
    public UserAnswer doScore(List<String> choice, App app) throws Exception {

        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choice);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);
        //如果有缓存直接返回
        if (StrUtil.isNotBlank(answerJson)){
            //构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(app.getId());
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }

        //定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);

        try {
            //竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            //没抢到锁，强行返回
            if (!res){
                return null;
            }
            //抢到锁，执行后序业务逻辑

            // 1.根据id，查询到题目和题目结果信息
            Question question = questionService.getOne(
                    Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
            );

            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

            //2 调用ai获取结果
            //封装 Prompt
            String userMessage = getAiTestScoringUserMessage(app, questionContent, choice);
            // AI生成
            String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
            // 获取需要的JSON信息
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}");
            String json = result.substring(start, end + 1);

            // 缓存结果
            answerCacheMap.put(cacheKey, json);

            // 返回最高分数和最高分数对应的评分结果
            // 3.构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
            userAnswer.setAppId(app.getId());
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);

            return userAnswer;
        }finally {
            if (lock != null && lock.isLocked()){
                // 只有本人才能释放锁
                if (lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }

        }


    }


    /**
     * AI 测评用户封装
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */

    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    /**
     * 构建缓存key
     *
     * @param appId
     * @param choicesStr
     * @return
     */
    private String buildCacheKey(Long appId, String choicesStr) {
        return DigestUtil.md5Hex(appId + ":" + choicesStr);
    }


}
