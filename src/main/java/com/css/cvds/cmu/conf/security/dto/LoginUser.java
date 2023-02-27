package com.css.cvds.cmu.conf.security.dto;

import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.storager.dao.dto.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class LoginUser implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 用户
     */
    private User user;


    /**
     * 登录时间
     */
    private String loginTime;

    /**
     * 登录终端
     */
    private String terminal;

    /**
     * 登录IP
     */
    private String ip;

    /**
     *
     * @param user
     * @param loginTime
     */
    public LoginUser(User user, String loginTime) {
        this.user = user;
        this.loginTime = loginTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 账户是否未过期，过期无法验证
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁，锁定的用户无法进行身份验证
     * <p>
     * 密码锁定
     * </p>
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码)，过期的凭据防止认证
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否被启用或禁用。禁用的用户无法进行身份验证。
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 认证完成后，擦除密码
     */
    @Override
    public void eraseCredentials() {
        user.setPassword(null);
    }

    public int getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

    public boolean isAdmin() { return getRole().getId() < Role.OPERATOR_ID; }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginTime() {
        return loginTime;
    }
}
