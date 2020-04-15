package kid.youtube.Service;

import kid.youtube.Entity.Member;
import kid.youtube.Entity.MemberDetails;
import kid.youtube.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailsService implements UserDetailsService
{
    @Autowired
    private MemberRepository memberRepository;

    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException
    {
        Optional<Member> member = memberRepository.findMemberById(id);
        member.orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다!"));
        return member.map(MemberDetails::new).get();
    }
}
