package com.mibo2000.codigo.codetest.controller;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.pub.IPub;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/public")
@RestController
@RequiredArgsConstructor
public class PubController {
    private final IPub iPub;
    @GetMapping("/country")
    public ResponseEntity<BaseResponse<?>> getCountryList() {
        return ResponseEntity.ok(iPub.getCountryList());
    }
}
