package xyz.chthollywn.ctool.rest;

import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.chthollywn.ctool.excepion.CToolException;
import xyz.chthollywn.ctool.log.annotation.GlobalExceptionLog;

import javax.servlet.http.HttpServletRequest;

@GlobalExceptionLog
public class GlobalErrorHandler {
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        return "error";
    }

    @ExceptionHandler(CToolException.class)
    public String testhandler(CToolException cToolException) {
        return "error";
    }
}
