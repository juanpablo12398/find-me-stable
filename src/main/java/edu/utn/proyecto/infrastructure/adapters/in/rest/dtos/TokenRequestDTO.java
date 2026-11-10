package edu.utn.proyecto.infrastructure.adapters.in.rest.dtos;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class TokenRequestDTO {
    private HttpServletRequest request;
    private String token;
    private Claims claims;

    public TokenRequestDTO() {}

    public TokenRequestDTO(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() { return request; }
    public void setRequest(HttpServletRequest request) { this.request = request; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Claims getClaims() { return claims; }
    public void setClaims(Claims claims) { this.claims = claims; }
}

