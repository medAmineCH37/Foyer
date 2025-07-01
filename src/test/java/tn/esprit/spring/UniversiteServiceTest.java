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
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.UniversiteRepository;
import tn.esprit.spring.services.universite.UniversiteService;

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
class UniversiteServiceTest {
    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteService universiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Universite u = new Universite();
        u.setNomUniversite("Test Uni");

        when(universiteRepository.save(u)).thenReturn(u);

        Universite saved = universiteService.addOrUpdate(u);
        assertNotNull(saved);
        assertEquals("Test Uni", saved.getNomUniversite());

        verify(universiteRepository, times(1)).save(u);
    }

    @Test
    void testFindAll() {
        List<Universite> list = Arrays.asList(new Universite(), new Universite());
        when(universiteRepository.findAll()).thenReturn(list);

        List<Universite> result = universiteService.findAll();

        assertEquals(2, result.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Universite u = new Universite();
        u.setIdUniversite(1L);

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(u));

        Universite result = universiteService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdUniversite());
    }

    @Test
    void testFindById_NotFound() {
        when(universiteRepository.findById(1L)).thenReturn(Optional.empty());

        Universite result = universiteService.findById(1L);
        assertNull(result);
    }

    @Test
    void testDeleteById() {
        doNothing().when(universiteRepository).deleteById(1L);
        universiteService.deleteById(1L);
        verify(universiteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Universite u = new Universite();
        doNothing().when(universiteRepository).delete(u);
        universiteService.delete(u);
        verify(universiteRepository, times(1)).delete(u);
    }

    @Test
    void testAjouterUniversiteEtSonFoyer() {
        Universite u = new Universite();
        u.setNomUniversite("Uni avec foyer");

        when(universiteRepository.save(u)).thenReturn(u);

        Universite result = universiteService.ajouterUniversiteEtSonFoyer(u);
        assertNotNull(result);
        assertEquals("Uni avec foyer", result.getNomUniversite());

        verify(universiteRepository, times(1)).save(u);
    }
}
