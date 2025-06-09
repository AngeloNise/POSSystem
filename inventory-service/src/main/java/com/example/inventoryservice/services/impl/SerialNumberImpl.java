package com.example.inventoryservice.services.impl;

import com.code.share.codesharing.model.ResultMsg;
import com.example.inventoryservice.dto.ShowSerialNumbersDto;
import com.example.inventoryservice.models.ProductIdModel;
import com.example.inventoryservice.models.ProductIdSerialModel;
import com.example.inventoryservice.models.SerialNumberModel;
import com.example.inventoryservice.repository.ProductSerialsRepository;
import com.example.inventoryservice.services.SerialNumberService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SerialNumberImpl implements SerialNumberService {

    private final ProductSerialsRepository productSerialsRepository;

    public SerialNumberImpl(ProductSerialsRepository sNumbersRepository){
        this.productSerialsRepository = sNumbersRepository;
    }


    @Override
    public  SerialNumberModel showSerialNumbers(ShowSerialNumbersDto pIdModel){

        List<String> sNumber = productSerialsRepository.selectAllSerialNumbers(pIdModel);

        return new SerialNumberModel(sNumber);
    }

    @Override
    public ResultMsg addSerialNumbers(ProductIdSerialModel pModel) {

        Integer productId = pModel.getProductId();
        List<String> serialList =  pModel.getSerialNumber();

        try {
            if(!serialList.isEmpty()) {
                productSerialsRepository.insertSerialNumbers(productId, serialList);
                return new ResultMsg<>().success("Serial numbers " + serialList + " added successfully", "success");
            }else {
                return new ResultMsg<>().success("Please check field and press add plus sign", "warn");
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResultMsg<>().success("Something went wrong, please try again", "error");
        }
    }

    @Override
    public ResultMsg deleteSerialNumbers(ProductIdSerialModel pModel) {

        Integer productId = pModel.getProductId();
        List<String> serialList =  pModel.getSerialNumber();
        try {
            if(!serialList.isEmpty()) {
            productSerialsRepository.deleteSerialNumbers(productId, serialList);
            return new ResultMsg<>().success("Serial numbers " + serialList + " have been deleted successfully" , "success");
            }else {
                return new ResultMsg<>().success("Please check field and press add plus sign", "warn");
            }

        }catch (Exception e){
            return new ResultMsg<>().success("Something went wrong, please try again", "error");
        }
    }

}
