package myvertx.uiam.config;

import java.time.Duration;

import lombok.Data;

@Data
public class MainProperties {
    /**
     * 授权码到期时间
     * 默认10分钟
     */
    private Duration authCodeExpiresIn = Duration.parse("PT10M");
    /**
     * 颁发者
     */
    private String   iss;
}
