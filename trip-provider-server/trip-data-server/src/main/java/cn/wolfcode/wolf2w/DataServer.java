package cn.wolfcode.wolf2w;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //启动定时任务
public class DataServer {
    public static void main(String[] args) {
        SpringApplication.run(DataServer.class, args).start();
    }
}
