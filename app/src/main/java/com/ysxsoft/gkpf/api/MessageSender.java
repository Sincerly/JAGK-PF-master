package com.ysxsoft.gkpf.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.AbsReconnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.ysxsoft.gkpf.bean.SocketHeartBean;
import com.ysxsoft.gkpf.bean.request.BaseRequest;
import com.ysxsoft.gkpf.utils.ByteUtils;
import com.ysxsoft.gkpf.utils.ShareUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_ASK_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_CACHE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_EXAMEND_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_FILESEND;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_FILESEND_ACK;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_INSTRUCTION_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_LOGINOUT_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_REPLACE_ACK;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_REPLACE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_ACK;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_TOTALGRADEFORM_REPLY;

/**
 * 消息发送
 * 数据协议
 * ------------------
 * 起始码	1字节	0X03
 * 地址码	1字节	0X01
 * 报文类型	2字节	区分不同的报文，整数，范围（1-32768）
 * 数据长度	4字节	接下来的字节数目，整数，范围（1-4294967295
 * 数据内容	N字节	Json字符串数据 eg：{"key1", value1, "key2", value2}
 * 校验码	1字节	起始码与校验码之间所有数据之和（不包含起始码与校验码）
 * 终止码	1字节	0X0A
 * ------------------
 * {@link #createMessage(int, String)}     //创建消息
 * {@link #checkSum(byte[], int, int)}     //校验和
 * {@link #connect(String, int)}           //连接服务器
 * {@link #sendMessage(int, String)}       //发送消息
 */
public class MessageSender {

    public static IConnectionManager manager;
    public static ConnectionInfo info;
    public static SocketActionAdapter adapter;

    /**
     * 创建消息
     *
     * @param packetType 报文类型
     * @param str        消息内容
     * @return
     */
    public static byte[] createMessage(short packetType, String str) {
        byte start = 0X03;
        byte address = 0X01;
        byte[] packet = ByteUtils.shortToBytes((short) packetType);//大端字节传输   byte[2,0]
        byte[] body = str.getBytes(Charset.defaultCharset());
        int dataLength = body.length;
        byte[] dataArray = ByteUtils.intToBytes(dataLength);//大端字节传输  byte[13,0,0,0]

        byte[] bytes = new byte[10 + body.length];
        bytes[0] = start;//起始码
        bytes[1] = address;//地址码
        int p = 2;
        for (int i = p; i < packet.length + p; i++) {
            bytes[i] = packet[i - p];//报文类型
        }
        p = packet.length + p;//position 4
        for (int i = p; i < dataArray.length + p; i++) {
            bytes[i] = dataArray[i - p];//数据长度
        }
        p = dataArray.length + p;//position 8
        System.arraycopy(body, 0, bytes, p, body.length);//数据内容
        byte sum = checkSum(bytes, 1, bytes.length - 2);
        bytes[bytes.length - 2] = sum;//校验码
        bytes[bytes.length - 1] = 0X0A;//终止码
        return bytes;
    }

    public static byte checkSum(byte[] btAryBuffer, int start, int nLen) {
        byte btSum = 0x00;
        for (int i = start; i < start + nLen; i++) {
            btSum += btAryBuffer[i];
        }
        return btSum;
//        return (byte) ((~btSum) + 1 & 0xFF);
    }

    public static String translationMessage(byte[] responseBytes) {
        String json = "";
        return json;
    }

