package com.guli.edu.controller.admin;


import com.guli.common.vo.R;
import com.guli.edu.entity.Video;
import com.guli.edu.form.VideoInfoForm;
import com.guli.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@RestController
@RequestMapping("/admin/edu/video")
@Api(description = "章节管理")
@CrossOrigin
@Slf4j
public class VideoAdminController {

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "新增课程")
    @PostMapping("save-video-info")
    public R save(
            @ApiParam(name = "videoForm", value = "课时对象",required = true)
            @RequestBody VideoInfoForm videoInfoForm ){

        videoService.saveVideoInfo(videoInfoForm);
        return R.ok();
    }

    @ApiOperation(value = "获取课程")
    @GetMapping("select-video-info/{id}")
    public R getById(
            @ApiParam(name = "id", value = "课时id",required = true)
            @PathVariable String id ){

        VideoInfoForm videoInfoForm  = videoService.getVideoInfoFormById(id);

        return R.ok().data("item",videoInfoForm);
    }

    @ApiOperation(value = "更改课程")
    @PutMapping("update-video-info/{id}")
    public R updateById(
            @ApiParam(name = "id", value = "课时id",required = true)
            @PathVariable String id,

            @ApiParam(name = "videoForm", value = "课时对象",required = true)
            @RequestBody VideoInfoForm videoInfoForm ){

        videoService.updateVideoInfoFormById(videoInfoForm);

        return R.ok();
    }

    @ApiOperation(value = "删除课程")
    @DeleteMapping("{id}")
    public R removeById(
            @ApiParam(name = "id", value = "课时id",required = true)
            @PathVariable String id ){

        videoService.removeVideoInfoFormById(id);

        return R.ok();
    }
}

