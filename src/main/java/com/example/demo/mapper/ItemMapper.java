package com.example.demo.mapper;

import com.example.demo.dto.ItemDTO;
import com.example.demo.model.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toDto(Item item);

    Item fromDto(ItemDTO itemDTO);
}
