package com.datasouk.service.exportExcel;

import com.datasouk.core.exception.ServiceException;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ExportService {

  public ResponseEntity<Resource> exportAsExcel(MultipartFile file) throws ServiceException;

  public ResponseEntity<InputStreamResource> exportJsonAsExcel(ByteArrayInputStream excelFilePath1);

}
