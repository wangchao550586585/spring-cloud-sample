package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
//turbine配置
@EnableTurbine
@EnableHystrixDashboard
public class TurbineApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(TurbineApp.class,args);
    }
}
