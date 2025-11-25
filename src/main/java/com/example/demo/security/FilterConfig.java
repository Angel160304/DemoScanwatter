// package com.example.demo.config;

// import com.example.demo.security.FirebaseAuthFilter;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class FilterConfig {

//     private final FirebaseAuthFilter firebaseAuthFilter;

//     public FilterConfig(FirebaseAuthFilter firebaseAuthFilter) {
//         this.firebaseAuthFilter = firebaseAuthFilter;
//     }

//     @Bean
//     public FilterRegistrationBean<FirebaseAuthFilter> registrationBean() {
//         FilterRegistrationBean<FirebaseAuthFilter> registrationBean = new FilterRegistrationBean<>();
//         registrationBean.setFilter(firebaseAuthFilter);

//         // Aplicar el filtro a todas las rutas
//         registrationBean.addUrlPatterns("/*");

//         return registrationBean;
//     }
// }
