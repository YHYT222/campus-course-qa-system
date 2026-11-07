package com.campus.qa.config;

import com.campus.qa.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 全局配置：跨域支持、本地图片静态资源映射、拦截器注册
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /** 图片保存目录 */
    @Value("${campus.upload.dir}")
    private String uploadDir;

    /** 图片访问前缀 */
    @Value("${campus.upload.access-prefix}")
    private String accessPrefix;

    /** 配置跨域，允许 Vue 前端（默认 5173 端口）访问后端接口 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /** 把本地上传目录映射为可访问的静态资源路径（图片本地存储方案） */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadDir;
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler(accessPrefix + "**")
                .addResourceLocations("file:" + location);
    }

    /** 注册登录拦截器，放行注册、登录与课程分类查询接口 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/category/list"
                );
    }
}
