package kid.youtube.Controller;

import kid.youtube.Entity.Member;
import kid.youtube.Repository.MemberRepository;
import kid.youtube.Service.AuthService;
import kid.youtube.Service.TokenService;
import kid.youtube.Service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/token")

public class TokenController
{

    private Logger log = Logger.getLogger(this.getClass().getName());
    @Autowired private MemberRepository memberRepository;

    @Autowired private TokenService tokenService;
    @Autowired private AuthService authService;
    @Autowired private UtilityService utilityService;



    @GetMapping ("/generate")
    public ResponseEntity<?> makeToken(@RequestParam String id, @RequestParam String pw, HttpServletResponse response)
    {
        Optional<Member> op_member = memberRepository.findMemberById(id);
        op_member.orElseThrow(() -> new UsernameNotFoundException("계정을 확인할 수 없습니다."));

        Member member = op_member.get();
        if(!member.getPw().equals(pw)) throw new UsernameNotFoundException("비밀번호가 일치하지 않습니다.");

        String token = tokenService.createToken(id);
        response.addCookie(utilityService.getCookie(token));
        return ResponseEntity.ok(token);
    }

    /**
     * @param response 토큰 값을 담고 있는 만료된 쿠키를 함께 보낼 응답
     * @return 존재하는 토큰을 담고있는 쿠키의 값을 없애고, 만료시켜 응답에 추가한 뒤, 확인 메세지를 반환.
     */
    @DeleteMapping ("/expires")
    public ResponseEntity<?> expiresToken(HttpServletResponse response)
    {
        Cookie cookie = utilityService.getCookie(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("Server has expired Token Cookie!");
        return ResponseEntity.ok("Token Expired!");
    }

    /**
     * @param request 클라이언트가 보내는 요청 (헤더 또는 쿠키를 찾는 목적으로 사용)
     * @return 사용자가 토큰을 보유했다면 True, 헤더 또는 쿠키에 토큰이 없다면 필터에 의하여 요청을 거부.
     */
    @GetMapping ("/verify")
    public String verifyToken(HttpServletRequest request)
    {
        String token = utilityService.findToken(request);
        log.info("Token " + token + " Detected!");
        return "Your Token has been Verified!";
    }

    /**
     * @param request 클라이언트가 보내는 요청 (헤더 또는 쿠키를 찾는 목적으로 사용)
     * @param role 클라이언트가 확인하고 싶은 역할 (권한)
     * @return 아이디를 추출하여 유저의 권한을 확인한 후 일치하면 True, 일치하지 않으면 False (토큰이 없다면 필터에 의해서 거부됨)
     */
    @GetMapping ("/verify/{role}")
    public String verifyRole(HttpServletRequest request, @PathVariable String role)
    {
        String AccessToken = utilityService.findToken(request);
        String id = tokenService.extractUsername(AccessToken);
        Optional<Member> op_member = memberRepository.findMemberById(id);
        op_member.orElseThrow(() -> new UsernameNotFoundException("계정을 확인할 수 없습니다."));

        log.info("User " + id + " requested to response with their roles.");
        if(!authService.hasRole(op_member.get(), role.toUpperCase())) throw new RuntimeException("사용자의 권한이 일치하지 않습니다!");
        return "Good to Go!";
    }
}
