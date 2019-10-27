package com.guli.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExceptionUtils;
import com.guli.vod.service.VideoService;
import com.guli.vod.util.AliyunVodSDKUtils;
import com.guli.vod.util.ConstantPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Override
    public String uploadVideo(MultipartFile file) {

        String filename = file.getOriginalFilename();
        String title = filename.substring(0, filename.lastIndexOf("."));

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }

        UploadStreamRequest request = new UploadStreamRequest(
                ConstantPropertiesUtil.ACCESS_KEY_ID,
                ConstantPropertiesUtil.ACCESS_KEY_SECRET,
                title,
                filename,
                inputStream);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);

        String videoId = response.getVideoId();
        //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
        // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
        if(!response.isSuccess()){
            if(StringUtils.isEmpty(videoId))
                throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }

//    @Override
//    public void removeVideo(String videoId) {
//
//        DefaultAcsClient client = null;
//
//        try {
//            client =  AliyunVodSDKUtils.initVodClient(
//                    ConstantPropertiesUtil.ACCESS_KEY_ID,
//                    ConstantPropertiesUtil.ACCESS_KEY_SECRET
//            );
//
//            DeleteVideoRequest request = new DeleteVideoRequest();
//            request.setVideoIds(videoId);
//
//            DeleteVideoResponse response = client.getAcsResponse(request);
//
//        } catch (ClientException e) {
//            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
//        }
//    }

    @Override
    public CreateUploadVideoResponse getUploadAuthAndAddress(String title, String fileName) {


        try {
            //初始化
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET
            );

            //创建请求对象
            CreateUploadVideoRequest request = new CreateUploadVideoRequest();
            request.setTitle(title);
            request.setFileName(fileName);

            //获取响应
            CreateUploadVideoResponse response = client.getAcsResponse(request);

            return response;

        }catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.FETCH_VIDEO_UPLOAD_PLAYAUTH_ERROR);
        }
    }

    @Override
    public RefreshUploadVideoResponse refreshUploadAuthAndAddress(String videoId) {

        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
            request.setVideoId(videoId);

            RefreshUploadVideoResponse response = client.getAcsResponse(request);

            return response;

        }catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.FETCH_VIDEO_UPLOAD_PLAYAUTH_ERROR);
        }
    }


    @Override
    public String getVideoPlayAuth(String videoSourceId) {

        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoSourceId);

        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET
            );

            GetVideoPlayAuthResponse response = client.getAcsResponse(request);

            String playAuth = response.getPlayAuth();

            return playAuth;

        }catch (ClientException e){
            throw new GuliException(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
        }
    }


    @Override
    public void removeVideoByIdList(List<String> videoSourceIdList) {

        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            DeleteVideoRequest request = new DeleteVideoRequest();

            int size = videoSourceIdList.size();
            StringBuffer idListStr = new StringBuffer();
            for (int i = 0; i < size; i++) {
                idListStr.append(videoSourceIdList.get(i));
                if(i == size - 1 || i % 20 ==19){
                    System.out.println("idListStr = " + idListStr.toString());

                    request.setVideoIds(idListStr.toString());
                    DeleteVideoResponse acsResponse = client.getAcsResponse(request);
                    System.out.println("requestId = " + acsResponse.getRequestId());

                    idListStr = new StringBuffer();
                    System.out.println("idListStr empty = " + idListStr);
                }else if(i % 20 < 19){
                    idListStr.append(",");
                }
            }
        } catch (ClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }
}
