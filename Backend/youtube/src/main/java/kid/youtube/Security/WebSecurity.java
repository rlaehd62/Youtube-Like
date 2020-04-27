package kid.youtube.Security;

import kid.youtube.Security.Filter.TokenFilter;
import kid.youtube.Service.MemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter
{
    @Autowired
    private MemberDetailsService memberDetailsService;

    @Autowired
    private TokenFilter tokenFilter;


    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(memberDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    protected void configure(HttpSecurity http) throws Exception
    {
        http.cors();
        http.headers().frameOptions().disable();

        http.csrf().disable().authorizeRequests()
                .antMatchers("/console").hasRole("ADMIN")
                .antMatchers("/videos/upload/**").hasRole("ADMIN")
                .antMatchers("/categories/generate").hasRole("ADMIN")
                .antMatchers("/categories/delete").hasRole("ADMIN")
                .antMatchers("/videos/delete/**").authenticated()
                .antMatchers("/token/expires").authenticated()
                .anyRequest().permitAll()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
