package tn.esprit.spring.restcontrollers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.services.etudiant.IEtudiantService;

import java.util.List;

@RestController
@RequestMapping("etudiant")
@AllArgsConstructor
public class EtudiantRestController {
    IEtudiantService service;

    @PostMapping("addOrUpdate")
    public Etudiant addOrUpdate(@RequestBody Etudiant e) {
        return service.addOrUpdate(e);
    }

    @GetMapping("findAll")
    public List<Etudiant> findAll() {
        return service.findAll();
    }

    @GetMapping("findById")
    public Etudiant findById(@RequestParam long id) {
        return service.findById(id);
    }

    @DeleteMapping("delete")
    public void delete(@RequestBody Etudiant e) {
        service.delete(e);
    }

    @DeleteMapping("deleteById")
    public void deleteById(@RequestParam long id) {
        service.deleteById(id);
    }

    @GetMapping("selectJPQL")
    public List<Etudiant> selectJPQL(@RequestParam String nom){
        return service.selectJPQL(nom);
    }
}
