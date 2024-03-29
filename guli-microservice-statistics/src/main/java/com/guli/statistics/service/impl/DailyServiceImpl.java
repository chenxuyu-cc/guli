package com.guli.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.statistics.client.UcenterClient;
import com.guli.statistics.entity.Daily;
import com.guli.statistics.mapper.DailyMapper;
import com.guli.statistics.service.DailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-10-14
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private UcenterClient ucenterClient;

    @Override
    public void createStatisticsByDay(String day) {

        //删除已存在的统计对象
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_calculated", day);
        baseMapper.delete(queryWrapper);

        //获取统计信息
        Integer registerNum = (Integer) ucenterClient.registerCount(day).getData().get("countRegister");

        Integer loginNum = RandomUtils.nextInt(100, 200);
        Integer videoViewNum = RandomUtils.nextInt(100, 200);
        Integer courseNum = RandomUtils.nextInt(100, 200);

        //创建统计对象
        Daily daily = new Daily();
        daily.setRegisterNum(registerNum);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);

        baseMapper.insert(daily);

    }

    @Override
    public Map<String, Object> getChartData(String begin, String end, String type) {

        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(type,"date_calculated");
        queryWrapper.between("date_calculated",begin,end);

        List<Daily> dayList = baseMapper.selectList(queryWrapper);

        HashMap<String, Object> map = new HashMap<>();
        List<Integer> dataList = new ArrayList<Integer>();
        List<String> dateList = new ArrayList<String>();
        map.put("dataList", dataList);
        map.put("dateList", dateList);

        for (int i = 0; i < dayList.size(); i++) {
            Daily daily = dayList.get(i);
            dateList.add(daily.getDateCalculated());
            switch (type) {
                case "register_num":
                    dataList.add(daily.getRegisterNum());
                    break;
                case "login_num":
                    dataList.add(daily.getLoginNum());
                    break;
                case "video_view_num":
                    dataList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    dataList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }
        }

        return map;
    }

//    @Override
//    public Map<String, Object> getChartDataByList(String begin, String end, String typeList) {
//        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
//
//        queryWrapper.between("date_calculated",begin,end);
//
//        queryWrapper.select(typeList, "date_calculated");
//
//        List<Daily> dayList = baseMapper.selectList(queryWrapper);
//
//        HashMap<String, Object> map = new HashMap<>();
//        List<Integer> dataList = new ArrayList<Integer>();
//        List<String> dateList = new ArrayList<String>();
//        map.put("dataList", dataList);
//        map.put("dateList", dateList);
//
//        for (int i = 0; i < dayList.size(); i++) {
//            Daily daily = dayList.get(i);
//            dateList.add(daily.getDateCalculated());
//            switch (type) {
//                case "register_num":
//                    dataList.add(daily.getRegisterNum());
//                    break;
//                case "login_num":
//                    dataList.add(daily.getLoginNum());
//                    break;
//                case "video_view_num":
//                    dataList.add(daily.getVideoViewNum());
//                    break;
//                case "course_num":
//                    dataList.add(daily.getCourseNum());
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        return map;
//    }
}
