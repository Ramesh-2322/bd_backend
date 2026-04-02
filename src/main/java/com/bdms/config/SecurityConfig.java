package com.bdms.config;

import com.bdms.security.CustomUserDetailsService;
import com.bdms.security.JwtAuthenticationFilter;
import com.bdms.security.RateLimitFilter;
import com.bdms.security.TenantIsolationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final TenantIsolationFilter tenantIsolationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
            .headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults())
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'")))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                            response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/uploads/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/requests/user/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/appointments/user/*").permitAll()
                    .requestMatchers("/ws/**", "/ws-notifications/**").permitAll()
                    .requestMatchers("/api/hospitals/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/requests").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                        .requestMatchers(HttpMethod.PUT, "/api/requests/*/status").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/*/status").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                    .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/subscriptions/plans").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/subscriptions/current").authenticated()
                        .requestMatchers("/api/subscriptions/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                        .requestMatchers("/api/audit-logs/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "HOSPITAL")
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/health").permitAll()
                    .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantIsolationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_SUPER_ADMIN > ROLE_ADMIN\nROLE_ADMIN > ROLE_HOSPITAL\nROLE_HOSPITAL > ROLE_DONOR");
        return roleHierarchy;
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }

    @Bean
    public HttpFirewall strictHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(false);
        firewall.setAllowBackSlash(false);
        firewall.setAllowUrlEncodedDoubleSlash(false);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(HttpFirewall firewall) {
        return web -> web.httpFirewall(firewall);
    }
}
