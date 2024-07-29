package myvertx.uiam.mo;

import java.util.List;

import lombok.Data;

@Data
public class UiamClientMo {
    /**
     * 客户端标识符
     */
    private String       clientId;
    /**
     * 客户端机密
     */
    private String       clientSecret;
    /**
     * 重定向uri
     */
    private List<String> redirectUris;
}
