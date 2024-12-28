package se331.olympicapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import se331.olympicapp.entity.Country;
import se331.olympicapp.repository.CountryRepository;
import se331.olympicapp.dto.CountryDTO;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CountryDataLoader {
    private final CountryRepository countryRepository;

    public CountryDataLoader(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PostConstruct
    public void loadCountryData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/countryinfo.json");

            List<Country> countryData = objectMapper.readValue(inputStream, new TypeReference<List<Country>>() {});
            for (Country country : countryData) {
                Optional<Country> existingCountry = countryRepository.findByNoc(country.getNoc());
                if (existingCountry.isPresent()) {
                    Country existing = existingCountry.get();
                    existing.setRank(country.getRank());
                    existing.setGold(country.getGold());
                    existing.setSilver(country.getSilver());
                    existing.setBronze(country.getBronze());
                    existing.setTotal(country.getTotal());
                    existing.setSortRank(country.getSortRank());
                    existing.setRankTotal(country.getRankTotal());
                    existing.setSortTotalRank(country.getSortTotalRank());
                    existing.setEnDescription(country.getEnDescription());
                    existing.setEnLongDescription(country.getEnLongDescription());
                    existing.setFlagUrl(country.getFlagUrl());
                    countryRepository.save(existing);
                } else {
                    countryRepository.save(country);
                }
            }
            System.out.println("Country data successfully loaded into the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
