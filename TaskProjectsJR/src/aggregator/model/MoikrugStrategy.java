package aggregator.model;

import aggregator.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoikrugStrategy implements Strategy {
    private static final String URL_FORMAT= "https://moikrug.ru/vacancies?q=java+%s&page=%d";
   // private static final String URL_FORMAT = "https://javarush.ru/testdata/big28data.html";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> Vacancies = new ArrayList<>();
        int pageNum = 0;
        Document doc = null;
        while (true) {
            try {
                doc = getDocument(searchString, pageNum);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements vacancies = doc.getElementsByClass("job");
            if (vacancies.size() == 0) break;
            for (Element element : vacancies) {
                if (element != null) {
                    Vacancy vac = new Vacancy();
                    vac.setTitle(element.getElementsByAttributeValue("class", "title").text());
                    vac.setCompanyName(element.getElementsByAttributeValue("class", "company_name").text());
                    vac.setSiteName(URL_FORMAT);
                    vac.setUrl("https://moikrug.ru" + element.select("a[class=job_icon]").attr("href"));
                    String salary = element.getElementsByAttributeValue("class", "salary").text();
                    String city = element.getElementsByAttributeValue("class", "location").text();
                    vac.setSalary(salary.length() == 0 ? "" : salary);
                    vac.setCity(city.length() == 0 ? "" : city);
                    Vacancies.add(vac);
                }
            }
            pageNum++;
        }
        return Vacancies;
    }


    protected Document getDocument(String searchString, int page) throws IOException {
        String url = String.format(URL_FORMAT, searchString, page);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
                .referrer("none")
                .timeout(5 * 1000)
                .get();

        return doc;
    }
}