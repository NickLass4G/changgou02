package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {
    /**
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        // 令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.DKK3253F5761miwkGKsUutVlb6NnKf2SrXfiiQ4phRUUrgiaTQwd0MtaR3Ivoq304Fn5mBU5yEmWKncRLSb-oGxcifBs_U7VGe0yhaW-JY-gaOHM2wY07oBh8B0jldniH46TUUyAOFOHWysB1Ow8eDdNHE5azAI3Za_NM-6y5zR6x-It8WTLopaIG5HCq8ZrSNzfESAOLZzs3Nm02YzlQcc1ZhO3gwQE0LwnClz_UJ8QQROmJa-nTk45M6sm4Od6scm9EEM7eFAZ3VsVpSzIYgHnoJqkPCaoBHNQXGr5d_TMSGu4BVOFZ075onvffMPqoQH9chl8pIUQXCeGvqHqBQ";
        // 公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjdTQNdGbTti7soo2icqlcHB398Prcvl/hDH1HhGqjrkMkdHKtkwHwIcfza2KncekMtFFXWzFGcRWujAWNjN+CERpJTn0Yw0VZClzF1YqlhKRWOrhE6vdd/v2HdAaMzMGXL4h5PII2Nmbn3PVvSqohoTHtzy5JsvTLoLrJzyBl22WsA0qXadoU7rzUOZ4y6ChpVW4LlSVQrd00d/PIyN358dx7Z1uxkQmdHTGAZROX/RKNv3W67AFsPgi9ehh9AhIRPlehjIbyaPFXFu5s1JBy/JtcrymXTLJgXIkTzN4ow8JB3KFfMREqGGO7vQW8wF8xTWTRoeucp5HGFdnBiWGmwIDAQAB-----END PUBLIC KEY-----";
        // 校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        // 获取jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        // jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
