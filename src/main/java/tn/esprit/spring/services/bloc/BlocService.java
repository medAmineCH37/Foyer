package tn.esprit.spring.services.bloc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc b) { //Cascade
        List<Chambre> chambres = b.getChambres();
        for (Chambre c : chambres) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc addOrUpdate(Bloc b) {
        List<Chambre> chambres = b.getChambres();
        b = blocRepository.save(b);
        for (Chambre chambre : chambres) {
            chambre.setBloc(b);
            chambreRepository.save(chambre);
        }
        return b;
    }

    @Override
    public List<Bloc> findAll() {
        return blocRepository.findAll();
    }

    @Override
    public Bloc findById(long id) {
        return blocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloc not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        Bloc b = blocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloc not found with id: " + id));

        chambreRepository.deleteAll(b.getChambres());
        blocRepository.delete(b);
    }

    @Override
    public void delete(Bloc b) {
        chambreRepository.deleteAll(b.getChambres());
        blocRepository.delete(b);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        var b = blocRepository.findByNomBloc(nomBloc);  // Changed to var
        var chambres = new ArrayList<Chambre>();

        for (Long nu : numChambre) {
            var chambre = chambreRepository.findByNumeroChambre(nu);  // Changed to var
            chambres.add(chambre);
        }

        for (var cha : chambres) {  // Changed to var
            cha.setBloc(b);
            chambreRepository.save(cha);
        }
        return b;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        var b = blocRepository.findByNomBloc(nomBloc);  // Changed to var
        var f = foyerRepository.findByNomFoyer(nomFoyer);  // Changed to var

        b.setFoyer(f);
        return blocRepository.save(b);
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc b) {
        // Activer l'option cascade au niveau parent
        for (Chambre c : b.getChambres()) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc b, String nomFoyer) {
        var f = foyerRepository.findByNomFoyer(nomFoyer);  // Changed to var
        b.setFoyer(f);
        return blocRepository.save(b);
    }

}
