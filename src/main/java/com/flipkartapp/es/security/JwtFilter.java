package com.flipkartapp.es.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flipkartapp.es.Exception.UserNotLoggedInException;
import com.flipkartapp.es.entity.AccessToken;
import com.flipkartapp.es.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private AccessTokenRepository accessTRepo;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomUserDetailService userDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String rt = null;
		String at = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {

			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("at"))
					at = cookie.getValue();
				if (cookie.getName().equals("rt"))
					rt = cookie.getValue();
			}

			String username = null;
			if (at != null && rt != null) {

				Optional<AccessToken> accessToken = accessTRepo.findByTokenAndIsBlocked(at, false);
				if (accessToken == null)
					throw new UserNotLoggedInException("please log in");

				else {
					log.info("Authenticating the token...");

					username = jwtService.extractUsername(at);
					if (username == null)
						throw new RuntimeException("failed to authenticate");
					UserDetails userDetails = userDetailService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null,
							userDetails.getAuthorities());// ignoring password so null

					token.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(token);
					log.info("authenticated successfully");

				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
