package com.guli.edu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.common.vo.R;
import com.guli.edu.entity.Teacher;
import com.guli.edu.query.TeacherQuery;
import com.guli.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@Api(description="讲师管理")
@RestController
@RequestMapping("admin/edu/teacher")

public class TeacherAdminController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation(value = "所有讲师")
    @GetMapping
    public R list(){
        List<Teacher> list = teacherService.list(null);
        return R.ok().data("items",list).message("获取列表成功");
    }

    @ApiOperation(value = "通过id删除")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id",value = "讲师id",required = true)
            @PathVariable String id){

//        Teacher teacher = teacherService.getById(id);
//        System.out.println(teacher);


        boolean result = teacherService.removeById(id);
        if(result){
           return R.ok().message("删除讲师成功");
        }else {
           return R.error().message("删除讲师失败");
        }
    }
    @ApiOperation(value = "通过id列表批量删除")
    @DeleteMapping()
    public R removeByIdList(
            @ApiParam(name = "idList",value = "讲师id",required = true)
            @RequestBody List<String> idList){

        boolean result = teacherService.removeByIds(idList);
        if(result){
            return R.ok().message("删除讲师成功");
        }else {
            return R.error().message("删除讲师失败");
        }
    }

    @ApiOperation(value = "分页讲师列表")
    @GetMapping("{page}/{limit}")
    public R pageList(
            @ApiParam(name = "page",value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "teacherQuery", value = "查询对象", required = false)
            TeacherQuery teacherQuery){

        Page<Teacher> pageParam = new Page<>(page,limit);

        teacherService.pageQuery(pageParam,teacherQuery);

        List<Teacher> records = pageParam.getRecords();
        long total = pageParam.getTotal();

        return R.ok().data("total",total).data("rows",records);

    }

    @ApiOperation(value = "新增讲师")
    @PostMapping
    public R save(
            @ApiParam(name = "teacher", value = "讲师对象", required = true)
            @RequestBody Teacher teacher){
        teacherService.save(teacher);
        return R.ok();
    }

    @ApiOperation(value = "通过id查询")
    @GetMapping("{id}")
    public R getById(@ApiParam(name = "id",value = "讲师id",required = true)
                         @PathVariable String id){
        Teacher teacher = teacherService.getById(id);

        return R.ok().data("item",teacher);
    }

    @ApiOperation(value = "根据ID修改讲师")
    @PutMapping("{id}")
    public R updateById(
            @ApiParam(name = "id",value = "讲师id",required = true)
            @PathVariable String id,

            @ApiParam(name = "teacher", value = "讲师对象", required = true)
            @RequestBody Teacher teacher){

        teacher.setId(id);
        teacherService.updateById(teacher);
        return R.ok();
    }

    @ApiOperation(value = "根据关键字查询讲师")
    @GetMapping("name/{key}")
    public R selectNameListByKey(
            @ApiParam(name = "key",value = "讲师姓名关键字",required = true)
            @PathVariable String key){

        List<Map<String, Object>> list = teacherService.selectNameListByKey(key);
        list.get(0).keySet();
        return R.ok().data("nameList",list);
    }
}
