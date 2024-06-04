package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SpringSecurityConfig {

    private UserDetailsService userDetailsService;
    
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    
    /*
     * 
     * return security.userDetailsService(this.service()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests((auth) -> {
					auth.requestMatchers("/admin/**")
					.hasRole("ADMIN").requestMatchers("/employee/**")
					.hasRole("EMPLOYEE")
					.requestMatchers("/**")
					.permitAll()
					.anyRequest()
					.authenticated();
				}).formLogin(form -> {
					form.loginPage("/auth/signin").defaultSuccessUrl("/auth/validateLoginUser", true);
				}).build();
     * 
     * 
     */
    
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	System.out.println("In filterchain....");
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                authorize.requestMatchers("/admin/**")
				.hasRole("ADMIN")
				.requestMatchers("/company/**")
				.hasRole("COMPANY")
				.requestMatchers("/jobseeker/**")
				.hasRole("JOBSEEKER")
				.requestMatchers("/**")
				.permitAll()
				.anyRequest()
				.authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/userlogin")
                                .loginProcessingUrl("/login")  //login is predefined in security
                              // .defaultSuccessUrl("/company/companyHome")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/index") 
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

	public SpringSecurityConfig(UserDetailsService userDetailsService) {
		super();
		this.userDetailsService = userDetailsService;
	}
    
    
}