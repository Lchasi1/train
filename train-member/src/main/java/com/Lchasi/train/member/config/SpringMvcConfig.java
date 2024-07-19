package com.Lchasi.train.member.config;

//import com.Lchasi.train.common.interceptor.LogInterceptor;
import com.Lchasi.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

//   @Resource
//   LogInterceptor logInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
//       registry.addInterceptor(logInterceptor);

       // 路径不要包含context-path
       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(//排除
                       "/hello",
                       "/member/send-code",
                       "/member/login"
               );
   }
}
