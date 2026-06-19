package com.catalog.album;

import com.catalog.album.domain.Album;
import com.catalog.album.repository.AlbumRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlbumRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void getAlbums_returnsJsonArray() throws Exception {
        repository.save(new Album("Nevermind", "Nirvana", "1991", "Rock"));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Nevermind")))
                .andExpect(jsonPath("$[0].artist", is("Nirvana")))
                .andExpect(jsonPath("$[0].releaseYear", is("1991")))
                .andExpect(jsonPath("$[0].genre", is("Rock")))
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    @Test
    void getAlbums_emptyWhenNoneExist() throws Exception {
        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void putAlbum_createsWithGeneratedId() throws Exception {
        Album album = new Album("Thriller", "Michael Jackson", "1982", "Pop");

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Thriller")))
                .andExpect(jsonPath("$.artist", is("Michael Jackson")))
                .andExpect(jsonPath("$.releaseYear", is("1982")))
                .andExpect(jsonPath("$.genre", is("Pop")));
    }

    @Test
    void putAlbum_rejectsMissingTitle() throws Exception {
        Album album = new Album(null, "Artist", "1990", "Rock");

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putAlbum_rejectsMissingArtist() throws Exception {
        Album album = new Album("Title", null, "1990", "Rock");

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putAlbum_rejectsInvalidReleaseYear() throws Exception {
        Album album = new Album("Title", "Artist", "99", "Rock");

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putAlbum_rejectsMissingGenre() throws Exception {
        Album album = new Album("Title", "Artist", "1990", null);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postAlbum_updatesExisting() throws Exception {
        Album saved = repository.save(new Album("Old Title", "Artist", "1990", "Rock"));
        saved.setTitle("New Title");

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.title", is("New Title")));
    }

    @Test
    void getAlbumById_returnsAlbum() throws Exception {
        Album saved = repository.save(new Album("Nevermind", "Nirvana", "1991", "Rock"));

        mockMvc.perform(get("/albums/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.title", is("Nevermind")));
    }

    @Test
    void getAlbumById_returnsNullBodyForNonExistent() throws Exception {
        MvcResult result = mockMvc.perform(get("/albums/nonexistent"))
                .andExpect(status().isOk())
                .andReturn();

        assert result.getResponse().getContentLength() == 0
                || result.getResponse().getContentAsString().isEmpty()
                || result.getResponse().getContentAsString().equals("null");
    }

    @Test
    void deleteAlbum_removesFromRepository() throws Exception {
        Album saved = repository.save(new Album("Nevermind", "Nirvana", "1991", "Rock"));

        mockMvc.perform(delete("/albums/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void fullCrudCycle() throws Exception {
        // Create
        Album album = new Album("Test Album", "Test Artist", "2000", "Jazz");
        MvcResult createResult = mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andReturn();

        Album created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Album.class);

        // Read
        mockMvc.perform(get("/albums/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Album")));

        // Update
        created.setTitle("Updated Album");
        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Album")));

        // Delete
        mockMvc.perform(delete("/albums/" + created.getId()))
                .andExpect(status().isOk());

        // Verify gone
        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
