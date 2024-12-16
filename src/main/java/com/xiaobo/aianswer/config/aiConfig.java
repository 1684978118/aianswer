package com.xiaobo.aianswer.config;

import com.xiaobo.aianswer.common.aiKey;
import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class aiConfig {

    /**
     * apiKEY 需要从平台获取
     */
    private String apiKey;

    @Bean
    public ClientV4 getClientV4(){
        return new ClientV4.Builder(apiKey).build();
    }

}
