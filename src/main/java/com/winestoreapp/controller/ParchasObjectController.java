package com.winestoreapp.controller;

import com.winestoreapp.dto.mapper.PurchaseObjectMapper;
import com.winestoreapp.dto.mapper.ShoppingCardMapper;
import com.winestoreapp.repository.PurchaseObjectRepository;
import com.winestoreapp.repository.ShoppingCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/po")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PATCH, RequestMethod.DELETE})
public class ParchasObjectController {

    //todo DELETE this controller
    private final PurchaseObjectRepository repository;
    private final PurchaseObjectMapper purchaseObjectMapper;
    private final ShoppingCardRepository shoppingCardRepository;
    private final ShoppingCardMapper shoppingCardMapper;

    //    @GetMapping("/p/{id}")
    //    public PurchaseObjectDto getPoById(@PathVariable Long id) {
    //        final Optional<PurchaseObject> byId = repository.findById(id);
    //        final PurchaseObject purchaseObject = byId.get();
    //        System.out.println(byId.get());
    //        final PurchaseObjectDto dto = purchaseObjectMapper.toDto(purchaseObject);
    //        System.out.println(dto);
    //        return dto;
    //    }
    //
    //    @GetMapping("/sc/{id}")
    //    public ShoppingCardDto getScById(@PathVariable Long id) {
    //        final Optional<ShoppingCard> byId = shoppingCardRepository.findById(id);
    //        final ShoppingCard shoppingCard = byId.get();
    //        final ShoppingCardDto dto = shoppingCardMapper.toDto(shoppingCard);
    //        return dto;
    //    }
}
