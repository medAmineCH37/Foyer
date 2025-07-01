package tn.esprit.spring.services.etudiant;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EtudiantService implements IEtudiantService {
    EtudiantRepository repo;
    ReservationRepository reservationRepository;

    @Override
    public Etudiant addOrUpdate(Etudiant e) {
        return repo.save(e);
    }

    @Override
    public List<Etudiant> findAll() {
        return repo.findAll();
    }

    @Override
    public Etudiant findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Etudiant not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Etudiant e) {
        repo.delete(e);
    }

    @Override
    public List<Etudiant> selectJPQL(String nom) {
        return repo.selectJPQL(nom);
    }

    @Override
    public void affecterReservationAEtudiant(String idR, String nomE, String prenomE) {
        // 1- Récupérer les objets
        Reservation res = reservationRepository.findById(idR)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + idR));

        Etudiant et = repo.getByNomEtAndPrenomEt(nomE, prenomE);

        // 2- Affectation
        et.getReservations().add(res);

        // 3- Sauvegarde
        repo.save(et);
    }

    @Override
    public void desaffecterReservationAEtudiant(String idR, String nomE, String prenomE) {
        // 1- Récupérer les objets
        Reservation res = reservationRepository.findById(idR)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + idR));

        Etudiant et = repo.getByNomEtAndPrenomEt(nomE, prenomE);

        // 2- Désaffectation
        et.getReservations().remove(res);

        // 3- Save du parent
        repo.save(et);
    }
}
