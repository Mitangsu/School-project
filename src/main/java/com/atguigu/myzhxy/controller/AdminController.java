package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Admin;
import com.atguigu.myzhxy.service.AdminService;
import com.atguigu.myzhxy.util.MD5;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Su
 * @create 2022-05-11 15:11
 */
@RestController
@RequestMapping("/sms/adminController")
public class AdminController {


    @Autowired
    AdminService adminService;

    //GET
    //getAlladmin/1/3?adminName=
    @ApiOperation("分页待条件查询管理员信息")
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAllAdmin(
            @ApiParam("页码数")@PathVariable() Integer pageNo,
            @ApiParam("页大小")@PathVariable() Integer pageSize,
            String adminName
    ){
        Page<Admin> page = new Page<>();
        IPage<Admin> pageRs = adminService.getAdMINByOp(page,adminName);
        return Result.ok(pageRs);
    }

    //POST
    //SaveOrUpdateAdmin
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(
            @ApiParam("JSON格式的Admin对象")@RequestBody Admin admin
    ){
        Integer id = admin.getId();
        if (id == null || 0 == id){
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);

        return Result.ok();
    }



    //DELETE
    //deleteAdmin
    @ApiOperation("删除Admin信息")
    @DeleteMapping("/deleteAdmin")
    public Result deleteAdmin(
            @ApiParam("要删除的所有的管理员的id的JSON集合") @RequestBody List<Integer> ids){

        adminService.removeByIds(ids);

        return Result.ok();

    }


}
