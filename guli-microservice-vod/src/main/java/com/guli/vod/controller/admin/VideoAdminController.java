package com.guli.vod.controller.admin;

import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.guli.common.vo.R;
import com.guli.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description="阿里云视频点播微服务")
@RestController
@CrossOrigin
@RequestMapping("/admin/vod/video")
public class VideoAdminController {

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频")
    @PostMapping("upload")
    public R uploadVideo(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file){

        String videoId = videoService.uploadVideo(file);

        return R.ok().message("视频上传成功").data("videoId", videoId);
    }

//    @ApiOperation(value = "删除上传视频")
//    @DeleteMapping("{videoId}")
//    public R removeVideo(
//            @ApiParam(name = "videoId", value = "视频id", required = true)
//            @PathVariable String videoId){
//
//        System.out.println("调用。。。。。。");
//
//        videoService.removeVideo(videoId);
//
//        return R.ok().message("删除视频成功");
//    }

    @ApiOperation(value = "删除上传视频")
    @DeleteMapping("remove")
    public R removeVideoByIdList(
            @ApiParam(name = "videoSourceId", value = "阿里云视频文件的id", required = true)
            @RequestBody List<String> videoSourceIdList){

//        System.out.println("调用。。。。。。");

        videoService.removeVideoByIdList(videoSourceIdList);

        return R.ok().message("删除视频成功");
    }

    @ApiOperation(value = "获取视频上传地址和凭证")
    @GetMapping("get-upload-auth-and-address/{title}/{fileName}")
    public R getUploadAuthAndAddress(
            @ApiParam(name = "title", value = "视频标题", required = true)
            @PathVariable String title,

            @ApiParam(name = "fileName", value = "视频源文件名", required = true)
            @PathVariable String fileName){
        CreateUploadVideoResponse response = videoService.getUploadAuthAndAddress(title, fileName);
        return R.ok().message("获取视频上传地址和凭证成功").data("response", response);
    }

    @ApiOperation(value = "刷新视频上传地址和凭证")
    @GetMapping("refresh-upload-auth-and-address/{videoId}")
    public R refreshUploadAuthAndAddress(
            @ApiParam(name = "videoId", value = "云端视频id", required = true)
            @PathVariable String videoId){

        RefreshUploadVideoResponse response =  videoService.refreshUploadAuthAndAddress(videoId);

        return R.ok().message("刷新视频上传地址和凭证成功").data("response", response);
    }
}
