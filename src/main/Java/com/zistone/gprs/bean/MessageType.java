package com.zistone.gprs.bean;

public class MessageType
{
    //终端通用应答
    public static final int CLIENTRESPONSE = 0x0001;

    //平台通用应答
    public static final int SERVERRESPONSE = 0x8001;

    //终端心跳
    public static final int CLIENTHEARTBEAT = 0x0002;

    //补传分包请求
    public static final int PACKAGEREQUEST = 0x8003;

    //终端注册
    public static final int CLIENTREGISTER = 0x0100;

    //终端注册应答
    public static final int CLIENTREGISTER_RESPONSE = 0x8100;

    //终端注销
    public static final int CLIENTLOGOUT = 0x0003;

    //终端鉴权
    public static final int CLIENTAK = 0x0102;

    //设置终端参数
    public static final int SETCLIENTPARAM = 0x8103;

    //查询终端参数
    public static final int SEARCHCLIENTPARM = 0x8104;

    //查询终端参数应答
    public static final int SEARCHCLIENTPAR_RESPONSE = 0x0104;

    //终端控制
    public static final int CLIENTCONTROL = 0x8105;

    //查询指定终端参数
    public static final int SEARCHCLIENTPARMBYID = 0x8106;

    //查询终端属性
    public static final int SEARCHCLIENTPROPERTY = 0x8107;

    //查询终端属性应答
    public static final int SEARCHCLIENTPROPERTY_RESPONSE = 0x0107;

    //下发终端升级包
    public static final int UPGRADERESULTPACKAGE = 0x8108;

    //终端升级结果通知
    public static final int UPGRADERESULT = 0x0108;

    //位置信息汇报
    public static final int LOCATIONREPORT = 0x0200;

    //位置信息查询
    public static final int LOCATIONSEARCH = 0x8201;

    //位置信息查询应答
    public static final int LOCATIONSEARCH_RESPONSE = 0x0201;

    //事件设置
    public static final int EVENTSET = 0x8301;

    //事件报告
    public static final int EVENTREPORT = 0x0301;

    //提问下发
    public static final int QUESTIONDOWN = 0x8302;

    //提问应答
    public static final int QUESTIONRESPON = 0x0302;

    //信息点播菜单设置
    public static final int DIBBLING = 0x8303;

    //信息点播/取消
    public static final int DIBBLING_CANCEL = 0x0303;

    //信息服务
    public static final int MESSAGESERVER = 0x8304;

    //电话回拨
    public static final int PHONECALL = 0x8400;

    //设置电话本
    public static final int SETPHONE = 0x8401;

    //车辆控制
    public static final int CARCONTROL = 0x8500;

    //车辆控制应答
    public static final int CARCONTROL_RESPONSE = 0x0500;

    //设置圆形区域
    public static final int SETROUNDREGION = 0x8600;

    //删除圆形区域
    public static final int DELROUNDREGION = 0x8601;

    //设置矩形区域
    public static final int SETRECTREGION = 0x8602;

    //删除矩形区域
    public static final int DELRECTREGION = 0x8603;

    //设置多边形区域
    public static final int SETPOLYREGION = 0x8604;

    //删除多边形区域
    public static final int DELPOLYREGION = 0x8605;

    //设置路线
    public static final int SETROUTE = 0x8606;

    //删除路线
    public static final int DELROUTE = 0x8607;

    //行驶记录仪数据采集命令
    public static final int DRIVERDATACOLLECTION = 0x8700;

    //临时位置跟踪控制
    public static final int TEMPLOCATION = 0x8202;

    //行驶记录仪数据上传
    public static final int DRIVERDATAUP = 0x0700;

    //人工确认报警消息
    public static final int ARTIFICIALCONFIRM = 0x8203;

    //行驶记录仪参数下传命令
    public static final int DRIVERPARAMDOWN = 0x8701;

    //文本信息下发
    public static final int TXTUP = 0x8300;

    //电子运单上报
    public static final int ELECTRONORDERUP = 0x0701;

    //驾驶员身份信息采集上报
    public static final int DRIVERMESSAGECOLLCTIONUP = 0x0702;

    //存储多媒体数据上传
    public static final int STORAGEMULTIMEDIAUP = 0x8803;

    //存储多媒体数据检索应答
    public static final int STORAGEMULTIMESEARCH_RESPONSE = 0x0802;

    //多媒体数据上传应答
    public static final int MULTIMEDATAUP_RESPONSE = 0x8800;

    //存储多媒体数据检索
    public static final int STORAGEMULTIMEDIASEARCH = 0x8802;

    //上报驾驶员身份信息请求
    public static final int DRIVERMESSAGEUP_REQUEST = 0x8702;

    //单条存储多媒体数据检索上传命令
    public static final int SINGLEMULTIMEDIAUP = 0x8805;

    //多媒体事件信息上传
    public static final int STORAGEMULTIMEDIAEVENTUP = 0x0800;

    //录音开始命令
    public static final int RECORDINGUP = 0x8804;

    //定位数据批量上传
    public static final int LOCATIONBATCHUP = 0x0704;

    //CAN总线数据上传
    public static final int CANDATAUP = 0x0705;

    //数据下行透传
    public static final int DATADOWN = 0x8900;

    //数据上行透传
    public static final int DATAUP = 0x0900;

    //多媒体数据上传
    public static final int MULTIMEDIAUP = 0x0801;

    //数据压缩上报
    public static final int DATACOMPRESSIONUP = 0x0901;

    //平台RSA公钥
    public static final int SERVERRSAPUBLICKEY = 0x8A00;

    //终端RSA公钥
    public static final int CLIENTRSAPUBLICKEY = 0x0A00;

    //摄像头立即拍摄命令
    public static final int CARERASTART = 0x8801;

    //摄像头立即拍摄命令应答
    public static final int CARERASTART_RESPONSE = 0x0805;
}
