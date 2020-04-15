package kid.youtube.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


public class MemberDetails implements UserDetails
{
    private Member member;
    public MemberDetails(Member member)
    {
        this.member = member;
    }

    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return
                Arrays.stream(member.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String getPassword()
    {
        return member.getPw();
    }

    public String getUsername()
    {
        return member.getId();
    }

    public boolean isAccountNonExpired()
    {
        return true;
    }

    public boolean isAccountNonLocked()
    {
        return true;
    }

    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    public boolean isEnabled()
    {
        return true;
    }
}
