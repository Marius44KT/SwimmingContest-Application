package concursinot;

import com.example.model.Angajat;
import com.example.persistence.Database.AngajatiDatabaseRepository;
import com.example.persistence.Database.AngajatiDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/app/employees")
//@CrossOrigin(origins="http://localhost:3000")
public class AngajatController {
    private static final String template = "Hello, %s!";

    @Autowired
    private AngajatiDatabaseRepository repo_angajati;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue = "World") String name){
        return String.format(template, name);
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody Angajat angajat) {
        String email=angajat.getEmail();
        String password=angajat.getParola();
        Angajat a=repo_angajati.findOneByEmailAndPassword(email,password);
        if (a!=null) {
            // Login successful
            System.out.println("Login successful");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // Login failed
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }



    @RequestMapping( method = RequestMethod.GET)
    public Angajat[] getAll(){
        System.out.println("Get all employees");
        Map<Long,Angajat> map_angajati=repo_angajati.findAll();
        List<Angajat> l=new ArrayList<>();
        for(Angajat a: map_angajati.values())
            l.add(a);
        return l.toArray(new Angajat[0]);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id){
        System.out.println("Get by id : " + id);
        Angajat a=repo_angajati.findOne(id);
        if(a==null){
            return new ResponseEntity<String>("Employee not found", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<Angajat>(a, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Angajat create(@RequestBody Angajat a){
        System.out.println("Creating employee");
        a.setId(repo_angajati.getNewId());
        repo_angajati.save(a);
        return a;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Angajat update(@PathVariable Long id, @RequestBody Angajat angajat){
        System.out.println("Updating employee");
        angajat.setId(id);
        repo_angajati.update(angajat);
        return angajat;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        System.out.println("Deleting employee : "+id.toString());
        try{
            repo_angajati.delete(id);
            return new ResponseEntity<Angajat>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl exception (DELETE )");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
