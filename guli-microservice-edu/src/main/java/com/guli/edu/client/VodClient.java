package com.guli.edu.client;

import com.guli.common.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient("guli-vod")
public interface VodClient {

//    @DeleteMapping("/admin/vod/video/{videoId}")
//    public R removeVideo(@PathVariable String videoId);

    @DeleteMapping(value = "/admin/vod/video/remove")
    R removeVideoByIdList(@RequestBody List<String> videoSourceIdList);
}
