package kid.youtube.Repository;

import kid.youtube.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String>
{
    Optional<Member> findMemberById(String id);
    void deleteMemberById(String id);
    boolean existsMemberById(String id);
    boolean existsMemberByRoles(String roles);
}
