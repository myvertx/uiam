package myvertx.uiam.ro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class OidcV10AuthenticationErrorRo {
    /**
     * 错误
     */
    private String error;
    /**
     * 错误描述
     */
    private String error_description;
    /**
     * 给开发者看的错误额外信息
     */
    private String error_uri;
    /**
     * 状态
     * 如果请求时存在，则原样返回
     */
    private String state;
    /**
     * 颁发者
     */
    private String iss;
}
