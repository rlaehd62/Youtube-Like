package kid.youtube.Security;

import kid.youtube.Security.Filter.TokenFilter;
import kid.youtube.Service.MemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
        http.
                cors().and().
                headers().frameOptions().disable().and()
                .csrf().disable().authorizeRequests()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
