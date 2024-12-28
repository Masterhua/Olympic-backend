package se331.olympicapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class SportDisciplineDTO {
    private String noc;
    private List<DisciplineDTO> disciplines;

    @Data
    public static class DisciplineDTO {
        private String name;
        private int gold;
        private int silver;
        private int bronze;
    }
}
