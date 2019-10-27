package com.guli.statistics.client;

import com.guli.common.vo.R;
import com.guli.statistics.client.exception.UcenterClientExceptionHandlerService;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "guli-ucenter",fallback= UcenterClientExceptionHandlerService.class)
public interface UcenterClient {

    @GetMapping(value = "/admin/ucenter/member/count-register/{day}")
    public R registerCount(
            @PathVariable("day") String day);
}
