package com.transportes.coches;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.transportes.coches.servicios.UsuarioDetailsService;
import com.transportes.coches.utils.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracionDeSeguridad {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtRequestFilter requestFilter;

    public ConfiguracionDeSeguridad(UsuarioDetailsService usuarioDetailsService, JwtRequestFilter requestFilter) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.requestFilter = requestFilter;
    }

    @Bean
    public PasswordEncoder codificadorContrasenia() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	public AuthenticationManager administradorAutenticacion(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
//            	.anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
