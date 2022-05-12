package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Admin;
import com.atguigu.myzhxy.pojo.LoginForm;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.pojo.Teacher;
import com.atguigu.myzhxy.service.AdminService;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.service.TeacherService;
import com.atguigu.myzhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.corba.se.impl.oa.toa.TOA;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Su
 * @create 2022-05-11 15:23
 */
@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    AdminService adminService;

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    /*
    * 修改密码的处理器
    * POST  /sms/system/updatePwd/123456/admin
    *       /sms/system/updatePwd/{oldPwd}/{newPwd}
    *       请求参数
                oldpwd
                newPwd
                token 头
            响应的数据
                Result OK data= null
    * */
    @ApiOperation("更新用户密码的处理器")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @ApiParam("token口令") @RequestHeader("token") String token,
            @ApiParam("旧密码") @PathVariable("oldPwd") String oldPwd,
            @ApiParam("新密码") @PathVariable("newPwd") String newPwd
    ) {
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration) {
            // token过期
            return Result.fail().message("token失效,请重新登录后修改密码");
        }
        // 获取用户ID和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);

        switch (userType) {
            case 1:
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("id", userId.intValue());
                queryWrapper.eq("password", oldPwd);
                Admin admin = adminService.getOne(queryWrapper);
                if (admin != null) {
                    // 修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                } else {
                    return Result.fail().message("原密码有误!");
                }
                break;
            case 2:
                QueryWrapper<Student> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("id", userId.intValue());
                queryWrapper2.eq("password", oldPwd);
                Student student = studentService.getOne(queryWrapper2);
                if (student != null) {
                    // 修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                } else {
                    return Result.fail().message("原密码有误!");
                }
                break;
            case 3:
                QueryWrapper<Teacher> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.eq("id", userId.intValue());
                queryWrapper3.eq("password", oldPwd);
                Teacher teacher = teacherService.getOne(queryWrapper3);
                if (teacher != null) {
                    // 修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                } else {
                    return Result.fail().message("原密码有误!");
                }
                break;
        }
        return Result.ok();
    }





    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("头像文件") @RequestPart("multipartFile")  MultipartFile multipartFile
            ,
            HttpServletRequest request
    ){

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName =uuid.concat(originalFilename.substring(i));

        //保存文件 将文件发送到第三方/独立的图片服务器,
        String portraitPath ="D:\\workspace_idea\\zhxy\\target\\classes\\public\\upload\\".concat(newFileName);
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 响应图片的路径
        String path="upload/".concat(newFileName);
        return Result.ok(path);
    }



    /**
     * 跳转到页面
     * @param token
     * @return
     */
    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析出用户的id和用户的类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String, Object> map = new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;

}

        return Result.ok(map);
    }

    /**
     * 登录相关验证
     * @param loginForm
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm,HttpSession session){
        //验证码校验
        String sessionverifiCode = (String) session.getAttribute("verifiCode");
        String loginverifiCode = loginForm.getVerifiCode();
        if ("".equals(sessionverifiCode)||null==sessionverifiCode){
            return Result.fail().message("验证码失效,请刷新后重试");
        }
        if (!(sessionverifiCode.equalsIgnoreCase(loginverifiCode))){
            return Result.fail().message("验证码有误,请重试");
        }
        //从session域中移除现有验证码
        session.removeAttribute("verifiCode");

        //准备一个map用户存放响应的数据
        Map<String,Object> map = new LinkedHashMap<>();

        //分用户类型进行校验
        switch (loginForm.getUserType()){
            case 1:
                try {

                    Admin admin = adminService.login(loginForm);
                    if (admin != null){
                        //用户的类型和用户id转换成一个密文,以token的名称向客户端反馈
                        String token = JwtHelper.createToken(admin.getId().longValue(), 1);
                        map.put("token",token);
                    }else{
                        throw  new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 2:
                try {

                   Student student = studentService.login(loginForm);
                    if (student != null){
                        //用户的类型和用户id转换成一个密文,以token的名称向客户端反馈
                        String token = JwtHelper.createToken(student.getId().longValue(), 2);
                        map.put("token",token);
                    }else{
                        throw  new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {

                    Teacher teacher = teacherService.login(loginForm);
                    if (teacher != null){
                        //用户的类型和用户id转换成一个密文,以token的名称向客户端反馈
                        String token = JwtHelper.createToken(teacher.getId().longValue(), 3);
                        map.put("token",token);
                    }else{
                        throw  new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }
        return Result.fail().message("查无此用户");

    }

    /**
     * 获取验证码图片
     * @param session
     * @param response
     */
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpSession session, HttpServletResponse response){
        //获取图片
        BufferedImage image = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入session域，为下一步验证做准备
        session.setAttribute("verifiCode",verifiCode);
        //将验证码图片响应给浏览器
        try {
            ImageIO.write(image,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
