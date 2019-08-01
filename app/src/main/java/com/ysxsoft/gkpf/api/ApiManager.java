package com.ysxsoft.gkpf.api;

import com.ysxsoft.gkpf.bean.request.BaseRequest;
import com.ysxsoft.gkpf.bean.request.CacheRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmAskRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmExamAckRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmExamStartRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmFileRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmReplaceRequest;
import com.ysxsoft.gkpf.bean.request.ConfirmTaskStateRequest;
import com.ysxsoft.gkpf.bean.request.DiffRequest;
import com.ysxsoft.gkpf.bean.request.LoginRequest;
import com.ysxsoft.gkpf.bean.request.LogoutRequest;
import com.ysxsoft.gkpf.bean.request.TotalGradeRequest;
import com.ysxsoft.gkpf.bean.request.UploadScoreRequest;
import com.ysxsoft.gkpf.utils.ShareUtils;

import java.util.List;

public class ApiManager {
    //接口访问域名
    public static String HOST = "https://xsjzhd.ysxapp.com:8080/jeecg-boot";

    ///////////////////////////////////////////////////////////////////////////
    // Message
    ///////////////////////////////////////////////////////////////////////////
    public static final short MSG_MANUALSCORE_START = 1;                   //!< 人工评分相关消息类型定义开始
    public static final short MSG_SOCKET_HEARTBEAT = 2;                    //!< 心跳发送
    public static final short MSG_SOCKET_HEARTBEAT_FEEDBACK = 3;           //!< 心跳接收反馈
    public static final short MSG_MANUALSCORE_LOGIN_REQUEST = 10;          //!< 登录请求
    public static final short MSG_MANUALSCORE_LOGIN_REPLY = 11;            //!< 登录请求反馈
    public static final short MSG_MANUALSCORE_LOGINOUT_REQUEST = 12;       //!< 登出请求
    public static final short MSG_MANUALSCORE_LOGINOUT_REPLY = 13;         //!< 登出请求反馈
    public static final short MSG_MANUALSCORE_EXAMSTART_NOTIFY = 14;       //!< 考试开始通知
    public static final short MSG_MANUALSCORE_EXAMSTART_ACK = 15;          //!< 考试开始接收确认
    public static final short MSG_MANUALSCORE_CACHE_REQUEST = 18;          //!< 评分缓存请求
    public static final short MSG_MANUALSCORE_CACHE_REPLY = 19;            //!< 评分缓存反馈
    public static final short MSG_MANUALSCORE_FILESEND = 20;               //!< 评分表文件发送
    public static final short MSG_MANUALSCORE_FILESEND_ACK = 21;           //!< 文件接收成功确认
    public static final short MSG_MANUALSCORE_REPLACE_NOTIFY = 33;         //!< 占位符替换通知
    public static final short MSG_MANUALSCORE_REPLACE_ACK = 34;            //!< 占位符接收确认
    public static final short MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY = 35;   //!< 任务状态通知
    public static final short MSG_MANUALSCORE_TASKLISTSTATE_ACK = 36;      //!< 任务状态接收确认
    public static final short MSG_MANUALSCORE_ASK_NOTIFY = 37;             //!< 问询提醒通知
    public static final short MSG_MANUALSCORE_ASK_ACK = 38;                //!< 问询提醒接收确认
    public static final short MSG_MANUALSCORE_UPLOADSCORE_REQUEST = 39;    //!< 上传分数请求
    public static final short MSG_MANUALSCORE_UPLOADSCORE_REPLY = 40;      //!< 上传分数反馈
    public static final short MSG_MANUALSCORE_EXAMEND_NOTIFY = 41;         //!< 考试结束通知
    public static final short MSG_MANUALSCORE_EXAMEND_ACK = 42;            //!< 考试结束接收确认
    public static final short MSG_MANUALSCORE_DIFFJUDGMENTS_REQUEST = 50;  //!< 评分结果不同请求
    public static final short MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY = 51;    //!< 评分结果不同反馈
    public static final short MSG_TOTALGRADEFORM_REQUEST = 60;             //!< 总成绩单请求
    public static final short MSG_TOTALGRADEFORM_REPLY = 61;               //!< 总成绩单反馈
    public static final short MSG_MANUALSCORE_INSTRUCTION_NOTIFY = 70;     //!< 评分说明通知
    public static final short MSG_MANUALSCORE_INSTRUCTION_ACK = 71;        //!< 评分说明通知确认
    public static final short MSG_MANUALSCORE_END = 100;                   //!< 定义结束

