package se331.olympicapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String noc;

    private String disciplineName;

    private int gold;

    private int silver;

    private int bronze;
}
