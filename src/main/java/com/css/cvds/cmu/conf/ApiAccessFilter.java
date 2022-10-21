package com.css.cvds.cmu.conf;

import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.storager.dao.dto.AccessLogDto;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.common.ApiSaveConstant;
import com.css.cvds.cmu.service.ILogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lin
 */
@WebFilter(filterName = "ApiAccessFilter", urlPatterns = "/api/*", asyncSupported=true)
public class ApiAccessFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(ApiAccessFilter.class);


    @Autowired
    private UserSetting userSetting;

    @Autowired
    private ILogService logService;


    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        if (SecurityUtils.getUserInfo() == null) {
            username = servletRequest.getParameter("username");
        }else {
            username = SecurityUtils.getUserInfo().getUsername();
        }
        long start = System.currentTimeMillis(); // 请求进入时间
        String uriName = ApiSaveConstant.getVal(servletRequest.getRequestURI());

        filterChain.doFilter(servletRequest, servletResponse);

        if (uriName != null && userSetting.getLogInDatabase()) {
            AccessLogDto accessLogDto = new AccessLogDto();
            accessLogDto.setName(uriName);
            accessLogDto.setUsername(username);
            accessLogDto.setAddress(servletRequest.getRemoteAddr());
            accessLogDto.setResult(HttpStatus.valueOf(servletResponse.getStatus()).toString());
            accessLogDto.setTiming(System.currentTimeMillis() - start);
            accessLogDto.setType(servletRequest.getMethod());
            accessLogDto.setUri(servletRequest.getRequestURI());
            accessLogDto.setCreateTime(DateUtil.getNow());
            logService.addAccessLog(accessLogDto);
        }
    }

    /**
     * 获取IP地址
     *
     * @param request 请求
     * @return request发起客户端的IP地址
     */
    private String getIP(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        }

        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");

        String UNKNOWN_IP = "unknown";
        if (StringUtils.isNotEmpty(XFor) && !UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }

        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            return XFor;
        }

        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }
}
