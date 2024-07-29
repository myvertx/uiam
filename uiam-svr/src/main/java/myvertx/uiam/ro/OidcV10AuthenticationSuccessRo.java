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
public class OidcV10AuthenticationSuccessRo {
    /**
     * 授权码
     */
    private String code;
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
