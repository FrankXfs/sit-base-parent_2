package cn.com.sit.tokenservice.identity;


import cn.com.sit.tokenservice.model.user.Role;
import cn.com.sit.tokenservice.model.user.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenUtil {

    //private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;// 10 days Validity
    private static final long VALIDITY_TIME_MS =  2 * 60 * 60 * 1000; // 2 hours  validity
    private static final String AUTH_HEADER_NAME = "Authorization";

    //@To-do:需要修改为从配置文件获取
    private String secret="sit_base_platform";

    public Optional<Authentication> verifyToken(HttpServletRequest request) {
      final String token = request.getHeader(AUTH_HEADER_NAME);

      if (token != null && !token.isEmpty()){
        final TokenUser user = parseUserFromToken(token.replace("Bearer","").trim());
        if (user != null) {
            return  Optional.of(new UserAuthentication(user));
        }
      }
      return Optional.empty();

    }

    //Get User Info from the Token
    public TokenUser parseUserFromToken(String token){

        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();

        User user = new User();
        user.setUserId( (String)claims.get("userId"));
        //user.setCustomerId((Integer)claims.get("customerId"));
        //user.setRole((String)claims.get("role"));
        user.setRole(Role.valueOf((String)claims.get("role")));
        return new TokenUser(user);
    }

    public String createTokenForUser(TokenUser tokenUser) {
      return createTokenForUser(tokenUser.getUser());
    }

    public String createTokenForUser(User user) {
      return Jwts.builder()
        .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_TIME_MS))
        .setSubject(user.getFullName())
        .claim("userId", user.getUserId())
        .claim("role", user.getRole().toString())
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
    }

}
