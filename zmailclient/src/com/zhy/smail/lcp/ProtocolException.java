package com.zhy.smail.lcp;

/**
 * Created by wenliz on 2017/1/20.
 */
public class ProtocolException extends Exception {
    private static final long serialVersionUID = 1L;
    //private static Log log = LogFactory.getLog(ProtocolException.class);

    /**
     * 错误码
     */
    private int errorCode = 0;

    /**
     * 错误信息
     */
    private String errorMessage = "";

    /**
     * 带参数的构造函数
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    public ProtocolException(String errorMessage) {
        this(0, errorMessage);
    }

    /**
     * 带参数的构造函数
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     */
    public ProtocolException(int errorCode, String errorMessage) {
        super(errorMessage);

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        //String record = String.format("协议类库异常, 错误代码: %d, 错误信息: %s", errorCode, errorMessage);
        //log.error(record);
    }

    public int getErrorCode() {
        return errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

}
