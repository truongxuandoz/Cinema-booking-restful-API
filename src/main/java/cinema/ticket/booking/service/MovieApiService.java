package cinema.ticket.booking.service;

import org.springframework.stereotype.Service;

@Service
public interface MovieApiService {
    public String getShowDetail(String genre, String id);
    public String getAllMovies(String key, Integer pageNumber, Integer pageSize);
}
