package cn.edu.hbpu.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局处理异常
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public R<String> exceptionHandler(Exception exception){
        log.info(exception.getMessage());
        return R.error("失败了！");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.info(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
