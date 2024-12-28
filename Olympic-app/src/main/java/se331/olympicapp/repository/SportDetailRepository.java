package se331.olympicapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se331.olympicapp.entity.SportDetail;

import java.util.List;

@Repository
public interface SportDetailRepository extends JpaRepository<SportDetail, Long> {
    List<SportDetail> findByNoc(String noc);
    SportDetail findByNocAndDisciplineName(String noc, String disciplineName);
}
