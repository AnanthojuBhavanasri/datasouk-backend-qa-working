package com.datasouk.controller;

import com.datasouk.service.exportExcel.ExportService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;



@RestController
@RequestMapping("/api/export")
@AllArgsConstructor
public class ExportToExcelController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportToExcelController.class);

  private final ExportService exportService;

  @PostMapping("/excel")
  @Tag(name="ImportExport",description="Api's to Related to Import/Export the excel")
  @ApiResponses(value = { 
  		  @ApiResponse(responseCode = "200", description = "Exporting the Meta Data"),
  		  @ApiResponse(responseCode = "400", description = "Invalid supplied"),
  		  @ApiResponse(responseCode = "404", description = "Not Found"),
  		  @ApiResponse(responseCode = "500", description = "Internal Server Error")})
  public ResponseEntity<Resource> getAllCompany(@RequestParam("file") MultipartFile file) {

    return exportService.exportAsExcel(file);
  }



}
