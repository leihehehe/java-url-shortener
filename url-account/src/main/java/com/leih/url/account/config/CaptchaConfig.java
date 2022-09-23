package com.leih.url.account.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {
    @Bean
    @Qualifier("captchaProduce r")
    public DefaultKaptcha kaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,"4");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE,"8");
        properties.setProperty(Constants.KAPTCHA_NOISE_IMPL,"com.google.code.kaptcha.impl.NoNoise");
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL,"com.google.code.kaptcha.impl.WaterRipple");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,"abcdefghijklmnopqrstuvwxyz0123456789");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
