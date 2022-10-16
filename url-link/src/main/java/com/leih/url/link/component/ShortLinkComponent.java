package com.leih.url.link.component;

import com.leih.url.common.util.CommonUtil;
import com.leih.url.link.strategry.ShardingDBConfig;
import com.leih.url.link.strategry.ShardingTableConfig;
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
        String code = encodeToBase62(murmurHash);
        String shortLinkCode = ShardingDBConfig.getRandomDBPrefix(code)+code+ ShardingTableConfig.getRandomTableSuffix(code);
        return shortLinkCode;
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
