package se331.olympicapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.olympicapp.entity.SportDetail;
import se331.olympicapp.repository.SportDetailRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SportDetailController {

    private final SportDetailRepository sportDetailRepository;

    public SportDetailController(SportDetailRepository sportDetailRepository) {
        this.sportDetailRepository = sportDetailRepository;
    }

    @GetMapping("/{noc}")
    public ResponseEntity<List<SportDetail>> getSportsByNoc(@PathVariable String noc) {
        List<SportDetail> sports = sportDetailRepository.findByNoc(noc);
        if (sports.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(sports);
    }

    @PutMapping("/{noc}/{id}")
    public ResponseEntity<?> updateSportDetail(
            @PathVariable String noc,
            @PathVariable Long id,
            @RequestBody SportDetail updatedDetail) {

        Optional<SportDetail> sportDetailOpt = sportDetailRepository.findById(id);

        if (sportDetailOpt.isEmpty()) {
            // 如果记录不存在，返回错误信息
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Sport detail not found"));
        }

        SportDetail sportDetail = sportDetailOpt.get();

        if (!sportDetail.getNoc().equals(noc)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "NOC mismatch"));
        }

        // 更新字段
        sportDetail.setGold(updatedDetail.getGold());
        sportDetail.setSilver(updatedDetail.getSilver());
        sportDetail.setBronze(updatedDetail.getBronze());

        sportDetailRepository.save(sportDetail);

        return ResponseEntity.ok(Map.of("message", "Sport detail updated successfully", "sportDetail", sportDetail));
    }

    @PostMapping("/{noc}")
    public ResponseEntity<?> addOrUpdateSportDetail(
            @PathVariable String noc,
            @RequestBody SportDetail newDetail) {
        // 查找是否已有记录
        SportDetail existingSportDetail = sportDetailRepository.findByNocAndDisciplineName(noc, newDetail.getDisciplineName());
        if (existingSportDetail != null) {
            // 更新现有记录
            existingSportDetail.setGold(newDetail.getGold());
            existingSportDetail.setSilver(newDetail.getSilver());
            existingSportDetail.setBronze(newDetail.getBronze());
            sportDetailRepository.save(existingSportDetail);

            return ResponseEntity.ok(Map.of("message", "Sport detail updated successfully", "sportDetail", existingSportDetail));
        } else {
            // 新增记录
            newDetail.setNoc(noc);
            sportDetailRepository.save(newDetail);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Sport detail created successfully", "sportDetail", newDetail));
        }
    }
}
