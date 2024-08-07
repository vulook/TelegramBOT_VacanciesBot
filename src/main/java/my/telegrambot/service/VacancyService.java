package my.telegrambot.service;

import jakarta.annotation.PostConstruct;
import my.telegrambot.model.Vacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileName: VacancyService
 * Author:   Andriy V
 * Date:     22.06.2024 21:49
 * Description: VacancyService
 */


@Service
public class VacancyService {

    String fileName = "vacancies.csv";


    private final VacanciesReaderService vacanciesReaderService;

    private final Map<String, Vacancy> vacancies = new HashMap<>();

    @Autowired
    public VacancyService(VacanciesReaderService vacanciesReaderService) {
        this.vacanciesReaderService = vacanciesReaderService;
    }


    @PostConstruct
    public void init() {

        List<Vacancy> listVacancies = vacanciesReaderService.getVacanciesFromFile(fileName);
        for (Vacancy vacancy : listVacancies) {
            vacancies.put(vacancy.getId(), vacancy);

        }
    }

    // get junior vacancies
    public List<Vacancy> getJuniorVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDTO -> vacancyDTO.getTitle().toLowerCase().contains("junior"))
                .toList();
    }

    // get middle vacancies
    public List<Vacancy> getMiddleVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDTO -> vacancyDTO.getTitle().toLowerCase().contains("middle"))
                .toList();
    }

    // get senior vacancies
    public List<Vacancy> getSeniorVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDTO -> vacancyDTO.getTitle().toLowerCase().contains("senior"))
                .toList();
    }


    public Vacancy get(String id) {
        return vacancies.get(id);
    }

}
