package com.changgou.gateway.system.filter;

import com.changgou.gateway.system.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author:Administrator
 * @Date: 2019/12/20 21:45
 */
@Component
public class AuthorizeFilter implements GlobalFilter,Ordered {
    private static final String AUTHORIZE_TOKEN = "token";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 2.获取响应
        ServerHttpResponse response = exchange.getResponse();
        // 3.如果是登录请求则放行
        if (request.getURI().getPath().contains("/admin/login")){
            return chain.filter(exchange);
        }
        // 4.获取请求头
        HttpHeaders headers = request.getHeaders();
        // 5.请求头中获取令牌
        String token = headers.getFirst(AUTHORIZE_TOKEN);
        // 6.判断请求头中是否有令牌
        if (StringUtils.isEmpty(token)){
            // 6.1 无令牌,响应中放入返回的状态吗, 没有权限访问
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 6.2 返回
            return response.setComplete();
        }
        // 7. 如果请求头中有令牌则解析令牌
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            // 8.解析jwt令牌出错, 说明令牌过期或者伪造等不合法情况出现
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 9.返回
            return response.setComplete();
        }
        // 10.放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
