package com.catalog.album.seed;

import com.catalog.album.domain.Album;
import com.catalog.album.repository.AlbumRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class AlbumDataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final AlbumRepository repository;

    public AlbumDataSeeder(AlbumRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (repository.count() == 0) {
            try (InputStream is = new ClassPathResource("albums.json").getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<Album> albums = mapper.readValue(is, new TypeReference<List<Album>>() {});
                repository.saveAll(albums);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load seed data", e);
            }
        }
    }
}
