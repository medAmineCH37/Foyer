package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("test")
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Etudiant e = new Etudiant();
        e.setNomEt("Ali");

        when(etudiantRepository.save(e)).thenReturn(e);

        Etudiant result = etudiantService.addOrUpdate(e);
        assertNotNull(result);
        assertEquals("Ali", result.getNomEt());

        verify(etudiantRepository, times(1)).save(e);
    }

    @Test
    void testFindAll() {
        List<Etudiant> list = Arrays.asList(new Etudiant(), new Etudiant());
        when(etudiantRepository.findAll()).thenReturn(list);

        List<Etudiant> result = etudiantService.findAll();
        assertEquals(2, result.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Etudiant e = new Etudiant();
        e.setIdEtudiant(1L);
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(e));

        Etudiant result = etudiantService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdEtudiant());
    }

    @Test
    void testFindById_NotFound() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> etudiantService.findById(1L));
        assertTrue(ex.getMessage().contains("Etudiant not found"));
    }

    @Test
    void testDeleteById() {
        doNothing().when(etudiantRepository).deleteById(1L);
        etudiantService.deleteById(1L);
        verify(etudiantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Etudiant e = new Etudiant();
        doNothing().when(etudiantRepository).delete(e);
        etudiantService.delete(e);
        verify(etudiantRepository, times(1)).delete(e);
    }

    @Test
    void testSelectJPQL() {
        List<Etudiant> list = Arrays.asList(new Etudiant(), new Etudiant());
        when(etudiantRepository.selectJPQL("Ali")).thenReturn(list);

        List<Etudiant> result = etudiantService.selectJPQL("Ali");
        assertEquals(2, result.size());
        verify(etudiantRepository, times(1)).selectJPQL("Ali");
    }

    @Test
    void testAffecterReservationAEtudiant() {
        Reservation r = new Reservation();
        Etudiant e = new Etudiant();
        e.setReservations(new ArrayList<>());

        when(reservationRepository.findById("R1")).thenReturn(Optional.of(r));
        when(etudiantRepository.getByNomEtAndPrenomEt("Ali", "Baba")).thenReturn(e);
        when(etudiantRepository.save(e)).thenReturn(e);

        etudiantService.affecterReservationAEtudiant("R1", "Ali", "Baba");

        assertTrue(e.getReservations().contains(r));
        verify(etudiantRepository, times(1)).save(e);
    }

    @Test
    void testDesaffecterReservationAEtudiant() {
        Reservation r = new Reservation();
        Etudiant e = new Etudiant();
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(r);
        e.setReservations(reservations);

        when(reservationRepository.findById("R1")).thenReturn(Optional.of(r));
        when(etudiantRepository.getByNomEtAndPrenomEt("Ali", "Baba")).thenReturn(e);
        when(etudiantRepository.save(e)).thenReturn(e);

        etudiantService.desaffecterReservationAEtudiant("R1", "Ali", "Baba");

        assertFalse(e.getReservations().contains(r));
        verify(etudiantRepository, times(1)).save(e);
    }
}
