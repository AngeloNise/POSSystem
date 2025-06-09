package com.example.inventoryservice.controllers;

import com.code.share.codesharing.model.PagingRequest;
import com.code.share.codesharing.model.ResultMsg;
import com.code.share.codesharing.model.ResultPage;
import com.example.inventoryservice.dto.ShowSerialNumbersDto;
import com.example.inventoryservice.models.*;
import com.example.inventoryservice.services.InventoryProductService;
import com.code.share.codesharing.model.MessageResponse;
import com.example.inventoryservice.services.SerialNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Slf4j
public class InventoryProductController {
    private final InventoryProductService inventoryProductService;
    private final SerialNumberService serialNumberService;
    public InventoryProductController(InventoryProductService inventoryProductService, SerialNumberService serialNumberService){
        this.inventoryProductService = inventoryProductService;
        this.serialNumberService = serialNumberService;
    }


    @PostMapping("/getProductsList")
    public ResultPage<ProductPageModel> getAllProducts(@RequestBody PagingRequest<SearchProductQueryModel> rPage){
        ResultPage<ProductPageModel> resultPage = inventoryProductService.onSelectProducts(rPage);
        return resultPage;
    }

    @RequestMapping(value = "/onAddEditProductInfo",  method = RequestMethod.POST )
    public ResultMsg onAddEditProduct(@RequestPart("productInfo") ProductActionModel pInfo,
                                      @RequestPart(name ="productImage", required = false) List<MultipartFile> productImage) {
        return inventoryProductService.onAddEditProduct(pInfo, productImage);
    }

    @PostMapping("/onDeleteProduct")
    public ResultMsg deleteSelectedProduct(@RequestBody ProductIdModel pId){
        return inventoryProductService.deleteSelectedProduct(pId);
    }

    @PostMapping("/showSerialNumbers")
    public  SerialNumberModel showSerialNumbers(@RequestBody ShowSerialNumbersDto pModel){
        return serialNumberService.showSerialNumbers(pModel);
    }

    @PostMapping("/addProductSerial")
    public ResultMsg addSerialNumbers(@RequestBody ProductIdSerialModel pModel){
        return serialNumberService.addSerialNumbers(pModel);
    }

    @PostMapping("/deleteSerialNumbers")
    public ResultMsg deleteSerialNumbers(@RequestBody ProductIdSerialModel pModel){
        return serialNumberService.deleteSerialNumbers(pModel);
    }

}
