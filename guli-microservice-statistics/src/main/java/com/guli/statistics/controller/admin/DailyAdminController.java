package com.guli.statistics.controller.admin;

import com.guli.common.vo.R;
import com.guli.statistics.service.DailyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyAdminController {

    @Autowired
    private DailyService dailyService;

    @GetMapping("{day}")
    public R createStatisticsByDate(
            @PathVariable String day){

        dailyService.createStatisticsByDay(day);

        return R.ok();
    }

    @GetMapping("show-chart/{begin}/{end}/{type}")
    public R showChart(
            @PathVariable String begin,
            @PathVariable String end,
            @PathVariable String type){

        Map<String,Object> map = dailyService.getChartData(begin,end,type);

        return R.ok().data(map);
    }

//    @GetMapping("show-chart-list/{begin}/{end}/{type}")
//    public R showCharts(
//            @PathVariable String begin,
//            @PathVariable String end,
//            @PathVariable String typeList){
//
//        Map<String,Object> map = dailyService.getChartDataByList(begin,end,typeList);
//
//        return R.ok().data(map);
//    }

}
