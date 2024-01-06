package xyz.funnyboy.yygh.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.yygh.common.result.Result;

/**
 * 全局异常处理程序
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 */
@ControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e) {
        e.printStackTrace();
        return Result.build(e.getCode(), e.getMessage());
    }
}
