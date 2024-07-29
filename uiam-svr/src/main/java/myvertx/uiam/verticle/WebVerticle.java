package myvertx.uiam.verticle;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import myvertx.uiam.OidcV10AuthenticationErrorDic;
import myvertx.uiam.config.MainProperties;
import myvertx.uiam.cst.RedisPrefixCst;
import myvertx.uiam.mo.UiamClientMo;
import myvertx.uiam.ro.OidcV10AuthenticationErrorRo;
import myvertx.uiam.ro.OidcV10AuthenticationSuccessRo;
import myvertx.uiam.to.OidcV10AuthenticationTo;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.core.RandomEx;
import rebue.wheel.vertx.util.JsonObjectUtils;
import rebue.wheel.vertx.verticle.AbstractWebVerticle;

@Slf4j
public class WebVerticle extends AbstractWebVerticle {

    @Inject
    private MainProperties mainProperties;

    @Inject
    private RedisAPI       redisApi;

    /**
     * 根据配置中的路由列表来配置路由
     */
    @Override
    protected void configRouter() {
        super.configRouter();
        this.router.get("/oidc/v1_0/authentication").handler(this::handleOidcV10Authentication);
    }

    /**
     * 处理认证请求
     *
     * @param routingContext 路由上下文
     */
    private void handleOidcV10Authentication(RoutingContext routingContext) {
        log.info("处理OIDC v1.0认证请求");
        OidcV10AuthenticationTo to       = JsonObjectUtils
                .multiMapToJsonObject(routingContext.queryParams())
                .mapTo(OidcV10AuthenticationTo.class);
        HttpServerResponse      response = routingContext.response();

        String                  clientId = to.getClient_id();
        if (StringUtils.isBlank(clientId)) {
            String msg = "缺少 client_id 参数";
            log.warn(msg);
            response.setStatusCode(HttpStatusCodeDic.BAD_REQUEST.getCode());
            response.end(msg);
            return;
        }

        String redirectUri = to.getRedirect_uri();
        if (StringUtils.isBlank(redirectUri)) {
            String msg = "缺少 redirect_uri 参数";
            log.warn(msg);
            response.setStatusCode(HttpStatusCodeDic.BAD_REQUEST.getCode());
            response.end(msg);
            return;
        }

        String clientRedisKey = RedisPrefixCst.CLIENT + clientId;
        redisApi.get(clientRedisKey).onSuccess(clientResp -> {
            UiamClientMo clientMo  = new JsonObject(clientResp.toString()).mapTo(UiamClientMo.class);
            boolean      isMatched = false;
            for (String matchRedirectUri : clientMo.getRedirectUris()) {
                if (redirectUri.startsWith(matchRedirectUri)) {
                    isMatched = true;
                    break;
                }
            }
            if (!isMatched) {
                String msg = "redirect_uri 参数的值超出预先配置的地址范围: "
                        + redirectUri + " ?! " + clientMo.getRedirectUris();
                log.warn(msg);
                response.setStatusCode(HttpStatusCodeDic.BAD_REQUEST.getCode());
                response.end(msg);
                return;
            }

            if (!"openid".equals(to.getScope())) {
                redirect(routingContext, redirectUri, OidcV10AuthenticationErrorRo.builder()
                        .error(OidcV10AuthenticationErrorDic.invalid_scope.name())
                        .error_description(OidcV10AuthenticationErrorDic.invalid_scope.getDesc()
                                + "->scope的值必须是openid")
                        .build());
                return;
            }

            if (!"code".equals(to.getResponse_type())) {
                redirect(routingContext, redirectUri, OidcV10AuthenticationErrorRo.builder()
                        .error(OidcV10AuthenticationErrorDic.unsupported_response_type.name())
                        .error_description(OidcV10AuthenticationErrorDic.unsupported_response_type.getDesc()
                                + "->response_type的值必须是code")
                        .build());
                return;
            }

            String clientAuthCodeRedisKey = RedisPrefixCst.CLIENT_AUTH_CODE + clientId + clientMo.getClientSecret();
            redisApi.get(clientAuthCodeRedisKey).onSuccess(authCodeResp -> {
                if (authCodeResp != null) {
                    redirect(routingContext, redirectUri, OidcV10AuthenticationSuccessRo.builder()
                            .code(authCodeResp.toString())
                            .state(to.getState())
                            .iss(mainProperties.getIss())
                            .build());
                } else {
                    String newAuthCode = RandomEx.random1(32);
                    redisApi.setex(clientAuthCodeRedisKey,
                            String.valueOf(mainProperties.getAuthCodeExpiresIn().getSeconds()),
                            newAuthCode).onSuccess(
                                    res -> redirect(routingContext, redirectUri,
                                            OidcV10AuthenticationSuccessRo.builder()
                                                    .code(newAuthCode)
                                                    .state(to.getState())
                                                    .iss(mainProperties.getIss())
                                                    .build()))
                            .onFailure(err -> {
                                String msg = "设置授权码失败(" + clientId + "," + redirectUri + ")";
                                log.error(msg);
                                redirect(routingContext, redirectUri, OidcV10AuthenticationErrorRo.builder()
                                        .error(OidcV10AuthenticationErrorDic.invalid_request.name())
                                        .error_description(msg)
                                        .build());
                            });
                }
            }).onFailure(err -> {
                String msg = "设置授权码失败(" + clientId + "," + redirectUri + ")";
                log.error(msg);
                redirect(routingContext, redirectUri, OidcV10AuthenticationErrorRo.builder()
                        .error(OidcV10AuthenticationErrorDic.invalid_request.name())
                        .error_description(msg)
                        .build());
            });
        }).onFailure(err -> redirect(routingContext, redirectUri,
                OidcV10AuthenticationErrorRo.builder()
                        .error(OidcV10AuthenticationErrorDic.invalid_request.name())
                        .build()));
    }

    @SneakyThrows
    private void redirect(RoutingContext routingContext, String redirect, OidcV10AuthenticationSuccessRo ro) {
        String parameterize = JsonObjectUtils.parameterize(JsonObject.mapFrom(ro));
        routingContext.redirect(redirect + parameterize);
    }

    @SneakyThrows
    private void redirect(RoutingContext routingContext, String redirect, OidcV10AuthenticationErrorRo ro) {
        String parameterize = JsonObjectUtils.parameterize(JsonObject.mapFrom(ro));
        routingContext.redirect(redirect + parameterize);
    }
}
