package com.guli.oss.controller;

import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExceptionUtils;
import com.guli.common.vo.R;
import com.guli.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Api(description="阿里云文件管理")
@CrossOrigin//跨域
@RestController
@RequestMapping("/admin/oss/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public R upLoad(
            @ApiParam(name = "file", value = "文件")
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileHost") String fileHost)  {

        try {

            String uploadUrl = fileService.upload(file, fileHost);
            return R.ok().message("文件上传成功").data("url",uploadUrl);
        } catch (IOException e) {
            //添加异常跟踪信息
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }

    @ApiOperation(value = "文件删除")
    @DeleteMapping("remove")
    public R removeFile(
            @ApiParam(name = "url", value = "要删除的文件路径", required = true)
            @RequestBody String url){

            fileService.removeFile(url);

            return R.ok().message("文件删除成功");

    }
}
