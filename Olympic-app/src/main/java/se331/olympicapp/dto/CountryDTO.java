package se331.olympicapp.dto;

import lombok.Data;

@Data
public class CountryDTO {
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
