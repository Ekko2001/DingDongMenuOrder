package com.yakusa.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
 定义全局异常处理类
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());//打印异常信息 方便调试
        //判断异常信息中是否包含Duplicate entry关键字
        if (ex.getMessage().contains("Duplicate entry")){
            //获取重复的值
            String[] split = ex.getMessage().split(" ");
            //拼接错误信息
            String msg = split[2]+"已存在";
            //返回错误信息
           return R.error(msg);
        }
        //返回未知错误
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());//打印异常信息 方便调试

        return R.error(ex.getMessage());//返回错误信息
    }



}
