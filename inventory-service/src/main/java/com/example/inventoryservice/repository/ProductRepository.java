package com.example.inventoryservice.repository;
import com.example.inventoryservice.dto.SearchProductDto;
import com.example.inventoryservice.models.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
@Mapper
@Repository

public interface ProductRepository {
        List<ProductPageModel> getProductList(SearchProductDto sDto);

        Integer getTotalCount(SearchProductDto sDto);

        Integer onAddNewProduct(ProductActionModel addParams);

        void onEditNewProduct(ProductActionModel editParams);

        void deleteSelectedProduct(ProductIdModel pIdModel);

        String fetchProductImagePath(ProductIdModel pIdModel);
        void deleteAssociatedSerials(ProductIdModel pIdModel);

        void uploadProductImage(UploadImageModel imgUpload);
}
