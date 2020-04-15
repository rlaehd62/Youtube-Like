package kid.youtube.Security.Filter;

import kid.youtube.Entity.MemberDetails;
import kid.youtube.Repository.MemberRepository;
import kid.youtube.Service.MemberDetailsService;
import kid.youtube.Service.TokenService;
import kid.youtube.Util.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter
{

    @Autowired
    private MemberDetailsService memberDetailsService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenService tokenService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String token = null;
        if(hasHeader(request)) token = request.getHeader(Name.TOKEN);
        else if(hasCookie(request))
            token = getCookie(request).getValue();

        if(Objects.nonNull(token))
        {
            String id = tokenService.extractUsername(token);
            if(!memberRepository.existsMemberById(id) || !tokenService.isValid(token)) throw new IllegalTokenException();

            if(SecurityContextHolder.getContext().getAuthentication() == null)
            {
                UserDetails details = memberDetailsService.loadUserByUsername(id);
                UsernamePasswordAuthenticationToken dToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                dToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(dToken);
            }

        }

        filterChain.doFilter(request, response);
    }

    private boolean hasHeader(HttpServletRequest request)
    {
        return Objects.nonNull(request.getHeader(Name.TOKEN));
    }

    private boolean hasCookie(HttpServletRequest request)
    {
        if(request.getCookies() == null) return false;
        for(Cookie cookie : request.getCookies())
        {
            String name = cookie.getName();
            if(name.equals(Name.TOKEN)) return true;
        }

        return false;
    }

    private Cookie getCookie(HttpServletRequest request)
    {
        for(Cookie cookie : request.getCookies())
        {
            String name = cookie.getName();
            if(name.equals(Name.TOKEN)) return cookie;
        }

        return null;
    }

    private static class IllegalTokenException extends RuntimeException
    {
        public IllegalTokenException()
        {
            super("토큰이 유효하지 않습니다.\n다시 확인해주세요.");
        }
    }
}
