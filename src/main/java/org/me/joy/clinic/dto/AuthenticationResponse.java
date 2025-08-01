package org.me.joy.clinic.dto;

/**
 * 认证响应DTO
 */
public class AuthenticationResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String department;
    private String position;
    private Long expiresIn; // 令牌有效期（毫秒）

    public AuthenticationResponse() {}

    public AuthenticationResponse(String token, Long userId, String username, String fullName, 
                                String email, String department, String position, Long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.position = position;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "type='" + type + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}