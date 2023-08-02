package concursinot.start;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class PropConfiguration {
    @Primary
    @Bean
    public Properties properties(){
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("C:\\Users\\Marius Andreiasi\\JavaProjects\\concursInot\\concursServer\\src\\main\\resources\\server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return null;
        }
        return serverProps;
    }
}
