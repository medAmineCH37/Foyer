package tn.esprit.spring.services.chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    private final ChambreRepository chambreRepository;
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        long compteur = 0;
        List<Chambre> list = chambreRepository.findAll();
        for (Chambre chambre : list) {
            if (chambre.getBloc().getIdBloc() == idBloc
                    && chambre.getTypeC().equals(type)) {
                compteur++;
            }
        }
        return compteur;
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        // Get current academic year dates
        var current = YearMonth.now();
        LocalDate dateDebutAU;
        LocalDate dateFinAU;

        if (current.getMonthValue() <= 7) { // Before August
            dateDebutAU = LocalDate.of(current.getYear() - 1, 9, 15);
            dateFinAU = LocalDate.of(current.getYear(), 6, 30);
        } else { // September onwards
            dateDebutAU = LocalDate.of(current.getYear(), 9, 15);
            dateFinAU = LocalDate.of(current.getYear() + 1, 6, 30);
        }

        List<Chambre> listChambreDispo = new ArrayList<>();

        for (Chambre c : repo.findAll()) {
            if (c.getTypeC().equals(type) &&
                    c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer)) {

                long reservationCount = c.getReservations().stream()
                        .filter(r -> !r.getAnneeUniversitaire().isBefore(dateDebutAU) &&
                                !r.getAnneeUniversitaire().isAfter(dateFinAU))
                        .count();

                if (isChambreAvailable(c.getTypeC(), reservationCount)) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    private boolean isChambreAvailable(TypeChambre type, long reservationCount) {
        return switch (type) {
            case SIMPLE -> reservationCount == 0;
            case DOUBLE -> reservationCount < 2;
            case TRIPLE -> reservationCount < 3;
            default -> false;
        };
    }

    @Scheduled(cron = "0 * * * * *")  // Fixed cron expression syntax
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() +  // Fixed typo: getMomBloc → getNomBloc
                    " ayant une capacité " + b.getCapaciteBloc());

            if (!b.getChambres().isEmpty()) {  // Using isEmpty() instead of size() > 0
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() +
                            " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("***********");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (double) (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        var pDouble = (double) (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = (double) (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;
        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    private static final String PLACE_DISPONIBLE_MSG = "Le nombre de place disponible pour la chambre %s %d est %d";
    private static final String CHAMBRE_COMPLETE_MSG = "La chambre %s %d est complete";

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        // Get current academic year dates
        var current = YearMonth.now();
        LocalDate dateDebutAU;
        LocalDate dateFinAU;

        if (current.getMonthValue() <= 7) { // Before August
            dateDebutAU = LocalDate.of(current.getYear() - 1, 9, 15);
            dateFinAU = LocalDate.of(current.getYear(), 6, 30);
        } else { // September onwards
            dateDebutAU = LocalDate.of(current.getYear(), 9, 15);
            dateFinAU = LocalDate.of(current.getYear() + 1, 6, 30);
        }

        repo.findAll().forEach(c -> {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                    c.getIdChambre(), true, dateDebutAU, dateFinAU);

            switch (c.getTypeC()) {
                case SIMPLE -> logAvailability(c, nbReservation, 1);
                case DOUBLE -> logAvailability(c, nbReservation, 2);
                case TRIPLE -> logAvailability(c, nbReservation, 3);
            }
        });
    }

    private void logAvailability(Chambre c, long nbReservation, int capacity) {
        if (nbReservation < capacity) {
            log.info(String.format(PLACE_DISPONIBLE_MSG,
                    c.getTypeC(),
                    c.getNumeroChambre(),
                    capacity - nbReservation));
        } else {
            log.info(String.format(CHAMBRE_COMPLETE_MSG,
                    c.getTypeC(),
                    c.getNumeroChambre()));
        }
    }

    @Override
    public List<Chambre> getChambresParNomBlocJava(String nomBloc) {
        var b = blocRepository.findByNomBloc(nomBloc);
        return b.getChambres();
    }

    @Override
    public List<Chambre> getChambresParNomBlocKeyWord(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocJPQL(String nomBloc) {
        return repo.getChambresParNomBlocJPQL(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocSQL(String nomBloc) {
        return repo.getChambresParNomBlocSQL(nomBloc);
    }
}
