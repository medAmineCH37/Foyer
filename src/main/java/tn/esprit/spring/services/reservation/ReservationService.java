package tn.esprit.spring.services.reservation;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService {
    ReservationRepository repo;
    ChambreRepository chambreRepository;
    EtudiantRepository etudiantRepository;

    @Override
    public Reservation addOrUpdate(Reservation r) {
        return repo.save(r);
    }

    @Override
    public List<Reservation> findAll() {
        return repo.findAll();
    }

    @Override
    public Reservation findById(String id) {
        Optional<Reservation> reservationOptional = repo.findById(id);
        if (reservationOptional.isPresent()) {
            return reservationOptional.get();
        } else {
            return null; // or handle differently
        }
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Reservation r) {
        repo.delete(r);
    }

    public LocalDate getDateDebutAU() {
        LocalDate dateDebutAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        }
        return dateDebutAU;
    }

    public LocalDate getDateFinAU() {
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        return dateFinAU;
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant
            (Long numChambre, long cin) {
        // Récupération de la chambre et de l'étudiant
        var chambre = chambreRepository.findByNumeroChambre(numChambre);
        var etudiant = etudiantRepository.findByCin(cin);

        // Compter le nombre de réservations existantes
        int nombreReservations = chambreRepository.
                countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween
                        (chambre.getIdChambre(), getDateDebutAU(), getDateFinAU());

        // Vérification de la capacité de la chambre
        var ajout = false;
        int capaciteMaximale = switch (chambre.getTypeC()) {
            case SIMPLE -> 1;
            case DOUBLE -> 2;
            case TRIPLE -> 3;
        };

        if (nombreReservations < capaciteMaximale) {
            ajout = true;
        } else {
            log.info("Chambre " + chambre.getTypeC() + " remplie !");
        }

        if (ajout) {
            // Création de la réservation
            String idReservation = "" + getDateDebutAU().getYear() + "/" + getDateFinAU().getYear() + "-" + chambre.getBloc().getNomBloc() + "-"
                    + chambre.getNumeroChambre() + "-" + etudiant.getCin();

            var reservation = Reservation.builder()
                    .estValide(true)
                    .anneeUniversitaire(LocalDate.now())
                    .idReservation(idReservation)
                    .build();

            // Affectation de l'étudiant à la réservation
            reservation.getEtudiants().add(etudiant);

            // Sauvegarde de la réservation
            reservation = repo.save(reservation);

            // Affectation de la réservation à la chambre
            chambre.getReservations().add(reservation);
            chambreRepository.save(chambre);

            return reservation;
        }

        // Retourner null ou lever une exception plutôt que de retourner une nouvelle réservation vide
        return null; // Ou vous pouvez lever une exception pour indiquer que l'ajout a échoué
    }


    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        return repo.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation r = repo.findByEtudiantsCinAndEstValide(cinEtudiant,
                true);
        var c = chambreRepository.findByReservationsIdReservation
                (r.getIdReservation());
        c.getReservations().remove(r);
        chambreRepository.save(c);
        repo.delete(r);
        return "La réservation " + r.getIdReservation()
                + " est annulée avec succés";
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        // Safe way to get Reservation with proper error handling
        Reservation r = repo.findById(idRes)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + idRes));

        // Safe way to get Chambre with proper error handling
        Chambre c = chambreRepository.findById(idChambre)
                .orElseThrow(() -> new EntityNotFoundException("Chambre not found with ID: " + idChambre));

        // Business logic
        c.getReservations().add(r);
        chambreRepository.save(c);
    }

    @Override
    public void deaffectReservationAChambre(String idRes, long idChambre) {
        // Safe retrieval with proper error handling
        Reservation r = repo.findById(idRes)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + idRes));

        // Safe retrieval with proper error handling
        Chambre c = chambreRepository.findById(idChambre)
                .orElseThrow(() -> new EntityNotFoundException("Chambre not found with ID: " + idChambre));

        // Business logic
        if (c.getReservations().remove(r)) {
            chambreRepository.save(c);
        }
        // Optional: You might want to handle case where reservation wasn't in the list
    }

    @Override
    public void annulerReservations() {
        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        for (Reservation reservation : repo.findByEstValideAndAnneeUniversitaireBetween
                (true, dateDebutAU, dateFinAU)) {
            reservation.setEstValide(false);
            repo.save(reservation);
            log.info("La reservation " + reservation.getIdReservation() + " est annulée automatiquement");
        }
    }

}
