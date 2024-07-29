package myvertx.uiam.to;

import lombok.Data;

@Data
public class OidcV10AuthenticationTo {
    /**
     * 范围
     * 必须填写 openid 这个值
     */
    private String scope;
    /**
     * 响应类型
     * 必须填写 code 这个值
     */
    private String response_type;
    /**
     * 客户端 ID
     * 授权服务器颁发的客户端标识符
     */
    private String client_id;
    /**
     * 重定向 URI
     * 响应时重定向的 URI，必须精确匹配在授权服务器中预配置的值
     */
    private String redirect_uri;
    /**
     * 状态
     * 用于在请求与回调间保持状态，授权请求后原样返回
     */
    private String state;
    /**
     * 随机数
     * 用于防范猜测值攻击
     */
    private String nonce;
}
