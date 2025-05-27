package propensi.tens.bms.core.security.jwt;


import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
 
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
 
    @Value("${bms.app.jwtSecret}")
    private String jwtSecret;
 
    @Value("${bms.app.jwtExpirationMs}")
    private int jwtExpirationMs;
 
    @SuppressWarnings("deprecation")
    public String generateJwtToken(String userId, String username, List<String> roles) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256);
        
        return builder.compact();
    }

    @SuppressWarnings("deprecation")
    public Claims getAllClaimsFromJwtToken(String token) {
        JwtParser jwtParser = ((JwtParserBuilder) Jwts.builder())
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String getUserNameFromJwtToken(String token){
        JwtParser jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
        Claims claims = jwtParser.parse(token).accept(Jws.CLAIMS).getPayload();
        return claims.getSubject();
    }
 
    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parse(authToken);
            return true;
        }catch(SignatureException e){
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }catch(IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch(ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        }catch(UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        return false;
    }
}
