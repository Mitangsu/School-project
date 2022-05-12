package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.service.StudentService;
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
 * @create 2022-05-11 15:12
 */
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    StudentService studentService;



    @ApiOperation("删除Student信息")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @ApiParam("要删除的所有的Student的id的JSON集合") @RequestBody List<Integer> ids){

        studentService.removeByIds(ids);

        return Result.ok();

    }



    @ApiOperation("新增或者修改Student")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("要保存或修改的学生JSON")@RequestBody Student student){

        Integer id = student.getId();
        if (null == id || 0 == id) {
            student.setPassword(MD5.encrypt(student.getPassword()));
        }

        studentService.saveOrUpdate(student);

        return Result.ok();
    }



    @ApiOperation("分页带条件查询学生信息")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("页码数")@PathVariable("pageNo") Integer pageNo,
            @ApiParam("页大小")@PathVariable("pageSize") Integer pageSize,
            Student student
    ){
        Page<Student> pageParam = new Page<>();

        IPage<Student> studentIPage =studentService.getStudentByOp(pageParam,student);


        return Result.ok(studentIPage);
    }
}
