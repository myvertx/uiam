package myvertx.uiam.cst;

public interface RedisPrefixCst {
    /**
     * 客户端机密(后面接 clientId，值为 UiamClientMo 类对象的json序列化字符串)
     */
    String CLIENT           = "myvertx.uiam.client:";
    /**
     * 客户端授权码(后面接 clientId + clientSecret，值为 authCode)
     */
    String CLIENT_AUTH_CODE = "myvertx.uiam.client-auth-code:";
}
