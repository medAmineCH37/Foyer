package tn.esprit.spring.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_FOYER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Foyer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFoyer;

    private String nomFoyer;
    private long capaciteFoyer;

    @OneToOne(mappedBy = "foyer")
    private Universite universite;  // Marked transient

    @OneToMany(mappedBy = "foyer")
    private List<Bloc> blocs = new ArrayList<>();  // Marked transient
}
