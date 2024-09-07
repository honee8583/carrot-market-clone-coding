package com.carrot.carrotmarketclonecoding.auth.dto;

public interface JwtVO {
    String TOKEN_PREFIX = "Bearer ";
    String HEADER = "Authorization";
    String JWT_EXCEPTION_ATTRIBUTE = "jwt_exception";
}