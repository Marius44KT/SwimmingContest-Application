package concursinot;


import com.example.model.Participant;
import com.example.persistence.Database.ParticipantiDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/app/participants")
//@CrossOrigin(origins="http://localhost:3000")
public class ParticipantController {
    private static final String template = "Hello, %s!";

    @Autowired
    private ParticipantiDatabaseRepository repo_participanti;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue = "World") String name){
        return String.format(template, name);
    }

    @RequestMapping( method = RequestMethod.GET)
    public Participant[] getAll(){
        System.out.println("Get all participants");
        Map<Long,Participant> map_participanti=repo_participanti.findAll();
        List<Participant> l=new ArrayList<>();
        for(Participant p: map_participanti.values())
            l.add(p);
        return l.toArray(new Participant[0]);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id){
        System.out.println("Get by id : " + id);
        Participant p=repo_participanti.findOne(id);
        if(p == null){
            return new ResponseEntity<String>("Participant not found", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<Participant>(p, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Participant create(@RequestBody Participant p){
        System.out.println("Creating user");
        p.setId(repo_participanti.getNewId());
        repo_participanti.save(p);
        return p;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Participant update(@PathVariable Long id, @RequestBody Participant participant){
        System.out.println("Updating participant");
        participant.setId(id);
        repo_participanti.update(participant);
        return participant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        System.out.println("Deleting participant : "+id.toString());
        try{
            repo_participanti.delete(id);
            return new ResponseEntity<Participant>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl exception (DELETE )");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
