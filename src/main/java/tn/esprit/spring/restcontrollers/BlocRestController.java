package tn.esprit.spring.restcontrollers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.services.bloc.IBlocService;

import java.util.List;

@RestController
@RequestMapping("bloc")
@AllArgsConstructor
public class BlocRestController {
    IBlocService service;

    @PostMapping("addOrUpdate")
    public Bloc addOrUpdate(@RequestBody Bloc b) {
        return service.addOrUpdate(b);
    }

    @GetMapping("findAll")
    public List<Bloc> findAll() {
        return service.findAll();
    }

    @GetMapping("findById")
    public Bloc findById(@RequestParam long id) {
        return service.findById(id);
    }

    @DeleteMapping("delete")
    public void delete(@RequestBody Bloc b) {
        service.delete(b);
    }

    @DeleteMapping("deleteById")
    public void deleteById(@RequestParam long id) {
        service.deleteById(id);
    }

    @PutMapping("affecterChambresABloc")
    public Bloc affecterChambresABloc(@RequestBody List<Long> numChambre, @RequestParam String nomBloc) {
        return service.affecterChambresABloc(numChambre, nomBloc);
    }
    // ...............?nomFoyer=....&nomBloc=....
    @PutMapping("affecterBlocAFoyer")
    public Bloc affecterBlocAFoyer(@RequestParam String nomBloc, @RequestParam String nomFoyer) {
        return service.affecterBlocAFoyer(nomBloc, nomFoyer);
    }

    // .............../Foyer des jasmins/Bloc G
    @PutMapping("affecterBlocAFoyer2/{nomFoyer}/{nomBloc}")
    public Bloc affecterBlocAFoyer2(@PathVariable String nomBloc, @PathVariable String nomFoyer) {
        return service.affecterBlocAFoyer(nomBloc, nomFoyer);
    }

    @PostMapping("ajouterBlocEtSesChambres")
    public Bloc ajouterBlocEtSesChambres(@RequestBody Bloc b) {
        return service.ajouterBlocEtSesChambres(b);
    }

    @PostMapping("ajouterBlocEtAffecterAFoyer/{nomF}")
    public Bloc ajouterBlocEtAffecterAFoyer(@RequestBody Bloc b,@PathVariable String nomF) {
        return service.ajouterBlocEtAffecterAFoyer(b,nomF);
    }
}
