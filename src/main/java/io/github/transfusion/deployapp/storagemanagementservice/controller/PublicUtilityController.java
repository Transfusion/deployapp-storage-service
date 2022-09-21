package io.github.transfusion.deployapp.storagemanagementservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/utility/public")
public class PublicUtilityController {
    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return new ResponseEntity<>("hello world", HttpStatus.OK);
    }
}
