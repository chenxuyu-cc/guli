package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.utils.ExcelImportUtil;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo;
import com.guli.edu.vo.SubjectVo2;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-09-24
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Transactional
    @Override
    public List<String> batchImport(MultipartFile file) throws Exception {

        //定义错误信息列表
        List<String> errorMsg = new ArrayList<>();
        //获取文件上传流
        InputStream inputStream = file.getInputStream();
        //使用Excel导入工具类
        ExcelImportUtil excelImportUtil = new ExcelImportUtil(inputStream);
        //获得sheet
        HSSFSheet sheet = excelImportUtil.getSheet();
        //获去数据行数
        int rowCount = sheet.getPhysicalNumberOfRows();
        //若只有标题返回错误信息
        if(rowCount <=1 ){
            errorMsg.add("请填写数据");
            return errorMsg;
        }
        //遍历其他列
        for (int rowNum = 1; rowNum < rowCount ; rowNum ++) {
            HSSFRow rowData = sheet.getRow(rowNum);
            //判断数据行的数据是否为空
            if(rowData != null){
                //从Excel中获取一级分类
                HSSFCell levelOneCell = rowData.getCell(0);
                //判断一级分类是否为空
                String levelOneValue="";
                if(levelOneCell != null){
                    //trim()去数据的前后空格
                    levelOneValue = excelImportUtil.getCellValue(levelOneCell).trim();
                    //判断一级分类数据是否为空
                    if(StringUtils.isEmpty(levelOneValue)){
                        errorMsg.add("第" + rowNum + "行一级分类为空");
                        continue;
                    }
                }
                //判断一级分类是否存在
                Subject subject = this.getByTitle(levelOneValue);
                String parentId="";
                if(subject == null){
                    //若不存在则插入数据
                    Subject subjectLevelOne = new Subject();
                    subjectLevelOne.setTitle(levelOneValue);
                    subjectLevelOne.setSort(rowNum);
                    baseMapper.insert(subjectLevelOne);

                    //获取二级分类的parent_id
                    parentId = subjectLevelOne.getId();
                }else{
                    //若查到了就把它的id作为parentId
                    parentId = subject.getId();
                }

                HSSFCell levelTowCell = rowData.getCell(1);
                String levelTowValue = "";
                if(levelTowCell != null){
                    levelTowValue  = excelImportUtil.getCellValue(levelTowCell).trim();
                    if(StringUtils.isEmpty(levelTowValue)){
                        errorMsg.add("第" + rowNum + "行二级分类为空");
                        continue;
                    }
                }
                Subject subjectSub = this.getSubgetByTitle(levelTowValue,parentId);
                Subject subjectLevelTwo = null;
                if(subjectSub == null){
                    subjectLevelTwo = new Subject();
                    subjectLevelTwo.setTitle(levelTowValue);
                    subjectLevelTwo.setSort(rowNum);
                    subjectLevelTwo.setParentId(parentId);
                    baseMapper.insert(subjectLevelTwo);
                }
            }

        }

        return errorMsg;
    }

    //区别在于方法一不用写mapper.xml配置文件
    //方法一
    @Override
    public List<SubjectNestedVo> nestedList() {

        //最终要的到的数据列表
        ArrayList<SubjectNestedVo> subjectNestedVoArrayList = new ArrayList<>();

        //获取一级分类数据记录
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        queryWrapper.orderByAsc("sort", "id");
        List<Subject> subjects = baseMapper.selectList(queryWrapper);

        //获取二级分类数据记录 TODO
        QueryWrapper<Subject> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.ne("parent_id", 0);
        queryWrapper2.orderByAsc("sort", "id");
        List<Subject> subSubjects = baseMapper.selectList(queryWrapper2);

        //填充一级分类vo数据
        int count = subjects.size();
        for (int i = 0; i < count; i++) {
            Subject subject = subjects.get(i);

            //创建一级类别vo对象
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
            BeanUtils.copyProperties(subject, subjectNestedVo);
            subjectNestedVoArrayList.add(subjectNestedVo);

            //填充二级分类vo数据 TODO
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            //SubjectVo subjectVo = new SubjectVo();
            int count2 = subSubjects.size();
            for (int j = 0; j < count2; j++) {

                Subject subSubject = subSubjects.get(j);
                if(subject.getId().equals(subSubject.getParentId())){

                    //创建二级类别vo对象
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subSubject, subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }
            subjectNestedVo.setChildren(subjectVoArrayList);
        }

        return subjectNestedVoArrayList;
    }

//    //方法二
//    @Override
//    public List<SubjectVo2> nestedList2() {
//        return baseMapper.selectNestedListByParentId("0");
//    }

    //判断某级分类是否存在
    private Subject getByTitle(String title){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",0);
        return baseMapper.selectOne(queryWrapper);
    }

    //判断某级分类是否存在
    private  Subject getSubgetByTitle(String title,String parentId){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
