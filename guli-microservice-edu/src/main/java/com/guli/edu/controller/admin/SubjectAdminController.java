package com.guli.edu.controller.admin;


import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExceptionUtils;
import com.guli.common.vo.R;
import com.guli.edu.service.SubjectService;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@RestController
@RequestMapping("/admin/edu/subject")
@Api(description = "课程分类管理")
@CrossOrigin
@Slf4j
public class SubjectAdminController {

    @Autowired
    private SubjectService subjectService;

    @ApiOperation(value = "Excel导入课程分类数据")
    @PostMapping("import")
    public R batchImport(
            @ApiParam(name="file" ,value="Excel文件",required = true)
            @RequestParam("file") MultipartFile file){

        try {
            List<String> errorMsg = subjectService.batchImport(file);
            if(errorMsg.size() == 0){
                return R.ok().message("批量导入成功");
            }else{
                return R.error().message("部分数据批量导入失败").data("errorMsgList",errorMsg);
            }
        } catch (Exception e) {
           log.error(ExceptionUtils.getMessage(e));
           throw new GuliException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
        }

    }
    //获取列表第一种方法
    @ApiOperation(value = "嵌套数据列表")
    @GetMapping("")
    public R nestedList(){

        List<SubjectNestedVo> subjectNestedVoList = subjectService.nestedList();
        return R.ok().data("items", subjectNestedVoList);
    }

//    //获取列表第二种方法
//    @ApiOperation(value = "嵌套数据列表2")
//    @GetMapping("list2")
//    public R nestedList2(){
//        List<SubjectVo2> subjectVo2List = subjectService.nestedList2();
//        return R.ok().data("items", subjectVo2List);
//    }
}

