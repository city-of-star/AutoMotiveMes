package com.automotivemes.common.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 响应工具类，用于构建包含不同状态码和响应体的ResponseEntity对象。
 * 该类提供了一系列静态方法，方便根据不同的业务场景返回相应的HTTP响应。
 */
public class ResponseUtils {

    /**
     * 返回表示成功的响应，状态码为200 OK，响应体包含传入的数据。
     *
     * @param data 响应体数据，类型为泛型T
     * @param <T>  泛型类型，根据实际返回的数据类型确定
     * @return ResponseEntity对象，包含封装了数据的CommonResponse以及状态码200
     */
    public static <T> ResponseEntity<CommonResponse<T>> ok(T data) {
        return buildResponse(HttpStatus.OK, data, null);
    }

    /**
     * 返回表示成功的响应，状态码为200 OK，响应体包含传入的数据。
     *
     * @return ResponseEntity对象，包含封装了数据的CommonResponse以及状态码200
     */
    public static <T> ResponseEntity<CommonResponse<T>> okWithoutData() {
        return buildResponse(HttpStatus.OK, null, null);
    }

    /**
     * 返回表示客户端错误请求的响应，状态码为400 Bad Request，响应体包含错误信息。
     *
     * @param message 错误信息，用于描述请求出现的问题
     * @param <T>     泛型类型，由于此方法主要用于返回错误信息，实际数据类型通常为null
     * @return ResponseEntity对象，包含封装了错误信息的CommonResponse以及状态码400
     */
    public static <T> ResponseEntity<CommonResponse<T>> badRequest(String message) {
        return buildResponse(HttpStatus.BAD_REQUEST, null, null, message);
    }

    /**
     * 返回表示未授权的响应，状态码为401 Unauthorized，响应体包含错误信息。
     *
     * @param message 错误信息，用于提示用户未授权的情况
     * @param <T>     泛型类型，由于此方法主要用于返回错误信息，实际数据类型通常为null
     * @return ResponseEntity对象，包含封装了错误信息的CommonResponse以及状态码401
     */
    public static <T> ResponseEntity<CommonResponse<T>> unauthorized(String message) {
        return buildResponse(HttpStatus.UNAUTHORIZED, null, null, message);
    }

    /**
     * 返回表示服务器内部错误的响应，状态码为500 Internal Server Error，响应体包含错误信息。
     *
     * @param message 错误信息，用于描述服务器内部发生的错误
     * @param <T>     泛型类型，由于此方法主要用于返回错误信息，实际数据类型通常为null
     * @return ResponseEntity对象，包含封装了错误信息的CommonResponse以及状态码500
     */
    public static <T> ResponseEntity<CommonResponse<T>> internalServerError(String message) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, message);
    }

    /**
     * 构建响应的私有方法，此方法用于构建成功响应（状态码为2xx）。
     *
     * @param status  HTTP状态码
     * @param data    响应体数据，类型为泛型T
     * @param headers HTTP响应头，可为null
     * @param <T>     泛型类型，根据实际返回的数据类型确定
     * @return ResponseEntity对象，包含封装了数据的CommonResponse以及指定的状态码和响应头
     */
    private static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, T data, HttpHeaders headers) {
        return buildResponse(status, data, headers, null);
    }

    /**
     * 构建响应的核心私有方法，根据传入的状态码、数据、响应头和错误信息构建ResponseEntity对象。
     *
     * @param status      HTTP状态码
     * @param data        响应体数据，类型为泛型T，成功响应时包含实际数据，错误响应时为null
     * @param headers     HTTP响应头，可为null
     * @param errorMessage 错误信息，仅在非成功响应（状态码非2xx）时使用，用于描述错误详情
     * @param <T>         泛型类型，根据实际返回的数据类型确定
     * @return ResponseEntity对象，包含封装了数据或错误信息的CommonResponse以及指定的状态码和响应头
     */
    private static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus status, T data, HttpHeaders headers, String errorMessage) {
        CommonResponse<T> response;

        // 如果状态码是2xx成功状态码
        if (status.is2xxSuccessful()) {
            if (data != null) response = CommonResponse.success(data);
            else response = CommonResponse.successWithoutData();
        } else {
            // 否则构建错误响应，使用状态码和错误信息（若未传入则使用状态码默认的原因短语）
            response = CommonResponse.error(status.value(), errorMessage!= null? errorMessage : status.getReasonPhrase());
        }

        // 如果响应头为null，创建一个新的HttpHeaders对象
        if (headers == null) headers = new HttpHeaders();

        // 返回包含响应体、响应头和状态码的ResponseEntity对象
        return new ResponseEntity<>(response, headers, status);
    }
}