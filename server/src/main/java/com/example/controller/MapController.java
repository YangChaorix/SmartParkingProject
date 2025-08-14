package com.example.controller;

import com.example.common.Result;
import com.example.service.MapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/map")
public class MapController {
    @Resource
    private MapService mapService;

    @GetMapping("/getMap")
    public Result selectAll() {
        return Result.success(mapService.selectAll());
    }
}
