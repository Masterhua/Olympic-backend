package se331.olympicapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.olympicapp.entity.Country;
import se331.olympicapp.repository.CountryRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        if (countries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{noc}")
    public ResponseEntity<?> getCountryByNoc(@PathVariable String noc) {
        Optional<Country> countryOpt = countryRepository.findByNoc(noc);

        if (countryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Country not found"));
        }

        return ResponseEntity.ok(countryOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> addCountry(@RequestBody Country newCountry) {
        if (countryRepository.findByNoc(newCountry.getNoc()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Country with this NOC already exists"));
        }
        try {
            Country savedCountry = countryRepository.save(newCountry);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCountry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to add country", "error", e.getMessage()));
        }
    }

    @PutMapping("/{noc}")
    public ResponseEntity<?> updateCountry(@PathVariable String noc, @RequestBody Map<String, Object> updates) {
        Optional<Country> countryOpt = countryRepository.findByNoc(noc);

        if (countryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Country not found"));
        }

        Country country = countryOpt.get();
        updates.forEach((key, value) -> {
            switch (key) {
                case "rank":
                    country.setRank((Integer) value);
                    break;
                case "sortRank":
                    country.setSortRank((Integer) value);
                    break;
                case "rankTotal":
                    country.setRankTotal((Integer) value);
                    break;
                case "sortTotalRank":
                    country.setSortTotalRank((Integer) value);
                    break;
                case "gold":
                    country.setGold((Integer) value);
                    break;
                case "silver":
                    country.setSilver((Integer) value);
                    break;
                case "bronze":
                    country.setBronze((Integer) value);
                    break;
                case "total":
                    country.setTotal((Integer) value);
                    break;
                case "enDescription":
                    country.setEnDescription((String) value);
                    break;
                case "enLongDescription":
                    country.setEnLongDescription((String) value);
                    break;
                case "flagUrl":
                    country.setFlagUrl((String) value);
                    break;
                default:
                    break;
            }
        });

        countryRepository.save(country);

        return ResponseEntity.ok(Map.of("message", "Country updated successfully", "country", country));
    }
}
