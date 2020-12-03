package me.yangbajing.core.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.yangbajing.core.ApiResult;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:19:04
 */
@JsonIgnoreProperties(value = {"cause", "stackTrace", "localizedMessage", "message"})
public class DataException extends RuntimeException implements ApiResult {
    private static final long serialVersionUID = 1L;

    protected int status = 500;

    public DataException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }

    public DataException(String message) {
        this(message, null, false, true);
    }

    public DataException(Throwable cause) {
        super(cause.getMessage(), cause, false, true);
    }

    public DataException(String message, Throwable cause) {
        this(message, cause, false, true);
    }

    public DataException() {
        this("Runtime DataException.");
    }

    @Override
    public Object getObj() {
        return null;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMsg() {
        return getMessage();
    }

    public DataException withStatus(int status) {
        this.status = status;
        return this;
    }

    public static DataException badRequest(String message) {
        return new DataException(message).withStatus(400);
    }

    @Override
    public final boolean getSuccess() {
        int status = getStatus();
        return status >= 200 && status < 299;
    }
}
