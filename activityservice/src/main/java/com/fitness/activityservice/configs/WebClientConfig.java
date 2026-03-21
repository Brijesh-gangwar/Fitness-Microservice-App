package com.fitness.activityservice.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return  WebClient.builder();
    }


    @Bean
    public WebClient webServiceWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${services.user.base-url:http://USER-SERVICE}") String userServiceBaseUrl
    ){
        return webClientBuilder.baseUrl(userServiceBaseUrl).build();
    }

}
