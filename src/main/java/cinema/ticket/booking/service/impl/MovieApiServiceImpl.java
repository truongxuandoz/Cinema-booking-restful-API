package cinema.ticket.booking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cinema.ticket.booking.service.MovieApiService;

@Service
public class MovieApiServiceImpl implements MovieApiService {

    @Value("${app.movie_api_key}")
    private String API_KEY;

    @Value("${app.movie_api_host}")
    private String API_HOST;

    @Value("${app.movie_api_base_url}")
    private String API_BASE_URL;
    @Autowired
    private RestTemplate restTemplate;

    public String getShowDetail(String type, String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", API_KEY);
        headers.set("x-rapidapi-host", API_HOST);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = API_BASE_URL + "/" + type + "/" + id;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String getAllMovies(String key, Integer pageNumber, Integer pageSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", API_KEY);
        headers.set("x-rapidapi-host", API_HOST);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_BASE_URL)
                .queryParam("keyword", key)
                .queryParam("page", pageNumber)
                .queryParam("country", "us")
                .queryParam("service", "netflix")
                .queryParam("type", "movie")
                .queryParam("language", "en")
                .queryParam("output_language", "en")
                .queryParam("order_by", "popularity")
                .queryParam("year_min", "2000")
                .queryParam("year_max", "2023");

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        return response.getBody();
    }

}