    /**
     * 登录接口
     *
     * @param name
     * @param pwd
     */
    public static void login(String name, String pwd, String groupId) {
        LoginRequest request = new LoginRequest();
        request.setUserName(name);
        request.setPassword(pwd);
        request.setGroupId(groupId);
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_LOGIN_REQUEST, request);
    }

    /**
     * 登出接口
     *
     */
    public static void logout() {
        LogoutRequest request = new LogoutRequest();
        request.setUserName(ShareUtils.getUserName());
        request.setPassword(ShareUtils.getUserPwd());
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_LOGINOUT_REQUEST, request);
    }

    /**
     * 考试开始接收确认
     * @param missionId
     */
    public static void confirmExamStart(String missionId){
        ConfirmExamStartRequest request=new ConfirmExamStartRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_EXAMSTART_ACK, request);
    }

    /**
     * 请求缓存
     *
     */
    public static void cache() {
        CacheRequest request = new CacheRequest();
        request.setUserName(ShareUtils.getUserName());
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_CACHE_REQUEST, request);
    }

    /**
     * 评分表文件接收完毕确认
     * @param fileName
     */
    public static void confirmFile(String fileName) {
        ConfirmFileRequest request = new ConfirmFileRequest();
        request.setFileName(fileName);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_FILESEND_ACK, request);
    }

    /**
     * 占位符替换数据接收确认
     * @param flowNameList
     */
    public static void confirmReplace(List<String> flowNameList) {
        ConfirmReplaceRequest request = new ConfirmReplaceRequest();
        request.setFlowNameList(flowNameList);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_REPLACE_ACK, request);
    }

    /**
     * 任务状态变化收到确认
     * @param missionId
     */
    public static void confirmTaskState(String missionId) {
        ConfirmTaskStateRequest request = new ConfirmTaskStateRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_ACK, request);
    }

    /**
     * 问询通知接收确认
     * @param flowName  流程名
     */
    public static void confirmAsk(String flowName) {
        ConfirmAskRequest request = new ConfirmAskRequest();
        request.setFlowName(flowName);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_ASK_ACK, request);
    }

    /**
     * 上传分数  封装模型
     * @param request
     */
    public static void uploadScore(UploadScoreRequest request) {
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REQUEST, request);
    }

    /**
     * 考试结束接收确认
     * @param missionId 考试id
     */
    public static void confirmExamAck(String missionId) {
        ConfirmExamAckRequest request=new ConfirmExamAckRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_EXAMEND_ACK, request);
    }

    /**
     * 定位不同请求
     * @param  missionId 考试id
     */
    public static void diff(String missionId) {
        DiffRequest request=new DiffRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REQUEST, request);
    }

    /**
     * 总成绩单请求
     * @param  missionId 考试id
     */
    public static void totalGradeRequest(String missionId) {
        TotalGradeRequest request=new TotalGradeRequest();
        request.setMissionId(missionId);
        request.setGroupId(ShareUtils.getGroup());
        MessageSender.sendMessage(ApiManager.MSG_TOTALGRADEFORM_REQUEST, request);
    }

    /**
     * 评分说明接收确认
     */
    public static void confirmInstruction(){
        BaseRequest request=new BaseRequest();
        MessageSender.sendMessage(ApiManager.MSG_MANUALSCORE_INSTRUCTION_ACK, request);
    }

    public static String parsePacketType(short packetType) {
        String messageDescription = "";
        switch (packetType) {
            case ApiManager.MSG_MANUALSCORE_START:
                //人工评分相关消息类型定义开始
                break;
            case ApiManager.MSG_SOCKET_HEARTBEAT:
                //心跳发送
                break;
            case ApiManager.MSG_SOCKET_HEARTBEAT_FEEDBACK:
                //心跳接收反馈
                break;
            case ApiManager.MSG_MANUALSCORE_LOGIN_REQUEST:
                //登录请求
                break;
            case ApiManager.MSG_MANUALSCORE_LOGIN_REPLY:
                //登录请求反馈
                break;
            case ApiManager.MSG_MANUALSCORE_LOGINOUT_REQUEST:
                //登出请求
                break;
            case ApiManager.MSG_MANUALSCORE_LOGINOUT_REPLY:
                //登出请求反馈
                break;
            case ApiManager.MSG_MANUALSCORE_CACHE_REQUEST:
                //评分缓存请求
                break;
            case ApiManager.MSG_MANUALSCORE_CACHE_REPLY:
                //评分缓存请求反馈
                break;
            case ApiManager.MSG_MANUALSCORE_FILESEND:
                //评分表文件发送
                break;
            case ApiManager.MSG_MANUALSCORE_FILESEND_ACK:
                //文件接收成功确认
                break;
            case ApiManager.MSG_MANUALSCORE_REPLACE_NOTIFY:
                //占位符替换通知
                break;
            case ApiManager.MSG_MANUALSCORE_REPLACE_ACK:
                //占位符接收确认
                break;
            case ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态通知
                break;
            case ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_ACK:
                //任务状态接收确认
                break;
            case ApiManager.MSG_MANUALSCORE_ASK_NOTIFY:
                //问询提醒通知
                break;
            case ApiManager.MSG_MANUALSCORE_ASK_ACK:
                //问询提醒接收确认
                break;
            case ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REQUEST:
                //上传分数请求
                break;
            case ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //上传分数反馈
                break;
            case ApiManager.MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
                break;
            case ApiManager.MSG_MANUALSCORE_EXAMEND_ACK:
                //考试结束接收确认
                break;
            case ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REQUEST:
                //评分结果不同请求
                break;
            case ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //评分结果不同反馈
                break;
            case ApiManager.MSG_TOTALGRADEFORM_REQUEST:
                //总成绩单请求
                break;
            case ApiManager.MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
                break;
            case ApiManager.MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
                break;
            case ApiManager.MSG_MANUALSCORE_INSTRUCTION_ACK:
                //评分说明通知确认
                break;
            case ApiManager.MSG_MANUALSCORE_END:
                //定义结束
                break;
        }
        return messageDescription;
    }
}
