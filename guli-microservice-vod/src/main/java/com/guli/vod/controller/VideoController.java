package com.guli.vod.controller;

import com.guli.common.vo.R;
import com.guli.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description="阿里云视频点播微服务")
@CrossOrigin //跨域
@RestController
@RequestMapping("/vod/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("通过videoId获取凭证")
    @GetMapping("get-play-auth/{videoSourceId}")
    public R getVideoPlayAuth(
            @PathVariable("videoSourceId") String videoSourceId){

        String playAuth = videoService.getVideoPlayAuth(videoSourceId);

        return R.ok().message("获取凭证成功").data("playAuth",playAuth);
    }
}
