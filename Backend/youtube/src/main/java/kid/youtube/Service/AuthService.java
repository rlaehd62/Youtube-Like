package kid.youtube.Service;

import kid.youtube.Entity.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService
{
    public boolean hasRole(Member member, String role)
    {
        List<SimpleGrantedAuthority> roles = getRoles(member);
        SimpleGrantedAuthority role_obj = new SimpleGrantedAuthority(role.contains("ROLE_") ? role : "ROLE_" + role);
        return roles.contains(role_obj);
    }

    public List<SimpleGrantedAuthority> getRoles(Member member)
    {
        return Arrays.stream(member.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
