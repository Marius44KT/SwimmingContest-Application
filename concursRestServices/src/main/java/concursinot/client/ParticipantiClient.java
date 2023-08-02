package concursinot.client;


import com.example.model.Participant;
import concursinot.ServiceException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import concursinot.ServiceException;

import java.util.concurrent.Callable;

public class ParticipantiClient {
    public static final String URL="http://localhost:8080/app/participants";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Participant[] getAll(){
        return execute(() -> restTemplate.getForObject(URL, Participant[].class));
    }

    public Participant getById(Long id){
        return execute(() -> restTemplate.getForObject(URL + "/" + id.toString(), Participant.class));
    }

    public Participant create(Participant c){
        return execute(() -> restTemplate.postForObject(URL, c, Participant.class));
    }

    public Participant update(Long id, Participant c){
        execute(() -> {
            restTemplate.put(URL + "/" + id.toString(), c);
            return null;
        });
        return c;
    }

    public Participant delete(Long id){
        execute(() -> {
            restTemplate.delete(URL + "/" + id.toString());
            return null;
        });
        return null;
    }
}
