package com.leih.url.common.exception;

import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public static JsonData handler(Exception e){
        if(e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("[BizException] "+e);
            return JsonData.buildCodeAndMsg(bizException.getCode(),bizException.getMsg());
        }else{
            log.error("[Other Exception] "+e);
            return JsonData.buildError("Exception was thrown");
        }
    }
}
