package org.cloudfoundry.samples.music;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.cloudfoundry.samples.music.domain.Album;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlbumControllerCharacterizationTest {

    static WireMockServer wireMock = new WireMockServer(0);

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        wireMock.start();
        registry.add("album-service.url", () -> "http://localhost:" + wireMock.port());
    }

    @AfterAll
    static void tearDown() {
        wireMock.stop();
    }

    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
    }

    @Test
    void listAlbums_proxiesToAlbumService() {
        wireMock.stubFor(get(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":\"1\",\"title\":\"Nevermind\",\"artist\":\"Nirvana\",\"releaseYear\":\"1991\",\"genre\":\"Rock\",\"trackCount\":0}]")));

        ResponseEntity<Album[]> response = restTemplate.getForEntity("/albums", Album[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Nevermind");
        assertThat(response.getBody()[0].getArtist()).isEqualTo("Nirvana");
        assertThat(response.getBody()[0].getId()).isEqualTo("1");
    }

    @Test
    void listAlbums_emptyArray() {
        wireMock.stubFor(get(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        ResponseEntity<Album[]> response = restTemplate.getForEntity("/albums", Album[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void createAlbum_viaPut_proxiesAndReturnsAlbum() {
        wireMock.stubFor(put(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"abc-123\",\"title\":\"Test\",\"artist\":\"Artist\",\"releaseYear\":\"2001\",\"genre\":\"Jazz\",\"trackCount\":0}")));

        Album album = new Album("Test", "Artist", "2001", "Jazz");
        ResponseEntity<Album> response = restTemplate.exchange(
                "/albums", HttpMethod.PUT,
                new HttpEntity<>(album, jsonHeaders()),
                Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo("abc-123");
        assertThat(response.getBody().getTitle()).isEqualTo("Test");

        wireMock.verify(putRequestedFor(urlEqualTo("/albums")));
    }

    @Test
    void getAlbumById_proxiesAndReturnsAlbum() {
        wireMock.stubFor(get(urlEqualTo("/albums/abc-123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"abc-123\",\"title\":\"Found\",\"artist\":\"Artist\",\"releaseYear\":\"1999\",\"genre\":\"Pop\",\"trackCount\":0}")));

        ResponseEntity<Album> response = restTemplate.getForEntity("/albums/abc-123", Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Found");
    }

    @Test
    void getAlbumById_nonExistent_returnsNullBody() {
        wireMock.stubFor(get(urlEqualTo("/albums/does-not-exist"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        ResponseEntity<String> response = restTemplate.getForEntity("/albums/does-not-exist", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateAlbum_viaPost_proxiesToAlbumService() {
        wireMock.stubFor(post(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"abc-123\",\"title\":\"Updated\",\"artist\":\"Artist\",\"releaseYear\":\"2000\",\"genre\":\"Rock\",\"trackCount\":0}")));

        Album album = new Album("Updated", "Artist", "2000", "Rock");
        album.setId("abc-123");
        ResponseEntity<Album> response = restTemplate.exchange(
                "/albums", HttpMethod.POST,
                new HttpEntity<>(album, jsonHeaders()),
                Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo("abc-123");
        assertThat(response.getBody().getTitle()).isEqualTo("Updated");

        wireMock.verify(postRequestedFor(urlEqualTo("/albums")));
    }

    @Test
    void deleteAlbum_proxiesToAlbumService() {
        wireMock.stubFor(delete(urlEqualTo("/albums/abc-123"))
                .willReturn(aResponse().withStatus(200)));

        restTemplate.delete("/albums/abc-123");

        wireMock.verify(deleteRequestedFor(urlEqualTo("/albums/abc-123")));
    }

    @Test
    void putIsUsedForCreate_postForUpdate_contractPreserved() {
        wireMock.stubFor(put(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"new-id\",\"title\":\"New\",\"artist\":\"A\",\"releaseYear\":\"2010\",\"genre\":\"Electronic\",\"trackCount\":0}")));

        wireMock.stubFor(post(urlEqualTo("/albums"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"new-id\",\"title\":\"New\",\"artist\":\"Updated A\",\"releaseYear\":\"2010\",\"genre\":\"Electronic\",\"trackCount\":0}")));

        Album album = new Album("New", "A", "2010", "Electronic");

        ResponseEntity<Album> putResponse = restTemplate.exchange(
                "/albums", HttpMethod.PUT,
                new HttpEntity<>(album, jsonHeaders()),
                Album.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getId()).isEqualTo("new-id");

        Album existing = putResponse.getBody();
        existing.setArtist("Updated A");
        ResponseEntity<Album> postResponse = restTemplate.exchange(
                "/albums", HttpMethod.POST,
                new HttpEntity<>(existing, jsonHeaders()),
                Album.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postResponse.getBody().getArtist()).isEqualTo("Updated A");

        wireMock.verify(putRequestedFor(urlEqualTo("/albums")));
        wireMock.verify(postRequestedFor(urlEqualTo("/albums")));
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
