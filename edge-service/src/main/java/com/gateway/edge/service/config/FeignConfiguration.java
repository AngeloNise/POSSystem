package com.gateway.edge.service.config;

import feign.Contract;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

/**
 * Created by Default on 22/04/2022.
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters){
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }
}
