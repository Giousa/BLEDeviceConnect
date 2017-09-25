package com.zmm.bledeviceconnect.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志记录工具类
 * 在Release签名状态下命令行开启日志开关
 * adb shell setprop log.tag.TW DEBUG
 * @author kuangbiao
 * @version V1.3.1
 * @Description Wrapper API for sending log output.
 * @date 2014-4-24 下午3:59:58
 */
public class LogUtil {
    private static final String TAG_TW = "TW";

    //日志文件最大尺寸: 10M
    public static int MAX_LOG_FILE_SIZE = 1024 * 1024 * 10;

    //默认日志级别 为debug，发布时改成ERR0R
//    private static int logLevel = LogLevel.ERROR;
    private static int logLevel = LogLevel.DEBUG;

    //是否将日志记录文件(默认否),日志记录文件是一个比较耗时的操作
    private static boolean writeFile = false;
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //日志文件绝对路径
    private static String logFilePath = "";

    private static FileWriter fw;

    public static final String VERBOSE = "verbose";
    public static final String DEBUG = "debug";
    public static final String INFO = "info";
    public static final String WARN = "warn";
    public static final String ERROR = "error";

    public static interface LogLevel {
        public final int VERBOSE = Log.VERBOSE;

        public final int DEBUG = Log.DEBUG;

        public final int INFO = Log.INFO;

        public final int WARN = Log.WARN;

        public final int ERROR = Log.ERROR;

        public final int CLOSE = Log.ASSERT;
    }

    public LogUtil() {
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        LogUtil.logLevel = logLevel;
    }

    public static void setWriteFile(boolean writeFile, String logFilePath) {
        LogUtil.writeFile = writeFile;
        LogUtil.logFilePath = logFilePath;

              if (LogUtil.writeFile && !TextUtils.isEmpty(LogUtil.logFilePath)) {
                  if (fw == null) {
                      try {
                          File logFile = new File(LogUtil.logFilePath);
                          if (logFile.exists()
                                  && logFile.length() > MAX_LOG_FILE_SIZE) {
                              //                        logFile.delete();
                              File newFile = new File(logFile.getParent() + "/"
                                      + logFile.getName() + "-"
                                      + System.currentTimeMillis());
                              logFile.renameTo(newFile);
                              //                        boolean create = logFile.createNewFile();
                              //                        android.util.Log.i(TAG,
                              //                                "logFile too big and createNewFile == "
                              //                                        + create);
                          }

                          if (!logFile.exists()) {
                              File parent = new File(logFile.getParent());
                              if (!parent.isDirectory()) {
                                  parent.mkdirs();
                              }
                              logFile.createNewFile();
                          }

                          fw = new FileWriter(new File(LogUtil.logFilePath), true);
                      } catch (Exception e) {
                          e.printStackTrace();
                          LogUtil.writeFile = false;
                          fw = null;
                          LogUtil.logFilePath = null;
                      }
                  }
              } else {
                  if (fw != null) {
                      try {
                          fw.close();
                          fw = null;
                          LogUtil.logFilePath = null;
                      } catch (IOException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                      }
                  }
              }
    }

    public static String getLogFilePath() {
        return logFilePath;
    }

    public static void v(boolean on, String TAG, String msg) {
        if (!on)
            return;
        v(TAG, msg);
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg)
    {
        if (logLevel > LogLevel.VERBOSE)
            return;
        String logMsg = buildMessage(msg);
        Log.v(TAG_TW, logMsg);
        if (writeFile)
        {
            writeLog2file(VERBOSE,logMsg);
        }
    }
    public static void v(String TAG, String msg) {
        if (logLevel > LogLevel.VERBOSE)
            return;
        String logMsg = buildMessage(msg);
        Log.v(TAG, logMsg);
        if (writeFile) {
            writeLog2file(VERBOSE, logMsg);
        }
    }