    /**
     * 连接服务器
     *
     * @param ip
     * @param port
     */
    public static void connect(String ip, int port) {
        //清空消息池
        handler.removeCallbacksAndMessages(null);
        info = new ConnectionInfo(ip, port);
        manager = OkSocket.open(info);
        //配置属性
        OkSocketOptions options = manager.getOption();
        OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(options);
        okOptionsBuilder.setPulseFeedLoseTimes(5000);//心跳包5s丢失
        okOptionsBuilder.setPulseFrequency(1000);//心跳包间隔毫秒数
        okOptionsBuilder.setConnectTimeoutSecond(20000);
        okOptionsBuilder.setMaxReadDataMB(1024);
        okOptionsBuilder.setReconnectionManager(new ReconnectionManager());
        okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                return 8;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                Log.e("tag", "getBodyLength headerLength:"+new Gson().toJson(header));
                int bodyLength = 8;
                byte[] b = new byte[4];
                for (int i = 4; i < 8; i++) {
                    b[i - 4] = header[i];
                }
                bodyLength = ByteUtils.bytesToInt(b) + 2;
                Log.e("tag", "getBodyLength dataLength:" + bodyLength);
                return bodyLength;
            }
        });
        manager.option(okOptionsBuilder.build());
        //配置监听
        adapter = new SocketActionAdapter() {

            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Log.e("tag", "onSocketConnectionSuccess:" + action.toString());
                //Socket连接成功回调
                OkSocket.open(info)
                        .getPulseManager()
                        .setPulseSendable(pulseData)//只需要设置一次,下一次可以直接调用pulse()
                        .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
            }

            @Override
            public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                super.onSocketConnectionFailed(info, action, e);
                //Socket连接失败回调
                Log.e("tag", "onSocketConnectionFailed:" + action.toString() + "exception:" + e.getMessage());
            }

            @Override
            public void onSocketIOThreadStart(String action) {
                super.onSocketIOThreadStart(action);
                //Socket读写线程启动后回调
                Log.e("tag", "onSocketIOThreadStart:" + action.toString());
            }

            @Override
            public void onSocketIOThreadShutdown(String action, Exception e) {
                super.onSocketIOThreadShutdown(action, e);
                //Socket读写线程关闭后回调
                Log.e("tag", "onSocketIOThreadShutdown:" + action.toString());
            }

            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(info, action, e);
                //Socket连接状态由连接->断开回调
                Log.e("tag", "onSocketDisconnection:" + action.toString());
            }

            /**
             * 从服务器读到数据回调
             * @param info
             * @param action
             * @param data
             */
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
                handler.removeCallbacksAndMessages(null);//清空handler
                handler.sendEmptyMessageDelayed(0x01, 5000);//5s后重连
                //Socket从服务器读取到字节回调
                int length = data.getBodyBytes().length;
                if (length > 0) {
                    byte[] bytes = data.getBodyBytes();//带终止码和校验码的
                    byte[] body = new byte[bytes.length - 2];

                    byte end = bytes[bytes.length - 1];//终止码
                    if (0X0A == end) {
                        //数据校验
                        byte[] header = new byte[7];
                        System.arraycopy(data.getHeadBytes(), 1, header, 0, data.getHeadBytes().length - 1);//Header 去掉0X03起始码  参与校验用
                        System.arraycopy(bytes, 0, body, 0, bytes.length - 2);//数据内容

                        byte[] checkBytes = new byte[header.length + body.length];
                        System.arraycopy(header, 0, checkBytes, 0, header.length);//Header 去掉0X03起始码  参与校验用
                        System.arraycopy(body, 0, checkBytes, header.length, body.length);//数据内容

                        byte parseSum = checkSum(checkBytes, 0, checkBytes.length);
                        byte sum = bytes[bytes.length - 2];//校验码
                        //校验码比对
                        if (parseSum == sum) {
                            short packet = getPacketType(data.getHeadBytes());//获取报文类型
                            String s = null;
                            try {
                                s = new String(body, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            switch (packet) {
                                case ApiManager.MSG_MANUALSCORE_LOGIN_REPLY:
                                    //登录反馈
                                    Log.e("tag", "登录回复" + s);
                                    MessageCallbackMap.notifyPage("Login", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_LOGINOUT_REPLY:
                                    //登出反馈
                                    Log.e("tag", "登出回复" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_CACHE_REPLY:
                                    //缓存反馈
                                    Log.e("tag", "缓存反馈" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_FILESEND:
                                    //评分表文件发送
                                    Log.e("tag", "评分表文件发送" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_REPLACE_NOTIFY:
                                    //占位符替换数据通知
                                    Log.e("tag", "占位符替换数据通知" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                                    //任务状态变化通知
                                    Log.e("tag", "任务状态变化通知" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_ASK_NOTIFY:
                                    //问询通知
                                    Log.e("tag", "问询通知" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                                    //评分上传反馈
                                    Log.e("tag", "评分上传反馈" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                                    //考试结束通知
                                    Log.e("tag", "考试结束通知" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                                    //定位不同反馈
                                    Log.e("tag", "定位不同反馈" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_TOTALGRADEFORM_REPLY:
                                    //总成绩单反馈
                                    Log.e("tag", "总成绩单反馈" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                                    //评分说明通知
                                    Log.e("tag", "评分说明通知" + s);
                                    MessageCallbackMap.notifyPage("Main", packet, s, body);
                                    break;
                                default:
                                    break;
                            }
                            Log.e("tag", "收到数据:" + s);
                        }
                    } else {
                        Log.e("tag", "收到数据:无结尾符");
                    }
                }
                if (manager != null) {//是否是心跳返回包,需要解析服务器返回的数据才可知道
                    //喂狗操作
                    manager.getPulseManager().feed();
                }
            }

            @Override
            public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
                super.onSocketWriteResponse(info, action, data);
//                Log.e("tag", "onSocketWriteResponse:" + data.toString());
                Log.e("tag", "发送数据:" + parsePacketType(getPacketType(data.parse())) + " 数据包:" + new Gson().toJson(data.parse()));
            }

            @Override
            public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
                super.onPulseSend(info, data);
                //心跳发送回调
//                Log.e("tag", "onPulseSend:" + ByteUtils.byteArrToHex(data.parse()));
                Log.e("tag", "发送:" + new Gson().toJson(data.parse()));
            }
        };
        manager.registerReceiver(adapter);
        manager.connect();
    }

    /**
     * 重新连接
     */
    public static void reconnect() {
        if (manager != null && !manager.isConnect()) {
            manager.connect();
        }
    }

    public static class ReconnectionManager extends AbsReconnectionManager {

        @Override
        public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
            Log.e("tag", "连接 onSocketDisconnection");
            handler.removeCallbacksAndMessages(null);
            reconnect();
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
            Log.e("tag", "连接 onSocketConnectionSuccess");
            handler.removeCallbacksAndMessages(null);
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
            Log.e("tag", "连接 onSocketConnectionFailed");
            handler.removeCallbacksAndMessages(null);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    reconnect();
                }
            }, 1000);
        }
    }

    /**
     * 断开连接
     */
    public static void disconnect() {
        if (manager != null && manager.isConnect()) {
            manager.disconnect();
        }
    }

    private static Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x01:
                    //该消息主要负责未获取到信息 5s断开连接
                    Pattern pattern = Pattern.compile("^\\d+$");
                    if (!"".equals(ShareUtils.getPort()) && pattern.matcher(ShareUtils.getPort()).matches()) {
                        //5s之后重连
                        Log.e("tag", "断开连接!");
                        if (manager != null && manager.isConnect()) {
                            manager.disconnect();
                            manager.unRegisterReceiver(adapter);
                        }
                    } else {
                    }
                    break;
            }
        }
    };

    private static short getPacketType(byte[] header) {
        short type = 0;
        byte[] packetType = new byte[2];
        for (int i = 2; i < 4; i++) {
            packetType[i - 2] = header[i];
        }
        return ByteUtils.bytesToShort(packetType);
    }

    public static int REQUEST_ID = 1;

    /**
     * 发送消息
     *
     * @param packetType
     * @param data
     */
    public static void sendMessage(short packetType, BaseRequest request) {
        request.setRequestId(REQUEST_ID);
        if (manager != null && manager.isConnect()) {
            OkSocket.open(info).send(new SendData(packetType, new Gson().toJson(request)));
            REQUEST_ID++;
            if (REQUEST_ID > 10000) {
                REQUEST_ID = 1;
            }
        }
    }

    /**
     * 发送数据包
     */
    public static class SendData implements ISendable {
        private short packetType;
        private String data;

        public SendData(short packetType, String data) {
            this.packetType = packetType;
            this.data = data;
            Log.e("tag", "报文类型:" + parsePacketType(packetType) + " 数据包:" + data);
        }

        @Override
        public byte[] parse() {
            byte[] bytes = createMessage(packetType, data);
            return bytes;
        }
    }

    /**
     * 心跳包
     */
    public static int ackId = 1;//心跳每秒加1
    public static int maxAckId = 32767;//心跳包最大
    public static PulseData pulseData = new PulseData();

    public static class PulseData implements IPulseSendable {
        @Override
        public byte[] parse() {
            SocketHeartBean heartBean = new SocketHeartBean();
            ackId++;
            if (ackId >= maxAckId) {
                ackId = 1;
            }
            heartBean.setAckId(ackId);
            heartBean.setGroupId(ShareUtils.getGroup());
            return createMessage(ApiManager.MSG_SOCKET_HEARTBEAT, new Gson().toJson(heartBean));
        }
    }

    public static String parsePacketType(short packetType) {
        String messageDescription = "";
        switch (packetType) {
            case ApiManager.MSG_MANUALSCORE_START:
                //人工评分相关消息类型定义开始
                messageDescription = "人工评分相关消息类型定义开始";
                break;
            case ApiManager.MSG_SOCKET_HEARTBEAT:
                //心跳发送
                messageDescription = "心跳发送";
                break;
            case ApiManager.MSG_SOCKET_HEARTBEAT_FEEDBACK:
                //心跳接收反馈
                messageDescription = "心跳接收反馈";
                break;
            case ApiManager.MSG_MANUALSCORE_LOGIN_REQUEST:
                //登录请求
                messageDescription = "登录请求";
                break;
            case ApiManager.MSG_MANUALSCORE_LOGIN_REPLY:
                //登录请求反馈
                messageDescription = "登录请求反馈";
                break;
            case ApiManager.MSG_MANUALSCORE_LOGINOUT_REQUEST:
                //登出请求
                break;
            case MSG_MANUALSCORE_LOGINOUT_REPLY:
                //登出请求反馈
                break;
            case ApiManager.MSG_MANUALSCORE_CACHE_REQUEST:
                //评分缓存请求
                messageDescription = "评分缓存请求";
                break;
            case MSG_MANUALSCORE_CACHE_REPLY:
                //评分缓存请求反馈
                messageDescription = "评分缓存请求反馈";
                break;
            case MSG_MANUALSCORE_FILESEND:
                //评分表文件发送
                messageDescription = "评分表文件发送";
                break;
            case MSG_MANUALSCORE_FILESEND_ACK:
                //文件接收成功确认
                messageDescription = "文件接收成功确认";
                break;
            case MSG_MANUALSCORE_REPLACE_NOTIFY:
                //占位符替换通知
                messageDescription = "占位符替换通知";
                break;
            case MSG_MANUALSCORE_REPLACE_ACK:
                //占位符接收确认
                messageDescription = "占位符接收确认";
                break;
            case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态通知
                messageDescription = "任务状态通知";
                break;
            case MSG_MANUALSCORE_TASKLISTSTATE_ACK:
                //任务状态接收确认
                messageDescription = "任务状态接收确认";
                break;
            case MSG_MANUALSCORE_ASK_NOTIFY:
                //问询提醒通知
                messageDescription = "问询提醒通知";
                break;
            case ApiManager.MSG_MANUALSCORE_ASK_ACK:
                //问询提醒接收确认
                messageDescription = "问询提醒接收确认";
                break;
            case ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REQUEST:
                //上传分数请求
                messageDescription = "上传分数请求";
                break;
            case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //上传分数反馈
                messageDescription = "上传分数反馈";
                break;
            case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
                messageDescription = "考试结束通知";
                break;
            case ApiManager.MSG_MANUALSCORE_EXAMEND_ACK:
                //考试结束接收确认
                messageDescription = "考试结束接收确认";
                break;
            case ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REQUEST:
                //评分结果不同请求
                messageDescription = "评分结果不同请求";
                break;
            case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //评分结果不同反馈
                messageDescription = "评分结果不同反馈";
                break;
            case ApiManager.MSG_TOTALGRADEFORM_REQUEST:
                //总成绩单请求
                messageDescription = "总成绩单请求";
                break;
            case MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
                messageDescription = "总成绩单反馈";
                break;
            case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
                messageDescription = "评分说明通知";
                break;
            case ApiManager.MSG_MANUALSCORE_INSTRUCTION_ACK:
                //评分说明通知确认
                messageDescription = "评分说明通知确认";
                break;
            case ApiManager.MSG_MANUALSCORE_END:
                //定义结束
                messageDescription = "定义结束";
                break;
        }
        return messageDescription;
    }

    /**
     * int转换为4字节数组
     * 例如 13转换为 [0,0,0,13]
     *
     * @param value
     * @return
     */
    public static byte[] intToByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    /**
     * 检测是否有效
     *
     * @param packetType
     * @return
     */
    public static boolean checkPacketType(short packetType) {
        boolean auth = false;
        switch (packetType) {
            case ApiManager.MSG_MANUALSCORE_START:
                //人工评分相关消息类型定义开始
            case ApiManager.MSG_SOCKET_HEARTBEAT:
                //心跳发送
            case ApiManager.MSG_SOCKET_HEARTBEAT_FEEDBACK:
                //心跳接收反馈
            case ApiManager.MSG_MANUALSCORE_LOGIN_REQUEST:
                //登录请求
            case ApiManager.MSG_MANUALSCORE_LOGIN_REPLY:
                //登录请求反馈
            case ApiManager.MSG_MANUALSCORE_LOGINOUT_REQUEST:
                //登出请求
            case MSG_MANUALSCORE_LOGINOUT_REPLY:
                //登出请求反馈
            case ApiManager.MSG_MANUALSCORE_CACHE_REQUEST:
                //评分缓存请求
            case MSG_MANUALSCORE_CACHE_REPLY:
                //评分缓存请求反馈
            case MSG_MANUALSCORE_FILESEND:
                //评分表文件发送
            case MSG_MANUALSCORE_FILESEND_ACK:
                //文件接收成功确认
            case MSG_MANUALSCORE_REPLACE_NOTIFY:
                //占位符替换通知
            case MSG_MANUALSCORE_REPLACE_ACK:
                //占位符接收确认
            case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态通知
            case MSG_MANUALSCORE_TASKLISTSTATE_ACK:
                //任务状态接收确认
            case MSG_MANUALSCORE_ASK_NOTIFY:
                //问询提醒通知
            case ApiManager.MSG_MANUALSCORE_ASK_ACK:
                //问询提醒接收确认
            case ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REQUEST:
                //上传分数请求
            case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //上传分数反馈
            case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
            case ApiManager.MSG_MANUALSCORE_EXAMEND_ACK:
                //考试结束接收确认
            case ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REQUEST:
                //评分结果不同请求
            case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //评分结果不同反馈
            case ApiManager.MSG_TOTALGRADEFORM_REQUEST:
                //总成绩单请求
            case MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
            case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
            case ApiManager.MSG_MANUALSCORE_INSTRUCTION_ACK:
                //评分说明通知确认
            case ApiManager.MSG_MANUALSCORE_END:
                //定义结束
                auth = true;
                break;
        }
        return auth;
    }
}
