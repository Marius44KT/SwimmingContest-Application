package concursinot.start;


import com.example.model.Distanta;
import com.example.model.Participant;
import com.example.model.Stil;
import concursinot.ServiceException;
import concursinot.client.ParticipantiClient;

public class StartRestClient {
    private final static ParticipantiClient client = new ParticipantiClient();
    public static void main(String[] args){
        //get one
        show(() -> System.out.println(client.getById(3L)));
        System.out.println("1");
        // get all
        show(() -> {
            Participant[] p=client.getAll();
            for(Participant part : p){
                System.out.println(part);
            }
        });
        System.out.println("2");
        Participant newParticipant = new Participant(0L, Distanta.dist200m, Stil.liber);
        // create
        show(() -> {
            System.out.println(client.create(newParticipant));
        });
        System.out.println("3");
        Participant[] p = client.getAll();
        System.out.println("4");
        Long finalI=20L;
        show(() -> System.out.println(client.getById(finalI)));
        System.out.println("5");
        // update
        newParticipant.setDistanta(Distanta.dist1500m);
        newParticipant.setStil(Stil.flutur);
        show(() ->{
            System.out.println(client.update(finalI, newParticipant));
        });
        System.out.println("6");
        // delete
        show(() -> {
            System.out.println(client.delete(finalI));
        });
        show(() -> System.out.println(client.getById(finalI)));
    }


    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception"+ e);
        }
    }
}