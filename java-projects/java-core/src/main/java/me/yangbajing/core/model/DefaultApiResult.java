package me.yangbajing.core.model;

import lombok.Data;
import me.yangbajing.core.ApiResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:19:50
 */
@Data
public class DefaultApiResult implements ApiResult {
    public static Map<Object, Object> EmptyData = new HashMap<>();
    private int status;
    private String msg;
    private String sign;
    private Date curDate;
    private Object obj;

    public static DefaultApiResult success() {
        return success(EmptyData);
    }

    public static DefaultApiResult success(Object obj) {
        return success(obj, "");
    }

    public static DefaultApiResult success(Object obj, String msg) {
        return success(obj, msg, "");
    }

    public static DefaultApiResult success(Object obj, String msg, String sign) {
        return success(obj, msg, sign, new Date());
    }

    public static DefaultApiResult success(Object obj, String msg, String sign, Date curDate) {
        DefaultApiResult result = new DefaultApiResult();
        result.setObj(obj);
        result.setMsg(msg);
        result.setSign(sign);
        result.setCurDate(curDate);
        result.setStatus(200);
        return result;
    }

    public static DefaultApiResult failure(String msg) {
        return failure(msg, "");
    }

    public static DefaultApiResult failure(String msg, String sign) {
        return failure(msg, sign, new Date());
    }

    public static DefaultApiResult failure(String msg, Date curDate) {
        return failure(msg, "", curDate);
    }

    public static DefaultApiResult failure(String msg, String sign, Date curDate) {
        return failure(msg, sign, curDate, EmptyData);
    }

    public static DefaultApiResult failure(String msg, String sign, Date curDate, Object obj) {
        DefaultApiResult result = new DefaultApiResult();
        result.setMsg(msg);
        result.setSign(sign);
        result.setCurDate(curDate);
        result.setObj(obj);
        result.setStatus(500);
        return result;
    }

    public static DefaultApiResult from(ApiResult apiResult) {
        DefaultApiResult result = new DefaultApiResult();
        result.setStatus(apiResult.getSuccess() ? 200 : 500);
        result.setMsg(apiResult.getMsg());
        result.setObj(apiResult.getObj());
        result.setCurDate(new Date(System.currentTimeMillis()));
        result.setSign("");
        return result;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
