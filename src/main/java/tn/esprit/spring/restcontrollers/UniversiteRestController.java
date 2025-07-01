package tn.esprit.spring.restcontrollers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.services.universite.IUniversiteService;

import java.util.List;

@RestController
@RequestMapping("universite")
@AllArgsConstructor
public class UniversiteRestController {
    IUniversiteService service;

    @PostMapping("addOrUpdate")
    public Universite addOrUpdate(@RequestBody Universite u) {
        return service.addOrUpdate(u);
    }

    @GetMapping("findAll")
    public List<Universite> findAll() {
        return service.findAll();
    }

    @GetMapping("findById")
    public Universite findById(@RequestParam long id) {
        return service.findById(id);
    }

    @DeleteMapping("delete")
    public void delete(@RequestBody Universite u) {
        service.delete(u);
    }

    @DeleteMapping("deleteById")
    public void deleteById(@RequestParam long id) {
        service.deleteById(id);
    }

    @PostMapping("ajouterUniversiteEtSonFoyer")
    public Universite ajouterUniversiteEtSonFoyer(@RequestBody Universite u)
    {
        return service.ajouterUniversiteEtSonFoyer(u);
    }


}
