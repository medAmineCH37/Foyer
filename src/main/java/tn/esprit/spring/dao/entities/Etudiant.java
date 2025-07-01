package tn.esprit.spring.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Etudiant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEtudiant;

    private String nomEt;
    private String prenomEt;
    private long cin;
    private String ecole;
    private LocalDate dateNaissance;

    @ManyToMany(mappedBy = "etudiants")
    private List<Reservation> reservations = new ArrayList<>();
}
