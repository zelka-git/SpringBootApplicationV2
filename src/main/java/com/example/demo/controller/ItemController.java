package com.example.demo.controller;

import com.example.demo.dto.ItemDTO;
import com.example.demo.service.ItemService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Page<ItemDTO> getAll(Pageable pageable) {
        return itemService.getAll(pageable);
    }

    @GetMapping("{id}")
    public ItemDTO getById(UUID id) {
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDTO save(ItemDTO item) {
        return itemService.save(item);
    }

    @PutMapping("/{id}")
    public void update(UUID id, @PathVariable ItemDTO item) {
        itemService.update(id, item);
    }

    @DeleteMapping("{id}")
    public void delete(UUID id) {
        itemService.delete(id);
    }
}
