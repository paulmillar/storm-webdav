package org.italiangrid.storm.webdav.spring.web;

import javax.servlet.ServletContext;

import org.italiangrid.storm.webdav.authz.VOMSAuthenticationFilter;
import org.italiangrid.storm.webdav.authz.VOMSAuthenticationProvider;
import org.italiangrid.storm.webdav.authz.VOMSVOGrantedAuthority;
import org.italiangrid.storm.webdav.config.Constants;
import org.italiangrid.storm.webdav.config.StorageAreaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ServletContext context;	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		VOMSAuthenticationProvider prov = new VOMSAuthenticationProvider();
		
		http.csrf().disable();

		http.authenticationProvider(prov).addFilter(
			new VOMSAuthenticationFilter(prov));

		StorageAreaInfo sa = (StorageAreaInfo) context
			.getAttribute(Constants.SA_CONF_KEY);
		
		if (sa.anonymousReadEnabled() && sa.authenticatedReadEnabled()) {
			
			http.authorizeRequests().antMatchers(HttpMethod.GET, "**").
				access("isAuthenticated() or isAnonymous()");
			http.authorizeRequests().antMatchers(HttpMethod.HEAD, "**").
				access("isAuthenticated() or isAnonymous()");
			
		}else if (sa.authenticatedReadEnabled()){
			http.authorizeRequests().antMatchers(HttpMethod.GET, "**").authenticated();
			http.authorizeRequests().antMatchers(HttpMethod.HEAD, "**").authenticated();
		}
		
		for (String vo : sa.vos()) {

			VOMSVOGrantedAuthority voAuthority = new VOMSVOGrantedAuthority(vo);

			http.authorizeRequests().antMatchers("**")
				.hasAuthority(voAuthority.getAuthority());
		}

	}

}