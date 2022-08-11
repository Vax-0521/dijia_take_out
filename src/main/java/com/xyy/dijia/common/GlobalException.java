package com.xyy.dijia.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
@Slf4j
public class GlobalException {

    /**
     * 统一异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());

        if (e.getMessage().contains("Duplicate entry")){
            String[] s = e.getMessage().split(" ");
            String msg = s[2]+"已存在,请重新填写";
            R.error(msg);
            return R.error(msg);
        }
        return R.error("添加失败");
    }


    /**
     * 自定义业务异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException e){
        log.error(e.getMessage());

        return R.error(e.getMessage());
    }
}
