package my.telegrambot.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Vacancy {

    @CsvBindByName(column = "Id")
    private String id;

    @CsvBindByName(column = "Title")
    private String title;

    @CsvBindByName(column = "Short description")
    private String shortDescription;

    @CsvBindByName(column = "Long description")
    private String longDescription;

    @CsvBindByName(column = "Company")
    private String company;

    @CsvBindByName(column = "Salary")
    private String salary;

    @CsvBindByName(column = "Link")
    private String link;

}