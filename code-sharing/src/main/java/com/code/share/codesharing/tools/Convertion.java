package com.code.share.codesharing.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Convertion{
    public Map JsonStrToMap(String data){

        String str001 = data.replace("\\","");
        str001 = str001.replace("[\"","[");
        str001 = str001.replace("\"]","]");

        Gson gson = new Gson();
        JsonElement jelem = gson.fromJson(str001, JsonElement.class);
        JsonObject jobj = jelem.getAsJsonObject();

        Map yourHashMap = new Gson().fromJson(jobj, HashMap.class);

        return yourHashMap;
    }

    public List<Map> ObjectToListMap(Object obj){
        //
        ObjectMapper oMapper = new ObjectMapper();
        List<Map> mapList = oMapper.convertValue(obj, List.class);

        return mapList;
    }

    public Map ObjectToMap(String data)  {

        ObjectMapper oMapper = new ObjectMapper();
        /*Map mapObj = oMapper.convertValue(obj, Map.class);*/

        Map mapObj = null;
        try {
            mapObj = oMapper.readValue(data, Map.class);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
        }

        return mapObj;
    }

    public String ConvertPasswordToBCryptHash(String passwordString){
        //
        char[] passwordChars = new char[passwordString.length()];
        for (int i = 0; i < passwordString.length(); i++) {
            passwordChars[i] = passwordString.charAt(i);
        }

        String hashed = BCrypt.hashpw(String.valueOf(passwordChars), BCrypt.gensalt(12));

        return hashed;
    }

    public JSONObject StrToJson(String str) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json  = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
