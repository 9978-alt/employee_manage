package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class CrossFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Access-Control-Allow-Origin","*");  // 允许跨域
        resp.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,OPTIONS,DELETE,HEAD");// 允许跨域的方法
        resp.setHeader("Access-Control-Max-Age","3600");    // 间隔时间
        resp.setHeader("Access-Control-Allow-Headers","access-control-allow-origin,authority,content-type,version-info,X-Requested-With,token");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if(!req.getMethod().equalsIgnoreCase("OPTIONS")){
            filterChain.doFilter(req,resp);
        }
    }
}
