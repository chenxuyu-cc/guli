package com.guli.edu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.vo.R;
import com.guli.edu.entity.Course;
import com.guli.edu.entity.Teacher;
import com.guli.edu.service.CourseService;
import com.guli.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@CrossOrigin //跨域
@Api(description="讲师模块")
@RestController
@RequestMapping("/edu/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "讲师列表")
    @GetMapping
    public R list(){

        List<Teacher> list = teacherService.list(null);
        return  R.ok().data("items", list);
    }
    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping(value = "{teacherId}")
    public R getById(
            @ApiParam(name = "teacherId", value = "讲师ID", required = true)
            @PathVariable String teacherId){

        //查询讲师信息
        Teacher teacher = teacherService.getById(teacherId);

        return R.ok().data("teacher", teacher);
    }

    @ApiOperation(value = "分页讲师列表")
    @GetMapping("{page}/{limit}")
    public R getPageList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable Long page,
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable Long limit){

        Page<Teacher> pageParam = new Page(page,limit);
        Map<String,Object> map = teacherService.pageListWeb(pageParam);

        return R.ok().data(map);
    }

    @ApiOperation(value = "根据讲师id查询这个讲师的课程列表")
    @GetMapping("tea/{id}")
    public R pageList(
            @ApiParam(name = "id", value = "讲师id", required = true)
            @PathVariable String id){

        Teacher teacher = teacherService.getById(id);

        List<Course> courseList = courseService.selectByTeacher(id);

        return  R.ok().data("teacher",teacher).data("courseList",courseList);
    }
}

