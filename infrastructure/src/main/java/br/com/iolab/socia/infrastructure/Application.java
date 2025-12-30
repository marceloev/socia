package br.com.iolab.socia.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@Slf4j
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class Application {
    public static void main (String[] args) {
        log.info("Iniciando aplicação...");
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(Application.class, args);
    }
}