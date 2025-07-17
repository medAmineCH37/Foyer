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
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.services.chambre.ChambreService;

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
class ChambreServiceTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private ChambreService chambreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Chambre c = new Chambre();
        c.setNumeroChambre(101);

        when(chambreRepository.save(c)).thenReturn(c);

        Chambre result = chambreService.addOrUpdate(c);

        assertNotNull(result);
        assertEquals(101, result.getNumeroChambre());
        verify(chambreRepository, times(1)).save(c);
    }

    @Test
    void testFindAll() {
        List<Chambre> list = Arrays.asList(new Chambre(), new Chambre());
        when(chambreRepository.findAll()).thenReturn(list);

        List<Chambre> result = chambreService.findAll();

        assertEquals(2, result.size());
        verify(chambreRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Chambre c = new Chambre();
        c.setIdChambre(1L);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(c));

        Chambre result = chambreService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdChambre());
    }

    @Test
    void testFindById_NotFound() {
        when(chambreRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                chambreService.findById(1L));
        assertTrue(thrown.getMessage().contains("Chambre not found"));
    }

    @Test
    void testDeleteById() {
        doNothing().when(chambreRepository).deleteById(1L);
        chambreService.deleteById(1L);
        verify(chambreRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Chambre c = new Chambre();
        doNothing().when(chambreRepository).delete(c);
        chambreService.delete(c);
        verify(chambreRepository, times(1)).delete(c);
    }

    @Test
    void testNbChambreParTypeEtBloc() {
        Chambre c1 = new Chambre();
        Chambre c2 = new Chambre();
        Bloc bloc = new Bloc();
        bloc.setIdBloc(2L);

        c1.setBloc(bloc);
        c2.setBloc(bloc);
        c1.setTypeC(TypeChambre.DOUBLE);
        c2.setTypeC(TypeChambre.SIMPLE);

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.SIMPLE, 2L);
        assertEquals(1, count);
    }
}
