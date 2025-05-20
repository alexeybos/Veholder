package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/trips")
public class TripRestController {

    @Autowired
    private TripService service;


}
