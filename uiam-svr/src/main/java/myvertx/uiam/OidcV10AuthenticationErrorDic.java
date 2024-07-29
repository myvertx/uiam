package myvertx.uiam;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rebue.wheel.api.dic.Dic;
import rebue.wheel.api.dic.DicUtils;

@AllArgsConstructor
@Getter

public enum OidcV10AuthenticationErrorDic implements Dic {
    invalid_request(1, "无效的请求"),
    unauthorized_client(2, "客户端未被授权"),
    access_denied(3, "禁止访问"),
    unsupported_response_type(4, "不支持的响应类型"),
    invalid_scope(5, "无效的范围"),
    server_error(6, "服务器错误"),
    temporarily_unavailable(7, "服务器忙");

    private final Integer code;

    private final String  desc;

    /**
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口Dic
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    @JsonCreator
    public static OidcV10AuthenticationErrorDic getItem(final Integer pcode) {
        final OidcV10AuthenticationErrorDic result = (OidcV10AuthenticationErrorDic) DicUtils.getItem(OidcV10AuthenticationErrorDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

    @Override
    public String getName() {
        return name();
    }

    /**
     * springdoc显示枚举说明将会调用此方法
     */
    @Override
    public String toString() {
        return name() + "(" + getDesc() + ")";
    }
}
