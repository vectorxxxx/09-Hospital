package xyz.funnyboy.yygh.common.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;

/**
 * yygh 异常
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see RuntimeException
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class YyghException extends RuntimeException
{

    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param message
     * @param code
     */
    public YyghException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum
     */
    public YyghException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "YyghException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}
