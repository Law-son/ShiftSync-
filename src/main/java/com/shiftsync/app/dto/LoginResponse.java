package com.shiftsync.app.dto;

import com.shiftsync.app.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoginResponse {

    private Integer id;
    private String name;
    private Role role;
    @JsonIgnore
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    public LoginResponse() {}

    public LoginResponse(Integer id, String name, Role role, String accessToken, String refreshToken) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
