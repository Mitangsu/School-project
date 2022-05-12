package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Clazz;
import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.ClazzService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Su
 * @create 2022-05-11 15:12
 */
@Api("班级管理器")
@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    ClazzService clazzService;


    @ApiOperation("查询所有班级信息")
    @GetMapping("getClazzs")
    public Result getClazzs(){
        List<Clazz> list = clazzService.list();
        return Result.ok(list);
    }

    @ApiOperation("删除单个或多个班级信息")
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(
            @ApiParam("要删除的多个班级的ID的JSON数组")@RequestBody List<Integer> ids){

        clazzService.removeByIds(ids);
        return Result.ok();
    }



    @ApiOperation("添加或者修改班级信息")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(
            @ApiParam("JSON执行格式的班级信息")@RequestBody Clazz clazz
    ){
            clazzService.saveOrUpdate(clazz);
            return Result.ok();
    }




    //getClazzsByOpr/1/3?gradeName=&name=
    @ApiOperation("分页查询")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzByOpr(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询的查询条件") Clazz clazz){

        Page<Clazz> page = new Page<>(pageNo,pageSize);

        IPage<Clazz> pageRs = clazzService.getClazzByOpr(page,clazz);

        return Result.ok(pageRs);
    }





}
