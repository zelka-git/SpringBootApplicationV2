package com.example.demo.service;

import com.example.demo.dto.ItemDTO;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.repository.ItemRepository;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public record ItemService(ItemRepository itemRepository,
                          ItemMapper itemMapper) implements CommonService<ItemDTO, UUID> {

    private static final String ITEM_NOT_FOUND = "item with id: %s not found";

    @Override
    public Page<ItemDTO> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable).map(itemMapper::toDto);
    }

    @Override
    public ItemDTO getById(UUID id) {
        return itemRepository.findById(id).map(itemMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException(String.format(ITEM_NOT_FOUND, id)));
    }

    @Override
    public ItemDTO save(ItemDTO item) {
        return itemMapper.toDto(itemRepository.save(itemMapper.fromDto(item)));
    }

    @Override
    public void update(UUID id, ItemDTO item) {
        itemRepository.findById(id).map(itemEntity -> {
            itemEntity.setName(item.getName());
            return itemEntity;
        }).orElseThrow(() -> new EntityNotFoundException(String.format(ITEM_NOT_FOUND, id)));
    }

    @Override
    public void delete(UUID id) {
        itemRepository.deleteById(id);
    }
}
