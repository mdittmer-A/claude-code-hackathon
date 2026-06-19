package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.domain.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping(value = "/albums")
public class AlbumController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    private final RestTemplate restTemplate;
    private final String albumServiceUrl;

    public AlbumController(RestTemplate restTemplate,
                           @Value("${album-service.url}") String albumServiceUrl) {
        this.restTemplate = restTemplate;
        this.albumServiceUrl = albumServiceUrl;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Album>> albums() {
        return restTemplate.exchange(
                albumServiceUrl + "/albums",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Album>>() {});
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Album add(@RequestBody Album album) {
        logger.info("Adding album " + album.getId());
        return restTemplate.exchange(
                albumServiceUrl + "/albums",
                HttpMethod.PUT,
                new HttpEntity<>(album),
                Album.class).getBody();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Album update(@RequestBody Album album) {
        logger.info("Updating album " + album.getId());
        return restTemplate.exchange(
                albumServiceUrl + "/albums",
                HttpMethod.POST,
                new HttpEntity<>(album),
                Album.class).getBody();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Album getById(@PathVariable String id) {
        logger.info("Getting album " + id);
        return restTemplate.getForObject(albumServiceUrl + "/albums/" + id, Album.class);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting album " + id);
        restTemplate.delete(albumServiceUrl + "/albums/" + id);
    }
}
