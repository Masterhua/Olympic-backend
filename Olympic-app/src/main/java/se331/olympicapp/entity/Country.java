package se331.olympicapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String noc;

    private int rank;
    private int sortRank;
    private int rankTotal;
    private int sortTotalRank;

    private String nocSlug;

    private int gold;
    private int silver;
    private int bronze;
    private int total;

    private String enDescription;
    private String enLongDescription;
    private String flagUrl;
}
