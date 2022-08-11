package com.xyy.dijia.filter;

import com.alibaba.fastjson.JSON;
import com.xyy.dijia.common.BaseContext;
import com.xyy.dijia.common.R;
import com.xyy.dijia.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义过滤器，用来检查用户是否登录
 *
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();  //backend/index.html

        log.info("拦截到请求",requestURI);

        //不需要处理的请求路径
        String[] urls = new String[]{"/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端用户发送验证码
                "/user/login"  //移动端登录
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,servletResponse);
            return;
        }
        //4-1.判断登录状态，已登录放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已经登陆，用户id为：{}",request.getSession().getAttribute("employee"));

            //利用BaseContext工具类获取id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id：{}",id);

            filterChain.doFilter(request,servletResponse);
            return;
        }
        //4-2.判断登录状态，已登录放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已经登陆，用户id为：{}",request.getSession().getAttribute("user"));

            //利用BaseContext工具类获取id
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id = Thread.currentThread().getId();
            log.info("线程id：{}",id);

            filterChain.doFilter(request,servletResponse);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录，返回未登录结果(通过输出流方式向客户端页面响应数据)
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，检测本次请求是否需要放行
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
