package com.videoapp.libcommon.bean;

/**
 * 内部组件的调用结果基类
 *
 * @author mos
 * @date 2017.12.04
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class BaseActionResult {
    /** 成功 */
    public static final int CODE_SUCCESS = 1000;
    /** 成功 -- 异步回调 */
    public static final int CODE_SUCCESS_ASYNC_RESULT = 1010;
    /** 错误(通用) */
    public static final int CODE_ERROR = 1001;
    /** 参数错误 */
    public static final int CODE_PARAM_ERROR = 1002;
    /** URL不可用 */
    public static final int CODE_URL_NOT_AVAILABLE = 1003;
    /** 用户未登录 */
    public static final int CODE_USER_NOT_LOGIN = 1004;
}
