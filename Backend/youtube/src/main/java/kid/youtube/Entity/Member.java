package kid.youtube.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Member
{
    @Id private String id;
    private String pw;
    private String username;
    private String roles;

    public Member() {}
    public Member(String id, String pw, String username, String roles)
    {
        setId(id);
        setPw(pw);
        setUsername(username);
        setRoles(roles);
    }
}
