package com.smartorder.orderservice.infrastructure.catalog;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.smartorder.orderservice.infrastructure.catalog")
class CatalogConfiguration {}

