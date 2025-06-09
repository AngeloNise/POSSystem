package com.microservice.systemservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.systemservice.dto.RedisKeyDto;
import com.microservice.systemservice.models.Announcements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Angel
 * @created 14/02/2023 - 10:20 AM
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/redis")
public class RedisController {
/*
    //private final RedisTemplate redisTemplate;

    @GetMapping("/getKeys")
    public List<RedisKeyDto> getAllRedisKeys() {
        Set<String> redisKeys = redisTemplate.keys("*");
        assert redisKeys != null;
        List<RedisKeyDto> redisKeyDtoList = redisKeys.stream().map(this::mapRedisKeysToDto).collect(Collectors.toList());
        return redisKeyDtoList;
    }
    private RedisKeyDto mapRedisKeysToDto(Object value) {
        return RedisKeyDto.builder()
                .key(value.toString())
                .type(StringUtils.defaultIfBlank(redisTemplate.type(value.toString()).toString(),null))
                .build();
    }

    @GetMapping("/getValue/{redisKey}")
    public Set<Object> getAllValuesByKey(@PathVariable("redisKey") String redisKey){
        Set<Object> allAnnouncements = redisTemplate.opsForZSet().rangeByScore(
                redisKey, Instant.ofEpochMilli(Long.MIN_VALUE).toEpochMilli(),Instant.ofEpochMilli(Long.MAX_VALUE).toEpochMilli());
        assert allAnnouncements != null;
        return allAnnouncements;
    }

    @PutMapping("/deleteKey/{redisKey}")
    public boolean deleteRedisKey(@PathVariable("redisKey") String redisKey){
       return Boolean.TRUE.equals(redisTemplate.delete(redisKey));
    }

    @PostMapping("/deleteValue/{redisKey}")
    public void deleteValueOnKey(@PathVariable("redisKey") String redisKey, @RequestBody Map<String,Object> valueMap){
        Object value = MapUtils.getObject(valueMap,"value");
        redisTemplate.opsForZSet().remove(redisKey,value);
    }

    @PostMapping("/toggleRead/{redisKey}")
    public void toggleReadState(@PathVariable("redisKey") String redisKey, @RequestBody Map<String,Object> valueMap){
        ObjectMapper objectMapper = new ObjectMapper();
        Announcements announcement = objectMapper.convertValue(MapUtils.getObject(valueMap,"value"),Announcements.class);
        redisTemplate.opsForZSet().remove(redisKey,announcement);
        //announcement.setRead(!announcement.isRead());
        redisTemplate.opsForZSet().add(redisKey,announcement,Instant.now().toEpochMilli());
    }*/
}
