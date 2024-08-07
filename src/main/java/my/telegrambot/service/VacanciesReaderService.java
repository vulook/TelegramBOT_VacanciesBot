package my.telegrambot.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import my.telegrambot.model.Vacancy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * FileName: VacanciesReaderService
 * Author:   Andriy V
 * Date:     22.06.2024 21:48
 * Description: VacanciesReaderService
 */


@Service
public class VacanciesReaderService {

    public List<Vacancy> getVacanciesFromFile(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<Vacancy> csvToBean = new CsvToBeanBuilder<Vacancy>(inputStreamReader)
                    .withType(Vacancy.class)
                    //.withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();

        } catch (IOException ex) {
            throw new RuntimeException("Can not read data from file " + fileName, ex);
        }
    }


}
