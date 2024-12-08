package util;

import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.*;

import java.util.Date;

/**
 * 生成的token包含以下部分
 * Header:记录令牌类型，签名类型
 * Payload：有效数据，携带一些自定义信息，默认信息等等
 * Signature：防止token被篡改，确保安全性。将header、payload并加入指定密钥（通过指定签名算法计算而来）
 */
public class JwHelper {
    private static long tokenExpiration = 24*60*60*1000;
    private static String tokenSignKey = "123456";

    public static String createToken(Long userId){
        String token = Jwts.builder()
                .setSubject("YYGH-USER")    //
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))  // 设置有效期
                .claim("userId", userId)    // 设置自定义数据
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)   // 设置数字签名的算法，密钥
                .compressWith(CompressionCodecs.GZIP)
                .compact(); //生成token
        return token;
    }

    public  static Long getUserId(String token){
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);    // 设置密钥并解析令牌
        Claims claims = claimsJws.getBody();    // 获得自定义数据
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    public static boolean isExpiration(String token){
        try {
            boolean isExpire = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration().before(new Date());
            return isExpire;
        }catch (Exception e){
            // 过期出现异常 返回true
            return true;
        }
    }
}
