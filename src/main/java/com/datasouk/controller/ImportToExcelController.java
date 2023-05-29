package com.datasouk.controller;

import com.datasouk.core.models.arango.Node;
import com.datasouk.service.importExcel.ImportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/import")
@AllArgsConstructor
public class ImportToExcelController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportToExcelController.class);

  private final ImportService importService;

  @PostMapping("/excel")
  @Tag(name = "ImportExport", description = "Api's to Related to Import/Export the excel")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Importing the  Meta Data"),
      @ApiResponse(responseCode = "400", description = "Invalid supplied"),
      @ApiResponse(responseCode = "404", description = "Not Found"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")})

  public List<Node> getAllCompany(@RequestParam("file") MultipartFile file) {

    return importService.importExcel(file);
  }


}
