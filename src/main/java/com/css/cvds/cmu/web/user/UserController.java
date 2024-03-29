package com.css.cvds.cmu.web.user;

import com.css.cvds.cmu.common.HttpServletUtils;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.storager.dao.dto.LogDto;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
import com.css.cvds.cmu.service.IRoleService;
import com.css.cvds.cmu.service.IUserService;
import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.storager.dao.dto.User;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.github.pagehelper.PageInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Tag(name  = "用户管理")
@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private ILogService logService;

    @GetMapping("/login")
    @PostMapping("/login")
    @Operation(summary = "登录")
    @Parameter(name = "username", description = "用户名", required = true)
    @Parameter(name = "password", description = "密码（32位md5加密）", required = true)
    @Parameter(name = "terminal", description = "登录终端", required = true)
    public WVPResult<LoginUser> login(HttpServletRequest request,
                                      @RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String terminal){
        LoginUser user = null;
        String loginIp = HttpServletUtils.getRequestActualIp(request);
        try {
            user = SecurityUtils.login(username, password, authenticationManager);
            user.setIp(loginIp);

            if (StringUtils.isBlank(terminal)) {
                user.setTerminal("WEB");
            } else {
                user.setTerminal(terminal);
            }
        } catch (AuthenticationException e) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
        }
        if (user == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "用户名或密码错误");
        }
        logService.addUserLog(UserLogEnum.ONLINE, "登录系统成功: " + loginIp);

        return WVPResult.success(user);
    }

    @PostMapping("/changePassword")
    @Operation(summary = "修改密码")
    @Parameter(name = "username", description = "用户名", required = true)
    @Parameter(name = "oldpassword", description = "旧密码（已md5加密的密码）", required = true)
    @Parameter(name = "password", description = "新密码（未md5加密的密码）", required = true)
    public WVPResult<?> changePassword(@RequestParam String oldPassword, @RequestParam String password){
        // 获取当前登录用户id
        LoginUser userInfo = SecurityUtils.getUserInfo();
        if (userInfo== null) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        String username = userInfo.getUsername();
        LoginUser user = null;
        try {
            user = SecurityUtils.login(username, oldPassword, authenticationManager);
            if (user == null) {
                throw new ControllerException(ErrorCode.ERROR100);
            }
            int userId = SecurityUtils.getUserId();
            boolean result = userService.changePassword(userId, DigestUtils.md5DigestAsHex(password.getBytes()));
            if (!result) {
                throw new ControllerException(ErrorCode.ERROR100);
            }
            logService.addUserLog(UserLogEnum.DATA_CONFIG, "修改密码成功");
        } catch (AuthenticationException e) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
        }
        return WVPResult.success();
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户")
    @Parameter(name = "username", description = "用户名", required = true)
    @Parameter(name = "password", description = "密码（未md5加密的密码）", required = true)
    @Parameter(name = "roleId", description = "角色ID", required = true)
    @Parameter(name = "department", description = "部门")
    @Parameter(name = "phone", description = "电话")
    @Parameter(name = "description", description = "描述")
    public WVPResult<?> add(@RequestParam String username,
                    @RequestParam String password,
                    @RequestParam Integer roleId,
                    @RequestParam String department,
                    @RequestParam String phone,
                    @RequestParam String description) {
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password) || roleId == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "参数不可为空");
        }
        if (!SecurityUtils.isAdmin()) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        int currentRoleId = SecurityUtils.getUserInfo().getRole().getId();
        if (!roleId.equals(Role.OPERATOR_ID) && currentRoleId != Role.ADMIN_ID) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setDepartment(department);
        user.setDescription(description);
        user.setPhone(phone);
        Role role = roleService.getRoleById(roleId);

        if (role == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "角色不存在");
        }
        user.setRole(role);
        user.setCreateTime(DateUtil.getNow());
        user.setUpdateTime(DateUtil.getNow());
        int addResult = userService.addUser(user);
        if (addResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        logService.addUserLog(UserLogEnum.DATA_CONFIG, "添加用户：" + user.getUsername());
        return WVPResult.success();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "用户Id", required = true)
    public WVPResult<?> delete(@RequestParam Integer id){
        if (!SecurityUtils.isAdmin()) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        User user = userService.getUser(id);
        if (Objects.isNull(user)) {
            return WVPResult.success();
        }
        int currentRoleId = SecurityUtils.getUserInfo().getRole().getId();
        if (!Objects.equals(user.getRole().getId(), Role.OPERATOR_ID) && currentRoleId != Role.ADMIN_ID) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        int deleteResult = userService.deleteUser(id);
        if (deleteResult <= 0) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        logService.addUserLog(UserLogEnum.DATA_CONFIG, "删除用户：" + user.getUsername());

        return WVPResult.success();
    }

    @GetMapping("/all")
    @Operation(summary = "查询所有用户")
    public WVPResult<List<User>> all(){
        // 获取当前登录用户id
        return WVPResult.success(userService.getAllUsers());
    }

    /**
     * 分页查询用户
     *
     * @param page  当前页
     * @param count 每页查询数量
     * @return 分页用户列表
     */
    @GetMapping("/users")
    @Operation(summary = "分页查询用户")
    @Parameter(name = "page", description = "当前页", required = true)
    @Parameter(name = "count", description = "每页查询数量", required = true)
    public WVPResult<PageInfo<User>> users(int page, int count) {
        return WVPResult.success(userService.getUsers(page, count));
    }

    @PostMapping("/changePasswordForAdmin")
    @Operation(summary = "管理员修改普通用户密码")
    @Parameter(name = "adminId", description = "管理员id", required = true)
    @Parameter(name = "userId", description = "用户id", required = true)
    @Parameter(name = "password", description = "新密码（未md5加密的密码）", required = true)
    public WVPResult<?> changePasswordForAdmin(@RequestParam int userId, @RequestParam String password) {
        // 获取当前登录用户id
        LoginUser userInfo = SecurityUtils.getUserInfo();
        if (userInfo == null) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        User user = userService.getUser(userId);
        if (Objects.isNull(user)) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "用户不存在");
        }
        int currentRoleId = userInfo.getRole().getId();
        if (currentRoleId > Role.ADMIN_ID ||
                (!Objects.equals(user.getRole().getId(), Role.OPERATOR_ID) && currentRoleId != Role.ADMIN_ID)) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        Role role = userInfo.getRole();
        if (role != null) {
            boolean result = userService.changePassword(userId, DigestUtils.md5DigestAsHex(password.getBytes()));
            if (!result) {
                throw new ControllerException(ErrorCode.ERROR100);
            }
        }
        return WVPResult.success();
    }
}
