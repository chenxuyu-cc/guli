package com.guli.edu.vo;

import com.guli.edu.entity.Video;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "章节信息")
@Data
public class ChapterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private boolean free;

    private List<VideoVo> children = new ArrayList<>();
}