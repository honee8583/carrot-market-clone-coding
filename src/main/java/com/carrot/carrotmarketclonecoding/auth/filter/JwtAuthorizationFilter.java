package com.carrot.carrotmarketclonecoding.auth.filter;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.TOKEN_NOT_EXISTS;

import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.common.exception.JwtTokenExpiredException;
import com.carrot.carrotmarketclonecoding.common.exception.JwtTokenNotValidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (isAuthorizationHeaderExists(request)) {
                String token = request.getHeader(JwtVO.HEADER);
                LoginUser loginUser = jwtUtil.verify(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                request.setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, TOKEN_NOT_EXISTS.getMessage());
            }
        } catch (JwtTokenExpiredException e) {
            request.setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, e.getMessage());
        } catch (JwtTokenNotValidException e) {
            request.setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, e.getMessage());
        }
        chain.doFilter(request, response);
    }

    private boolean isAuthorizationHeaderExists(HttpServletRequest request) {
        String token = request.getHeader(JwtVO.HEADER);
        if (token == null || !token.startsWith(JwtVO.TOKEN_PREFIX)) {
            return false;
        }
        return true;
    }
}