package com.leih.url.account.controller;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/v1/account")
public class AccountController {
    @Autowired
    Producer captchaProducer;

    /**
     * Generate captcha
     * @param request
     * @param response
     */
    @GetMapping("captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        String captchaText = captchaProducer.createText();
        log.info("Captcha code:{}",captchaText);
        BufferedImage image = captchaProducer.createImage(captchaText);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("Failed to get output stream: {}",e.getMessage());
        }
    }
}
