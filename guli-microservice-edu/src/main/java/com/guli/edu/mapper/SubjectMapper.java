package com.guli.edu.mapper;

import com.guli.edu.entity.Subject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guli.edu.vo.SubjectVo2;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@Repository
public interface SubjectMapper extends BaseMapper<Subject> {
    List<SubjectVo2> selectNestedListByParentId(String parentId);
}
