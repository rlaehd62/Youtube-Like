package kid.youtube.Service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService
{
    private final String SECRET_KEY = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    public String createToken(String id)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("username", id);

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setClaims(map)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token)
    {
        return extractClaim(token, claims -> claims.get("username").toString());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public boolean isValid(String jwt)
    {
        try
        {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(jwt);

            return true;
        } catch (Exception e)
        { throw new IllegalTokenException(); }
    }

    private static class IllegalTokenException extends RuntimeException
    {
        public IllegalTokenException()
        {
            super("토큰이 유효하지 않습니다.\n다시 확인해주세요.");
        }
    }
}
