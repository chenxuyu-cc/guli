package com.guli.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.guli.oss.service.FileService;
import com.guli.oss.util.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file, String fileHost) throws IOException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtils.END_POINT;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        //判断存储空间是否存在
        if(!ossClient.doesBucketExist(bucketName)){
            ossClient.createBucket(bucketName);
            //设置权限
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        String filePath = new DateTime().toString("yyyy/MM/dd");
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString()+ originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileUrl = fileHost + "/" + filePath + "/" + fileName;

        //文件上传
        InputStream inputStream = file.getInputStream();
        ossClient.putObject(bucketName, fileUrl, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        return "http://" + bucketName + "." + endpoint + "/" + fileUrl;
    }

    @Override
    public void removeFile(String url) {

        String endPoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        String host = "https://" + bucketName + "." + endPoint + "/";
        String objectName = url.substring(host.length());

        ossClient.deleteObject(bucketName,objectName);

        ossClient.shutdown();
    }
}
