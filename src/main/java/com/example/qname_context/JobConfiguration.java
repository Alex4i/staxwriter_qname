package com.example.qname_context;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import java.nio.file.Files;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@Configuration
public class JobConfiguration {

    private static final XMLEventFactory XML_EVENT_FACTORY = XMLEventFactory.newInstance();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("staxJob")
                .start(step()).build();
    }

    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Element,Element>chunk(1)
                .reader(reader())
                .writer(writer()).build();
    }

    @Bean(destroyMethod = "")
    public ItemReader<Element> reader(){
        return new ItemReader<>() {
            boolean exhausted;

            @Override
            public Element read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (!exhausted) {
                    exhausted = true;
                    return new Element("0");
                }
                return null;
            }
        };
    }

    @Bean(destroyMethod = "")
    public StaxEventItemWriter<Element> writer() throws Exception {
        StaxEventItemWriter<Element> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setHeaderCallback(header());
        staxEventItemWriter.setFooterCallback(footer());

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Element.class);
        marshaller.afterPropertiesSet();

        staxEventItemWriter.setMarshaller(marshaller);
        Resource resource = new FileSystemResource(Files.createTempFile("tmp",""));
        staxEventItemWriter.setResource(resource);
        staxEventItemWriter.afterPropertiesSet();
        return staxEventItemWriter;
    };

    public StaxWriterCallback header(){
        return writer -> {
            try {
                writer.add(XML_EVENT_FACTORY.createStartElement("","","UnclosedHeader"));
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        };
    }

    public StaxWriterCallback footer(){
        return writer -> {
            try {
                writer.add(XML_EVENT_FACTORY.createEndElement("","","UnclosedHeader"));
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        };
    }

    @XmlAccessorType(FIELD)
    @XmlRootElement(name = "Element")
    public static class Element {

        @XmlAttribute
        String id;

        public Element(String id){
            this.id = id;
        }

        public Element(){}
    }
}
