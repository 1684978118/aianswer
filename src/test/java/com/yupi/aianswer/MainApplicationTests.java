package com.xiaobo.aianswer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaobo.aianswer.common.aiKey;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/lixiaobo">程序员小波</a>
 * @from <a href="https://xiaobo.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

//    @Test
//    void contextLoads() {
//        ClientV4 client = new ClientV4.Builder(aiKey.AI_KEY).build();
//        List<ChatMessage> messages = new ArrayList<>();
//        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为智谱开放平台创作一个吸引人的slogan");
//        messages.add(chatMessage);
////        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
//
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
//                .model(Constants.ModelChatGLM4)
//                .stream(Boolean.FALSE)
//                .invokeMethod(Constants.invokeMethod)
//                .messages(messages)
////                .requestId(requestId)
//                .build();
//        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
//        System.out.println("model output:" +invokeModelApiResp.getData().getChoices().get(0));
//
//    }

}
