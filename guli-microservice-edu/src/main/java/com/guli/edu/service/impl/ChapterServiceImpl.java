package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.edu.entity.Chapter;
import com.guli.edu.entity.Video;
import com.guli.edu.mapper.ChapterMapper;
import com.guli.edu.mapper.VideoMapper;
import com.guli.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.ChapterVo;
import com.guli.edu.vo.VideoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {

        QueryWrapper<Video> queryWrapper = new QueryWrapper();
        queryWrapper.eq("chapter_id", id);
        videoMapper.delete(queryWrapper);

        Integer result = baseMapper.deleteById(id);

        return null != result && result >= 0;
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {

        //定义一个最终要得到的数据
        ArrayList<ChapterVo> chapterVoArrayList = new ArrayList<>();

        //获取章节信息
        QueryWrapper<Chapter> queryWrapperChapter = new QueryWrapper<>();
        queryWrapperChapter.eq("course_id",courseId);
        queryWrapperChapter.orderByAsc("sort","id");
        List<Chapter> chapters = baseMapper.selectList(queryWrapperChapter);

        //获取课时信息
        QueryWrapper<Video> queryWrapperVideo = new QueryWrapper<>();
        queryWrapperVideo.eq("course_id",courseId);
        queryWrapperVideo.orderByAsc("sort","id");
        List<Video> videos = videoMapper.selectList(queryWrapperVideo);

        //遍历信息，充填章节vo数据
        for (int i = 0; i <chapters.size() ; i++) {
            Chapter chapter = chapters.get(i);

            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter,chapterVo);
            chapterVoArrayList.add(chapterVo);

            //遍历信息，充填视频vo数据
            ArrayList<VideoVo> videoVoArrayList = new ArrayList<>();
            for (int j = 0; j <videos.size() ; j++) {
                Video video = videos.get(j);

                if(chapter.getId().equals(video.getChapterId())) {

                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVoArrayList.add(videoVo);
                }
            }

            chapterVo.setChildren(videoVoArrayList);
        }

        return chapterVoArrayList;
    }
}
