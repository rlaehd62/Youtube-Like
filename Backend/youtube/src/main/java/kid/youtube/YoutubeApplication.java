package kid.youtube;

import kid.youtube.Entity.Member;
import kid.youtube.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class YoutubeApplication
{
    @Autowired
    private MemberRepository memberRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(YoutubeApplication.class, args);
    }

    @Bean
    CommandLineRunner runner()
    {
        return args ->
        {
            if(!memberRepository.existsMemberByRoles("ROLE_ADMIN"))
            {
                Member admin = new Member("KimDongDong", "kid1234", "KimDongDong", "ROLE_ADMIN");
                memberRepository.save(admin);
            }
        };
    }
}
