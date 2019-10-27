package com.guli.statistics.client.exception;

import com.guli.common.vo.R;
import com.guli.statistics.client.UcenterClient;
import org.springframework.stereotype.Component;

@Component
public class UcenterClientExceptionHandlerService implements UcenterClient {
    @Override
    public R registerCount(String day) {
        return R.ok().data("countRegister",0);
    }
}
