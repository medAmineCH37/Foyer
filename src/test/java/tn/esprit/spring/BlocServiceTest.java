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
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.services.bloc.BlocService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class BlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private BlocService blocService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Bloc b = new Bloc();
        b.setNomBloc("Bloc A");
        b.setChambres(List.of(new Chambre()));

        when(blocRepository.save(any(Bloc.class))).thenReturn(b);

        Bloc saved = blocService.addOrUpdate(b);
        assertNotNull(saved);
        assertEquals("Bloc A", saved.getNomBloc());

        verify(chambreRepository, times(1)).save(any(Chambre.class));
        verify(blocRepository, times(1)).save(any(Bloc.class));
    }

    @Test
    void testFindAll() {
        List<Bloc> list = List.of(new Bloc(), new Bloc());
        when(blocRepository.findAll()).thenReturn(list);

        List<Bloc> result = blocService.findAll();

        assertEquals(2, result.size());
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Bloc b = new Bloc();
        b.setIdBloc(1L);

        when(blocRepository.findById(1L)).thenReturn(Optional.of(b));

        Bloc result = blocService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdBloc());
    }

    @Test
    void testFindById_NotFound() {
        when(blocRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> blocService.findById(1L));
        assertTrue(exception.getMessage().contains("Bloc not found"));
    }

    @Test
    void testDeleteById() {
        Bloc b = new Bloc();
        b.setChambres(List.of(new Chambre()));

        when(blocRepository.findById(1L)).thenReturn(Optional.of(b));
        doNothing().when(chambreRepository).deleteAll(any());
        doNothing().when(blocRepository).delete(any());

        blocService.deleteById(1L);

        verify(chambreRepository, times(1)).deleteAll(any());
        verify(blocRepository, times(1)).delete(any());
    }

    @Test
    void testDelete() {
        Bloc b = new Bloc();
        b.setChambres(List.of(new Chambre()));

        doNothing().when(chambreRepository).deleteAll(any());
        doNothing().when(blocRepository).delete(any());

        blocService.delete(b);

        verify(chambreRepository, times(1)).deleteAll(any());
        verify(blocRepository, times(1)).delete(any());
    }

    @Test
    void testAffecterChambresABloc() {
        Bloc b = new Bloc();
        b.setNomBloc("Bloc A");

        Chambre c1 = new Chambre();
        Chambre c2 = new Chambre();

        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(b);
        when(chambreRepository.findByNumeroChambre(1L)).thenReturn(c1);
        when(chambreRepository.findByNumeroChambre(2L)).thenReturn(c2);

        Bloc result = blocService.affecterChambresABloc(List.of(1L, 2L), "Bloc A");

        assertNotNull(result);
        verify(chambreRepository, times(2)).save(any());
    }

    @Test
    void testAffecterBlocAFoyer() {
        Bloc b = new Bloc();
        Foyer f = new Foyer();

        when(blocRepository.findByNomBloc("Bloc A")).thenReturn(b);
        when(foyerRepository.findByNomFoyer("Foyer X")).thenReturn(f);
        when(blocRepository.save(any())).thenReturn(b);

        Bloc result = blocService.affecterBlocAFoyer("Bloc A", "Foyer X");

        assertEquals(f, result.getFoyer());
        verify(blocRepository, times(1)).save(any());
    }

    @Test
    void testAjouterBlocEtAffecterAFoyer() {
        Bloc b = new Bloc();
        Foyer f = new Foyer();

        when(foyerRepository.findByNomFoyer("Foyer X")).thenReturn(f);
        when(blocRepository.save(b)).thenReturn(b);

        Bloc result = blocService.ajouterBlocEtAffecterAFoyer(b, "Foyer X");

        assertEquals(f, result.getFoyer());
        verify(blocRepository, times(1)).save(b);
    }
}