    public static void destroy() {
        if (null != fw) {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeLog2file(String logLevel, String msg) {
        String fileAbsolutePath = logFilePath + logLevel + ".txt";
        //如果文件或者目录不存在，先创建
        File logFile = new File(fileAbsolutePath);
        if (!logFile.exists()) {
            File parent = new File(logFile.getParent());
            if (!parent.isDirectory()) {
                parent.mkdirs();
            }

            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fw == null) {
            try {
                fw = new FileWriter(new File(fileAbsolutePath), true);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            fw.append(formatter.format(new Date())).append(msg).append("\n").append("\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
                fw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr)
    {
        if (logLevel > LogLevel.VERBOSE)
            return;
        String logMsg = buildMessage(msg);
        Log.v(TAG_TW, logMsg, thr);
        if (writeFile)
        {
            writeLog2file(VERBOSE,logMsg);
    }
    }
    public static void v(String TAG, String msg, Throwable thr) {
        if (logLevel > LogLevel.VERBOSE)
            return;
        String logMsg = buildMessage(msg);
        Log.v(TAG, logMsg, thr);
        if (writeFile) {
            writeLog2file(VERBOSE, logMsg);
        }
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg)
    {
        if (logLevel > LogLevel.DEBUG)
            return;
        String logMsg = buildMessage(msg);
        Log.d(TAG_TW, logMsg);
        if (writeFile)
        {
            writeLog2file(DEBUG, logMsg);
        }
    }

    public static void d(boolean on, String TAG, String msg) {
        if (!on)
            return;
        d(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (logLevel > LogLevel.DEBUG)
        {
            if(Log.isLoggable(TAG_TW, Log.DEBUG))
            {
                Log.d(TAG_TW, msg);
            }
            return;
        }
        String logMsg = buildMessage(msg);
        Log.d(TAG, logMsg);
        if (writeFile) {
            writeLog2file(DEBUG, logMsg);

        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String msg, Throwable thr)
    {
        if (logLevel > LogLevel.DEBUG)
            return;
        String logMsg = buildMessage(msg);
        Log.d(TAG_TW, logMsg, thr);
        if (writeFile)
        {
            writeLog2file(DEBUG, logMsg);
        }
    }

    public static void d(String TAG, String msg, Throwable thr) {
        if (logLevel > LogLevel.DEBUG)
            return;
        String logMsg = buildMessage(msg);
        Log.d(TAG, logMsg, thr);
        if (writeFile) {
            writeLog2file(DEBUG, logMsg);
        }
    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     *
     */

    public static void i(String msg)
    {
        if (logLevel > LogLevel.INFO)
            return;
        String logMsg = buildMessage(msg);
        Log.i(TAG_TW, logMsg);
        if (writeFile)
        {
            writeLog2file(INFO, logMsg);

        }
    }
    public static void i(boolean on, String TAG, String msg) {
        if (!on)
            return;
        i(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (logLevel > LogLevel.INFO)
            return;
        String logMsg = buildMessage(msg);
        Log.i(TAG, logMsg);
        if (writeFile) {
            writeLog2file(INFO, logMsg);

        }
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr)
    {
        if (logLevel > LogLevel.INFO)
            return;
        String logMsg = buildMessage(msg);
        Log.i(TAG_TW, logMsg, thr);
        if (writeFile)
        {
            writeLog2file(INFO, logMsg);
        }
    }

    public static void i(String TAG, String msg, Throwable thr) {
        if (logLevel > LogLevel.INFO)
            return;
        String logMsg = buildMessage(msg);
        Log.i(TAG, logMsg, thr);
        if (writeFile) {
            writeLog2file(INFO, logMsg);

        }
    }

    /**
     * Send a WARN log message
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg)
    {
        if (logLevel > LogLevel.WARN)
            return;
        String logMsg = buildMessage(msg);
        Log.w(TAG_TW, logMsg);
        if (writeFile)
        {
            writeLog2file(WARN, logMsg);
    }
    }
    public static void w(boolean on, String TAG, String msg) {
        if (!on)
            return;
        w(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (logLevel > LogLevel.WARN)
        {
            //如果系统开启对应级别日志则打印
            if(Log.isLoggable(TAG_TW, Log.WARN))
            {
                Log.w(TAG_TW, msg);
            }
            return;
        }
        String logMsg = buildMessage(msg);
        Log.w(TAG, logMsg);
        if (writeFile) {
            writeLog2file(WARN, logMsg);

        }
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr)
    {
        if (logLevel > LogLevel.WARN)
            return;
        String logMsg = buildMessage(msg);
        Log.w(TAG_TW, logMsg, thr);
        if (writeFile)
        {
            writeLog2file(WARN, logMsg);
        }
    }
    public static void w(String TAG, String msg, Throwable thr) {
        if (logLevel > LogLevel.WARN)
        {
            //如果系统开启对应级别日志则打印
            if(Log.isLoggable(TAG_TW, Log.WARN))
            {
                Log.w(TAG_TW, msg);
            }
            return;
        }
        String logMsg = buildMessage(msg);
        Log.w(TAG, logMsg, thr);
        if (writeFile) {
            writeLog2file(WARN, logMsg);

        }
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg)
    {
        if (logLevel > LogLevel.ERROR)
            return;
        String logMsg = buildMessage(msg);
        Log.e(TAG_TW, logMsg);
        if (writeFile)
        {
            writeLog2file(ERROR, logMsg);
        }
    }

    public static void e(boolean on, String TAG, String msg)
    {
        if (logLevel > LogLevel.ERROR)
            return;
        if (!on)
            return;
        e(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (logLevel > LogLevel.ERROR)
        {
            //如果系统开启对应级别日志则打印
            if(Log.isLoggable(TAG_TW, Log.ERROR))
            {
                Log.e(TAG_TW, msg);
            }
            return;
        }
        String logMsg = buildMessage(msg);
        Log.e(TAG, logMsg);
        if (writeFile) {
            writeLog2file(ERROR, logMsg);

        }
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr)
    {
        if (logLevel > LogLevel.ERROR)
        {
            return;
        }
        String logMsg = buildMessage(msg);
        Log.e(TAG_TW, logMsg, thr);
        if (writeFile)
        {
            writeLog2file(ERROR, logMsg);
            printThrowable(TAG_TW, thr);
        }
    }

    public static void e(String TAG, String msg, Throwable thr) {
        if (logLevel > LogLevel.ERROR)
        {
            //如果系统开启对应级别日志则打印
            if(Log.isLoggable(TAG_TW, Log.ERROR))
            {
                Log.e(TAG_TW, msg,thr);
            }
            return;
        }
        String logMsg = buildMessage(msg);
        Log.e(TAG, logMsg, thr);
        if (writeFile) {
            writeLog2file(ERROR, logMsg);
            printThrowable(TAG, thr);
        }
    }

    /**
     * 打印异常的堆栈信息
     *
     * @param thr
     * @Description
     * @author kuangbiao
     */
    public static void printThrowable(String TAG, Throwable thr) {
        StackTraceElement[] stacks = thr.getStackTrace();
        if (stacks != null) {
            String start = TAG + ":printThrowable start ==============";
            writeLog2file(ERROR, start);
            Log.e(TAG, start);
            for (StackTraceElement stack : stacks) {
                String error = stack.toString();
                Log.e(TAG, error);
                writeLog2file(ERROR, error);
            }
            String end = TAG + ":printThrowable end ==============";
            writeLog2file(ERROR, end);
            Log.e(TAG, end);
        }
    }

    /**
     * 或取当前打印日志的类名 、方法名
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace()
                .getStackTrace()[2];

        return new StringBuilder().append(caller.getClassName())
                .append(".")
                .append(caller.getMethodName())
                .append("(): ")
                .append(msg)
                .toString();
    }

    /**
     * 是否允许日志写入文件
     *
     * @return boolean [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isWriteEnable() {
        return writeFile;
    }
}
