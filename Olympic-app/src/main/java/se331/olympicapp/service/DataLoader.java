package se331.olympicapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import se331.olympicapp.entity.SportDetail;
import se331.olympicapp.repository.SportDetailRepository;
import se331.olympicapp.dto.SportDisciplineDTO;

import java.io.InputStream;
import java.util.List;

@Service
public class DataLoader {
    private final SportDetailRepository sportDetailRepository;

    public DataLoader(SportDetailRepository sportDetailRepository) {
        this.sportDetailRepository = sportDetailRepository;
    }

    @PostConstruct
    public void loadSportDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/sportdb.json");

            List<SportDisciplineDTO> sportData = objectMapper.readValue(inputStream, new TypeReference<List<SportDisciplineDTO>>() {});
            for (SportDisciplineDTO dto : sportData) {
                for (SportDisciplineDTO.DisciplineDTO discipline : dto.getDisciplines()) {
                    // 查找是否已存在
                    SportDetail existingSportDetail = sportDetailRepository.findByNocAndDisciplineName(dto.getNoc(), discipline.getName());
                    if (existingSportDetail != null) {
                        // 更新已有记录
                        existingSportDetail.setGold(discipline.getGold());
                        existingSportDetail.setSilver(discipline.getSilver());
                        existingSportDetail.setBronze(discipline.getBronze());
                        sportDetailRepository.save(existingSportDetail);
                    } else {
                        // 插入新记录
                        SportDetail newSportDetail = new SportDetail();
                        newSportDetail.setNoc(dto.getNoc());
                        newSportDetail.setDisciplineName(discipline.getName());
                        newSportDetail.setGold(discipline.getGold());
                        newSportDetail.setSilver(discipline.getSilver());
                        newSportDetail.setBronze(discipline.getBronze());
                        sportDetailRepository.save(newSportDetail);
                    }
                }
            }
            System.out.println("Sport details successfully loaded into the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
