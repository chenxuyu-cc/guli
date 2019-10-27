package com.guli.edu.client;

import com.guli.common.vo.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient("guli-oss")
public interface OssClient {

    @DeleteMapping("/admin/oss/file/remove")
    public R removeFile(@RequestBody String url);


}
