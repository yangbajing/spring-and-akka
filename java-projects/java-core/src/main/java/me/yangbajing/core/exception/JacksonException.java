package me.yangbajing.core.exception;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:18:51
 */
public class JacksonException extends DataException {

    public JacksonException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
        this.status = 500;
    }

    public JacksonException(String message) {
        super(message);
        this.status = 500;
    }

    public JacksonException(Throwable cause) {
        super(cause);
        this.status = 500;
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
        this.status = 500;
    }
}
