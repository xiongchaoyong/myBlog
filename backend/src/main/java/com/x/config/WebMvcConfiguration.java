package com.x.config;

//import com.x.interceptor.JwtTokenUserInterceptor;
import com.x.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {


    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/ai/**")
                .addPathPatterns("/user/**")

                //文章
                .excludePathPatterns("/user/articles")
                .excludePathPatterns("/user/articles/*")
                .excludePathPatterns("/user/articles/top")
                .excludePathPatterns("/user/articles/*/like")
                .excludePathPatterns("/user/articles/*/view")
                .excludePathPatterns("/user/articles/stats/last3days")
                .excludePathPatterns("/user/articles/getMyArticleCount")


                //分类
                .excludePathPatterns("/user/categories")
                .excludePathPatterns("/user/categories/with-count")

                .excludePathPatterns("/user/post/getPosts")
                .excludePathPatterns("/user/post/getFivePosts")
                .excludePathPatterns("/user/post/getMyPostsCount")
                .excludePathPatterns("/user/post/getPostsCount")

                //用户
                .excludePathPatterns("/user/user/getUserCount")

                .excludePathPatterns("/user/timeline")
                .excludePathPatterns("/user/tool/getAll")
                .excludePathPatterns("/user/user/login").
                excludePathPatterns("/user/user/register")
                .excludePathPatterns("/user/user/github/login")
        ;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Knife4J 静态资源映射
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // Springdoc 静态资源映射
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }

//    http://localhost:8080/doc.html


}