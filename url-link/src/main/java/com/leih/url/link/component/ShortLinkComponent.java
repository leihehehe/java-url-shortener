package com.leih.url.link.component;

import com.leih.url.common.util.CommonUtil;
import org.springframework.stereotype.Component;

@Component
public class ShortLinkComponent {
    private static String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Generate a short url
     * @param originalUrl
     * @return
     */
    public String createShortLink(String originalUrl){
        long murmurHash = CommonUtil.MurmurHash(originalUrl);
        return encodeToBase62(murmurHash);
    }

    /**
     * base 10 to base 62
     * @param num
     * @return
     */
    private String encodeToBase62(long num){
        //thread safety
        StringBuffer stringBuffer = new StringBuffer();
        do{
            int i= (int) (num%62);
            stringBuffer.append(CHARS.charAt(i));
            num/=62;
        }while (num>0);
        return stringBuffer.reverse().toString();
    }
}
