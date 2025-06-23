package ch.bbzbl_it.module_306_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: Security
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(oauth2Login -> oauth2Login.tokenEndpoint(Customizer.withDefaults()).userInfoEndpoint(Customizer.withDefaults()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/unauthenticated", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().fullyAuthenticated()
                )
                .logout(logout -> logout.logoutSuccessUrl("http://localhost:8081/realms/external/protocol/openid-connect/logout?redirect_uri=http://localhost:8080/"));

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                // .csrf(Customizer.withDefaults())
//                .cors(Customizer.withDefaults())
//                .authorizeHttpRequests(auth -> auth

    /// /                                        .requestMatchers("/ausleihe/**").hasAnyRole("ADMIN", "USER")
    /// /                        .requestMatchers(HttpMethod.GET, "/medium/**").hasAnyRole("ADMIN", "USER")
    /// /                        .requestMatchers(HttpMethod.POST, "/medium").hasRole("ADMIN")
    /// /                        .requestMatchers(HttpMethod.PUT, "/medium/**").hasRole("ADMIN")
    /// /                        .requestMatchers(HttpMethod.DELETE, "/medium/**").hasRole("ADMIN")
    /// /                        .requestMatchers("/kunde/**").hasRole("ADMIN")
    /// /                        .requestMatchers("/adresse/**").hasRole("ADMIN")
    /// /                        .requestMatchers("/index.html").hasAnyRole("ADMIN", "USER")
    /// /                        .requestMatchers("/v3/api-docs/**").anonymous()
//                        .requestMatchers("/**").anonymous()
//                        .anyRequest()
//                        .authenticated()
//                )
//                .sessionManagement(sessionConfig -> sessionConfig
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
//                        .maximumSessions(1)
//                )
//                .logout((logout) -> logout
//                                .invalidateHttpSession(true)
//                                .logoutSuccessUrl("/Goodbye.html")
//                                .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL)))
//                        // .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.UNAUTHORIZED))
//                );
//        return httpSecurity.build();
//    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:8080")); // Frontend URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-CSRF-TOKEN"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
