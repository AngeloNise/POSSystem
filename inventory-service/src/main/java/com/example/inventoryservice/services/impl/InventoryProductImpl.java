package com.example.inventoryservice.services.impl;

import com.code.share.codesharing.model.PagingRequest;
import com.code.share.codesharing.model.ResultMsg;
import com.code.share.codesharing.model.ResultPage;
import com.example.inventoryservice.dto.SearchProductDto;
import com.example.inventoryservice.models.*;
import com.example.inventoryservice.repository.ProductRepository;
import com.code.share.codesharing.model.MessageResponse;
import com.example.inventoryservice.repository.ProductSerialsRepository;
import com.example.inventoryservice.services.InventoryProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.collections4.CollectionUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class InventoryProductImpl implements InventoryProductService {
    @Value("${Product.Image}")
    private String productImagePath;

    private final ProductRepository productRepository;
    private final ProductSerialsRepository productSerialsRepository;
    MessageResponse msgResponse = new MessageResponse();

    public InventoryProductImpl(ProductRepository productRepository, ProductSerialsRepository productSerialsRepository) {
        this.productRepository = productRepository;
        this.productSerialsRepository = productSerialsRepository;

    }

    @Transactional
    @Override
    public ResultPage<ProductPageModel>onSelectProducts(PagingRequest<SearchProductQueryModel> pPage) {

        SearchProductDto sProductDto = new SearchProductDto(pPage);

        try {

            return new ResultPage<>(productRepository.getProductList(sProductDto),productRepository.getTotalCount(sProductDto),pPage.getLimit(),pPage.getStart());

        }catch (Exception e){

            return new ResultPage<>();

        }

    }


    @Transactional
    @Override
    public ResultMsg onAddEditProduct(ProductActionModel productInfo,  List<MultipartFile> productImage) {

        String action;
        Integer productId;
        action = productInfo.getAction();

        ProductActionModel productActionModel;
        productActionModel = productInfo;

        try {

            if (action.equals("add")) {
                productRepository.onAddNewProduct(productActionModel);
                productId = productActionModel.getId();

                if (productImage != null && !CollectionUtils.isEmpty(productImage) && productId != null) {
                    onUpdateImage(productImage, productId);
                }

                if (productInfo.getSerialData().getSerialNumber() != null && !productInfo.getSerialData().getSerialNumber().isEmpty() && productId != null)
                {
                    productSerialsRepository.insertSerialNumbers(productId, productInfo.getSerialData().getSerialNumber());
                }

                return new ResultMsg<>().success("product added successfully", "success");

            } else if (action.equals("edit")) {
                productActionModel.setId(productInfo.getId());
                productRepository.onEditNewProduct(productActionModel);
                Integer proId = productActionModel.getId();

                if (productInfo.getSerialData().getSerialNumber() != null && !productInfo.getSerialData().getSerialNumber().isEmpty() && productInfo.getSerialData().getSerialNumber() != null) {
                    productSerialsRepository.insertSerialNumbers(productInfo.getSerialData().getProductId(), productInfo.getSerialData().getSerialNumber());
                }

                if (productImage != null && !CollectionUtils.isEmpty(productImage) && proId != null) {
                    onUpdateImage(productImage, proId);
                }

                return new ResultMsg<>().success("product Edited successfully", "success");
            } else {
                return new ResultMsg<>().success("Something went wrong, Check required fields", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultMsg<>().success("Something went wrong, Check required fields", "error");
        }
    }

    @Override
    public ResultMsg deleteSelectedProduct(ProductIdModel pIdModel) {
        try{
        if (!pIdModel.getProductId().equals(null)){

            String imagePath = productRepository.fetchProductImagePath(pIdModel);

            if (imagePath != null && !imagePath.equals("/product-image/Placeholder.jpg")) {
                Path path = Paths.get(productImagePath + imagePath);
                Files.deleteIfExists(path);
            }

            productRepository.deleteSelectedProduct(pIdModel);
            productRepository.deleteAssociatedSerials(pIdModel);
            return new ResultMsg().success("Product deleted successfully", "success");
        }else{
            return new ResultMsg().success("Product deletion Failed", "error");
        }
        }catch (Exception e) {
            e.printStackTrace();
            return new ResultMsg().success("Product deletion failed", "error");
        }
    }


    // function for image update
    public String onUpdateImage(List<MultipartFile> img, Integer productId) {
        String imgPath = "/product-image/Placeholder.jpg";
        UploadImageModel imgObjModel = new UploadImageModel();

        try {
            imgPath = this.onSaveProductImage(img.get(0), String.valueOf(productId));

            imgObjModel.setImgPath(imgPath);
            imgObjModel.setProductId(productId);
            productRepository.uploadProductImage(imgObjModel);

        } catch (IOException e) {

            e.printStackTrace();
            return "0";
        }

        return imgPath;
    }


    /**
     * FOR SAVING PRODUCT IMAGE FUNCTION
     */
    public String onSaveProductImage(MultipartFile image, String productId) throws IOException {

        String fileExtension = image.getOriginalFilename()
                .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        //Product Image directory
        String fileName = "product_" + productId + "." + fileExtension;

        Path path = Paths.get(productImagePath + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());

        return fileName;
    }
}
