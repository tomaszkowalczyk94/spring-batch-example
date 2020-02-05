package org.tomaszkowalczyk94.springbatchexample;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public ItemReader<Integer> reader(@Value("#{jobParameters['input.file.name']}") String test) {
        return new ListItemReader<>(Arrays.asList(11, 22, 33));
    }

    @Bean
    public ItemProcessor<Integer, Integer> processor() {
        return item -> item*100;
    }

    @Bean
    public ItemWriter<Integer> writer() {
        return items -> {
            System.out.println("WRITER RUN");
            items.forEach(System.out::println);
        };
    }

    @Bean
    public Job testJob( Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                //.listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemWriter<Integer> writer, ItemReader<Integer> reader) {
        return stepBuilderFactory.get("step1")
                .<Integer, Integer> chunk(30)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

}
