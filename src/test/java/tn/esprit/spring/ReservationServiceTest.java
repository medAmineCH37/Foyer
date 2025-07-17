package tn.esprit.spring;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");

        when(reservationRepository.save(r)).thenReturn(r);

        Reservation result = reservationService.addOrUpdate(r);
        assertEquals("R1", result.getIdReservation());

        verify(reservationRepository, times(1)).save(r);
    }

    @Test
    void testFindAll() {
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(new Reservation(), new Reservation()));
        List<Reservation> list = reservationService.findAll();
        assertEquals(2, list.size());
    }

    @Test
    void testFindById_Found() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");
        when(reservationRepository.findById("R1")).thenReturn(Optional.of(r));

        Reservation result = reservationService.findById("R1");
        assertNotNull(result);
    }

    @Test
    void testFindById_NotFound() {
        when(reservationRepository.findById("R2")).thenReturn(Optional.empty());

        Reservation result = reservationService.findById("R2");
        assertNull(result);
    }

    @Test
    void testDeleteById() {
        doNothing().when(reservationRepository).deleteById("R1");
        reservationService.deleteById("R1");
        verify(reservationRepository, times(1)).deleteById("R1");
    }

    @Test
    void testDelete() {
        Reservation r = new Reservation();
        doNothing().when(reservationRepository).delete(r);
        reservationService.delete(r);
        verify(reservationRepository, times(1)).delete(r);
    }

    @Test
    void testAffectReservationAChambre() {
        Reservation r = new Reservation();
        Chambre c = new Chambre();
        c.setReservations(new ArrayList<>());

        when(reservationRepository.findById("R1")).thenReturn(Optional.of(r));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(c));
        when(chambreRepository.save(any())).thenReturn(c);

        reservationService.affectReservationAChambre("R1", 1L);

        assertTrue(c.getReservations().contains(r));
        verify(chambreRepository).save(c);
    }

    @Test
    void testAffectReservationAChambre_NotFound() {
        when(reservationRepository.findById("X")).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> reservationService.affectReservationAChambre("X", 2L));
        assertTrue(ex.getMessage().contains("Reservation not found"));
    }

    @Test
    void testDeaffectReservationAChambre() {
        Reservation r = new Reservation();
        Chambre c = new Chambre();
        List<Reservation> list = new ArrayList<>();
        list.add(r);
        c.setReservations(list);

        when(reservationRepository.findById("R1")).thenReturn(Optional.of(r));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(c));

        reservationService.deaffectReservationAChambre("R1", 1L);

        assertFalse(c.getReservations().contains(r));
        verify(chambreRepository).save(c);
    }

    @Test
    void testAnnulerReservation() {
        Reservation r = new Reservation();
        r.setIdReservation("R1");
        r.setEstValide(true);
        Chambre c = new Chambre();
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(r);
        c.setReservations(reservations);

        when(reservationRepository.findByEtudiantsCinAndEstValide(12345678L, true)).thenReturn(r);
        when(chambreRepository.findByReservationsIdReservation("R1")).thenReturn(c);

        String result = reservationService.annulerReservation(12345678L);

        assertEquals("La réservation R1 est annulée avec succés", result);
        verify(reservationRepository).delete(r);
    }

}
