package com.catalog.album.web;

import com.catalog.album.domain.Album;
import com.catalog.album.repository.AlbumRepository;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumRepository repository;

    public AlbumController(AlbumRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Album> albums() {
        return repository.findAll();
    }

    @PutMapping
    public Album add(@RequestBody @Valid Album album) {
        return repository.save(album);
    }

    @PostMapping
    public Album update(@RequestBody @Valid Album album) {
        return repository.save(album);
    }

    @GetMapping("/{id}")
    public Album getById(@PathVariable String id) {
        return repository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        repository.deleteById(id);
    }
}
