package com.css.cvds.cmu.web.user;

import com.css.cvds.cmu.service.IRoleService;
import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name  = "角色管理")
@CrossOrigin
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping("/all")
    @Operation(summary = "查询角色")
    public WVPResult<List<Role>> all(){
        return WVPResult.success(roleService.getAll());
    }
}
