package com.yakusa.reggie.filter;
import com.alibaba.fastjson.JSON;
import com.yakusa.reggie.common.BaseContext;
import com.yakusa.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

    //1.获取请求的URI
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] ignorePaths={  // /backend/index.html
        "/employee/login",
        "/employee/logout",
        "/backend/**",
        "/front/**",
        "/common/**",
        "/user/sendMsg",
        "/user/login",
        };

     //2.判断请求路径是否需要处理
     boolean match=check(ignorePaths,requestURI);


     //3.如果不需要处理,则直接放行
    if (match){
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.如果需要处理,则判断用户是否登录(Pc端)
        if (request.getSession().getAttribute("employee")!=null){

            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.saveUserId(empId);

            filterChain.doFilter(request,response);
          return;
        }

        //4-2.如果需要处理,则判断用户是否登录(移动端)
        if (request.getSession().getAttribute("user")!=null){

            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.saveUserId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录,则返回未登录结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }
    //路径匹配器,支持通配符,检查请求路径是否需要处理
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
          boolean match=  PATH_MATCHER.match(url,requestURI);
                if (match){
                    return true;
                }
            }
        return false;
    }
}
