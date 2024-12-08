package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根据uri找到方法名称
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        String methodName =  split[split.length-1];
        // 根据反射找到对应的方法
        Class<? extends BaseController> aClass = this.getClass();
        try {
            Method declaredMethod = aClass.getDeclaredMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this,req,resp);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
