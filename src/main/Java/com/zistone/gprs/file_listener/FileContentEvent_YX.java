package com.zistone.gprs.file_listener;

import com.alibaba.fastjson.JSON;
import com.zistone.gprs.bean.LocationInfo;
import com.zistone.gprs.socket.SocketHttp;
import com.zistone.gprs.util.MyConvertUtil;
import com.zistone.gprs.util.MyPropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class FileContentEvent_YX {
    private static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static int _lineCount;
    private static int _webPort;
    private static String _webIP;

    static {
        _webIP = MyPropertiesUtil.GetValueProperties().getProperty("IP_WEB");
        _webPort = Integer.valueOf(MyPropertiesUtil.GetValueProperties().getProperty("PORT_WEB"));
    }

    private Logger _logger = Logger.getLogger(FileContentEvent_YX.class);
    private Timer _timer = new Timer();

    public FileContentEvent_YX() {
        ReadFileThread readFileThread = new ReadFileThread();
        //readFileThread.start();
        _logger.info(">>>线程" + readFileThread.getId() + "执行");

        ReadFromEamilFile("C:\\Users\\zistone\\Desktop\\YX_Email\\300234067349750_001276.sbd");
    }

    private void ReadFromEamilFile(String path) {
        File file = new File(path);
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new FileInputStream(file);
            //一次读取一个byte
            byte[] bytes = new byte[1];
            String str = "";
            String hexStr = "";
            while (inputStream.available() > 0) {
                inputStream.read(bytes);
                hexStr += MyConvertUtil.ByteArrayToHexStr(bytes);
                str += MyConvertUtil.HexStrToStr(hexStr);
            }
            System.out.println(">>>文件内容(HX):" + hexStr);
            String[] strArray = MyConvertUtil.StrAddCharacter(hexStr, 2, ",").split(",");
            //纬度,例如:167D12
            String lat1Str = String.valueOf(Integer.parseInt(strArray[13], 16));
            String lat2Str = String.valueOf(Integer.parseInt(strArray[13], 16));
            String lat3Str = String.valueOf(Integer.parseInt(strArray[14], 16));
            double latNumXxx = Double.valueOf(lat1Str + lat2Str + lat3Str) / 1000000;
            //经度,例如:71DFBB
            String lot1Str = String.valueOf(Integer.parseInt(strArray[15], 16));
            String lot2Str = String.valueOf(Integer.parseInt(strArray[16], 16));
            String lot3Str = String.valueOf(Integer.parseInt(strArray[17], 16));
            double lotNumXxx = Double.valueOf(lot1Str + lot2Str + lot3Str) / 1000000;

            if (1 > 0)
                return;
            String[] strArray1 = str.split("L");
            //设备编号
            String deviceId = strArray1[0].trim();
            String[] tempArray1 = strArray1[1].split("  ");
            //时间
            String time1 = tempArray1[1].replace("/", "-");
            Date date1 = SIMPLEDATEFORMAT.parse(time1);
            String time2 = tempArray1[2];
            //经纬度
            String latStr = tempArray1[3].trim();
            double lat = Double.valueOf(latStr);
            String lotStr = tempArray1[4].trim();
            double lot = Double.valueOf(lotStr);
            //TODO:其它参数不知道什么意思
            if (lat != 0.0 && lot != 0.0) {
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setDeviceId(deviceId);
                locationInfo.setLat(lat);
                locationInfo.setLot(lot);
                locationInfo.setCreateTime(date1);
                _logger.debug(String.format(">>>将本次数据%s更新至MySQL数据库", locationInfo.toString()));
                String locationStr = JSON.toJSONString(locationInfo);
                //由Web服务处理位置汇报
                String locationResult = new SocketHttp().SendPost(_webIP, _webPort, "/GPRS_Web/LocationInfo/InsertList", locationStr);
                int beginIndex2 = locationResult.indexOf("{");
                int endIndex2 = locationResult.lastIndexOf("}");
                locationResult = locationResult.substring(beginIndex2, endIndex2 + 1);
                _logger.debug(String.format(">>>汇报铱星设备位置,返回:%s", locationResult));
            } else {
                _logger.error(">>>本次数据有错误,禁止更新至数据库");
            }
        } catch (Exception e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadFileThread extends Thread {
        @Override
        public void start() {
            super.start();
        }

        @Override
        public void run() {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //ReadFile();
                    //ReadAllFile();
                }
            };
            _timer.schedule(timerTask, 0, 30 * 60 * 1000);
            _logger.info(">>>定时读取文本内容的任务执行");
        }

        private void ReadFile(String path) {
            File file = new File(path);
            FileInputStream fileInputStream;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            int lineCount;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                //过滤空行
                Stream<String> streams = bufferedReader.lines().filter(p -> p != null && !"".equals(p) && p.contains("L"));
                Object[] array = streams.toArray();
                lineCount = array.length;
                _logger.info(">>>本次共读取(过滤了空行):" + lineCount);
                //文件内容有变动
                if (lineCount != _lineCount) {
                    //最新的一条数据
                    String line = String.valueOf(Stream.of(array).filter(p -> p.equals("让过滤器的结果为false,执行返回最后一个元素")).findFirst()
                            .orElse(array[lineCount - 1]));
                    System.out.println(">>>监听的文本文件的内容有更新:" + line);
                    String[] strArray1 = line.split("L");
                    //设备编号
                    String deviceId = strArray1[0].trim();
                    String[] tempArray1 = strArray1[1].split("  ");
                    //时间
                    String time1 = tempArray1[1].replace("/", "-");
                    Date date1 = SIMPLEDATEFORMAT.parse(time1);
                    String time2 = tempArray1[2];
                    //经纬度
                    String latStr = tempArray1[3].trim();
                    double lat = Double.valueOf(latStr);
                    String lotStr = tempArray1[4].trim();
                    double lot = Double.valueOf(lotStr);
                    //TODO:其它参数不知道什么意思
                    if (lat != 0.0 && lot != 0.0) {
                        LocationInfo locationInfo = new LocationInfo();
                        locationInfo.setDeviceId(deviceId);
                        locationInfo.setLat(lat);
                        locationInfo.setLot(lot);
                        locationInfo.setCreateTime(date1);
                        _logger.debug(String.format(">>>将本次数据%s更新至MySQL数据库", locationInfo.toString()));
                        String locationStr = JSON.toJSONString(locationInfo);
                        //由Web服务处理位置汇报
                        String locationResult = new SocketHttp()
                                .SendPost(_webIP, _webPort, "/GPRS_Web/LocationInfo/InsertList", locationStr);
                        int beginIndex2 = locationResult.indexOf("{");
                        int endIndex2 = locationResult.lastIndexOf("}");
                        locationResult = locationResult.substring(beginIndex2, endIndex2 + 1);
                        _logger.debug(String.format(">>>汇报铱星设备位置,返回:%s", locationResult));
                    } else {
                        _logger.error(">>>本次数据有错误,禁止更新至数据库");
                    }
                    _lineCount = lineCount;
                }
            } catch (Exception e) {
                _logger.error(e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStreamReader) {
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void ReadAllFile(String path) {
            File file = new File(path);
            FileInputStream fileInputStream;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            int lineCount;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                //过滤空行
                Stream<String> streams = bufferedReader.lines().filter(p -> p != null && !"".equals(p) && p.contains("L"));
                String[] array = streams.toArray(String[]::new);
                lineCount = array.length;
                List<LocationInfo> locationInfoList = new ArrayList<>();
                for (String line : array) {
                    try {
                        String[] strArray1 = line.split("     ");
                        //设备编号
                        String deviceId = strArray1[0].trim();
                        String[] tempArray1 = strArray1[1].split("  ");
                        //时间
                        String time1 = tempArray1[1].replace("/", "-");
                        Date date1 = SIMPLEDATEFORMAT.parse(time1);
                        String time2 = tempArray1[2];
                        //经纬度
                        String latStr = tempArray1[3].trim();
                        double lat = Double.valueOf(latStr);
                        String lotStr = tempArray1[4].trim();
                        double lot = Double.valueOf(lotStr);
                        //TODO:其它参数不知道什么意思
                        if (lat != 0.0 && lot != 0.0) {
                            LocationInfo locationInfo = new LocationInfo();
                            locationInfo.setDeviceId(deviceId);
                            locationInfo.setLat(lat);
                            locationInfo.setLot(lot);
                            locationInfo.setCreateTime(date1);
                            locationInfoList.add(locationInfo);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        continue;
                    }
                }
                if (locationInfoList.size() > 0) {
                    _logger.debug(String.format(">>>本次共读取(过滤了空行):%d条数据,新增%d条正确数据", lineCount, locationInfoList.size()));
                    String locationStr = JSON.toJSONString(locationInfoList);
                    //由Web服务处理位置汇报
                    String locationResult = new SocketHttp()
                            .SendPost(_webIP, _webPort, "/GPRS_Web/LocationInfo" + "/InsertList", locationStr);
                    int beginIndex2 = locationResult.indexOf("{");
                    int endIndex2 = locationResult.lastIndexOf("}");
                    locationResult = locationResult.substring(beginIndex2, endIndex2 + 1);
                    _logger.debug(String.format(">>>汇报铱星设备位置,返回:%s", locationResult));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStreamReader) {
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
