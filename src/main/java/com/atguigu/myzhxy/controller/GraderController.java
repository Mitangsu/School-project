package com.atguigu.myzhxy.controller;

import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.GradeService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.util.List;

/**
 * @author Su
 * @create 2022-05-11 15:12
 */
@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GraderController {

    @Autowired
    GradeService gradeService;



    @ApiOperation("获取全部年级")
    @GetMapping("getGrades")
    public Result getGrades(){

        List<Grade> list = gradeService.list();

        return Result.ok(list);

    }


    /**
     * 删除功能，包括批量删除
     * @param ids
     * @return
     */
    @ApiOperation("删除Grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(
            @ApiParam("要删除的所有的grader的id的JSON集合") @RequestBody List<Integer> ids){

        gradeService.removeByIds(ids);

        return Result.ok();

    }





    /**
     * 完成修改和添加操作
     * @param grade
     * @return
     */
    @ApiOperation("新增或者修改Grade")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(
            @ApiParam("Json的Grade对象,有id属性是修改,没有则是增加")@RequestBody Grade grade){
        //接收参数
        //调用服务层方法完成增加或者修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }



    @ApiOperation("根据年级名称分页模糊查询")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询模糊匹配的名称")String gradeName
            ){
        //分页 带条件查询
        Page<Grade> page = new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Grade> pageRs = gradeService.getGradeByOpr(page,gradeName);

        //封装Result对象并返回
        return Result.ok(pageRs);

    }


}
