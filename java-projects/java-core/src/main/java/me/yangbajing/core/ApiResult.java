package me.yangbajing.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.yangbajing.core.model.DefaultApiResult;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:19:21
 */
public interface ApiResult {
    int STATUS_SUCCESS = 200;
    int STATUS_NOT_FOUND = 404;
    int STATUS_BAD_REQUEST = 400;
    String MSG_SUCCESS = "操作成功";
    String MSG_ERROR = "操作失败";
    String MSG_NOT_FOUND = "数据未找到";
    JsonNode EmptyData = new ObjectNode(JsonNodeFactory.instance);

    int getStatus();

    String getMsg();

    Object getObj();

    static ApiResult create(int status, String msg, Object obj) {
        DefaultApiResult result = new DefaultApiResult();
        result.setStatus(status);
        result.setMsg(msg);
        result.setObj(obj);
        return result;
    }

    static ApiResult success() {
        return success(MSG_SUCCESS);
    }

    static ApiResult success(String message) {
        return success(EmptyData, message);
    }

    static ApiResult success(Object obj) {
        return success(obj, "");
    }

    static ApiResult success(Object obj, String message) {
        return create(STATUS_SUCCESS, message, obj);
    }

    static ApiResult failure() {
        return failure(MSG_ERROR);
    }

    static ApiResult failure(String message) {
        return failure(message, EmptyData);
    }

    static ApiResult failure(String message, Object obj) {
        return create(500, message, obj);
    }

    static ApiResult notFound() {
        return notFound(MSG_NOT_FOUND);
    }

    static ApiResult notFound(String message) {
        return create(STATUS_NOT_FOUND, message, EmptyData);
    }

    static ApiResult badRequest(String message, Object obj) {
        return create(STATUS_BAD_REQUEST, message, obj);
    }

    static ApiResult from(boolean ret) {
        return ret ? success() : failure("");
    }

    default boolean getSuccess() {
        int status = getStatus();
        return (status >= STATUS_SUCCESS && status < 299) || status == 0;
    }
}
