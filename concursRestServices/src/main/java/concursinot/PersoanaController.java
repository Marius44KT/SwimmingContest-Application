package concursinot;

import com.example.model.Persoana;
import com.example.persistence.Database.PersoaneDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/app/persons")
//@CrossOrigin(origins="http://localhost:3000")
public class PersoanaController {
    private static final String template = "Hello, %s!";

    @Autowired
    private PersoaneDatabaseRepository repo_persoane;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue = "World") String name){
        return String.format(template, name);
    }

    @RequestMapping( method = RequestMethod.GET)
    public Persoana[] getAll(){
        System.out.println("Get all persons");
        Map<Long,Persoana> map_persoane=repo_persoane.findAll();
        List<Persoana> l=new ArrayList<>();
        for(Persoana p: map_persoane.values())
            l.add(p);
        return l.toArray(new Persoana[0]);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id){
        System.out.println("Get by id : " + id);
        Persoana p=repo_persoane.findOne(id);
        if(p==null){
            return new ResponseEntity<String>("Person not found", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<Persoana>(p, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Persoana create(@RequestBody Persoana p){
        System.out.println("Creating person");
        p.setId(repo_persoane.getNewId());
        repo_persoane.save(p);
        return p;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Persoana update(@PathVariable Long id, @RequestBody Persoana persoana){
        System.out.println("Updating person");
        persoana.setId(id);
        repo_persoane.update(persoana);
        return persoana;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        System.out.println("Deleting person: "+id.toString());
        try{
            repo_persoane.delete(id);
            return new ResponseEntity<Persoana>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl exception (DELETE )");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}