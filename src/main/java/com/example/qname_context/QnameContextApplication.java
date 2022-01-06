package com.example.qname_context;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableBatchProcessing
@Import(value = JobConfiguration.class)
public class QnameContextApplication implements ApplicationRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public static void main(String[] args) {
        SpringApplication.run(QnameContextApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
        jobLauncher.run(job, new JobParametersBuilder().toJobParameters());

    }
}
