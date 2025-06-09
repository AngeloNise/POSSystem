package com.microservice.systemservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.models.GetRequest;
import com.microservice.systemservice.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum Util {
    me;
    String path = "json/resources/";

    public void DeleteFile(String filename){

        filename += ".json";
        File file = new File(path +filename);

        if (file.delete()) {
            System.out.println("json file delete");
        } else {
            System.out.println("json file not delete");
        }
    }

    public List<Map> GetData(String functionName, List<Map> dataSource){

        List<Map> res = new ArrayList<>();
        if(CheckFileExist(functionName)){
            GetRequest getRequest = UniMapper(functionName);
            res  = getRequest.getMapList();
        } else {
            res = dataSource;
            //[
            //]
            Object[] object = new Object[1];
            object[0] = res;
            SaveUniMapper("","",functionName, object);
        }

        return res;
    }

    public boolean CheckFileExist(String name){

        name += ".json";

        File file = new File(path + name);

        if(file.exists()){
            return true;
        } else {
            return false;
        }
    }

    public GetRequest UniMapper( String filename){

        filename +=".json";
        try{
            //return new ObjectMapper().readValue(new File("json/resources/json_car.json"), GetRequest.class);
            return new ObjectMapper().readValue(new File(path +filename), GetRequest.class);
        }catch (Exception ee)
        {        }
        return null;
    }

    public void SaveUniMapper(String name, String type, String remarks, Object[] data){

        List<Map> resAccounts = (List<Map>) data[0];

        ObjectMapper objectMapper = new ObjectMapper();
        GetRequest getRequest = new GetRequest();
        getRequest.name = name;
        getRequest.type = type;
        getRequest.remarks = remarks;
        getRequest.setMapList(resAccounts);

        //String path1 = "system-service/src/main/resources/";
        //String filename = getRequest.getName()+"_"+ getRequest.getRemarks()+"_"+ getRequest.getType()+".json";
        String filename = getRequest.getRemarks()+".json";

        //String userDirectory = System.getProperty("user.dir");
        String userDirectory = new File(path + filename).getAbsolutePath();

        try {
            objectMapper.writeValue(new File(userDirectory), getRequest);

            //String carAsString = objectMapper.writeValueAsString(getRequest);
            //GetRequest car = objectMapper.readValue(carAsString, GetRequest.class);

          /*  String resourceName = "json_car.json";

            //ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(getClass().getClassLoader().getResource(resourceName).getFile());
            String absolutePath = file.getAbsolutePath();*/



            //GetRequest car2 = objectMapper.readValue(new File("system-service/src/main/resources/json_car.json"), GetRequest.class);

            //String absolutePath2 = file.getAbsolutePath();

        }catch (Exception ee){
            ee.getMessage();
        }

    }

}
