package com.example.inventoryservice.services;

import com.code.share.codesharing.model.MessageResponse;
import com.code.share.codesharing.model.PagingRequest;
import com.code.share.codesharing.model.ResultMsg;
import com.code.share.codesharing.model.ResultPage;
import com.example.inventoryservice.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InventoryProductService {

    ResultPage<ProductPageModel> onSelectProducts(PagingRequest<SearchProductQueryModel> pPage);

    ResultMsg onAddEditProduct(ProductActionModel productActionModel, List<MultipartFile> productImage);

    ResultMsg deleteSelectedProduct(ProductIdModel pIdModel);

}
