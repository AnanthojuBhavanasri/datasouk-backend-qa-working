package com.datasouk.service.exportExcel;

import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.models.arango.TargetRelation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExportServiceImpl implements ExportService {

  private final List<String> exportHeaders = new ArrayList<>(
      Arrays.asList("_key", "identifier", "displayName", "type/metaCollectionName", "type/name",
          "type/id",
          "articulationScore", "name", "ratingsCount", "avgRating", "id", "sourceCatalog"));
  private final List<String> attributeHeaders = new ArrayList<>();
  private final List<String> relationTargetHeaders = new ArrayList<>();
  private final List<String> relationSourceHeaders = new ArrayList<>();
  int maxTargetRelationValue = 0;
  int maxAttributeValue = 0;
  int maxSourceRelationValue = 0;
  private XSSFWorkbook workbook;
  private XSSFSheet sheet;

  public ExportServiceImpl() {
    this.workbook = new XSSFWorkbook();
  }

  @Override
  public ResponseEntity<Resource> exportAsExcel(MultipartFile file) throws ServiceException {

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<Node>> typeReference = new TypeReference<List<Node>>() {
    };

    try {
      List<Node> assets = mapper.readValue(file.getInputStream(), typeReference);

      ByteArrayInputStream in = generateExcel(assets);

      InputStreamResource fileRes = new InputStreamResource(in);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excel-sheet")
          .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(fileRes);

    } catch (IOException e) {
      throw new RuntimeException("fail to store excel data: " + e.getMessage());
    }
  }

  public ByteArrayInputStream generateExcel(List<Node> assets) throws ServiceException {

    try {
      workbook = new XSSFWorkbook();
      writeExcelHeader(assets);
      writeExcel(assets);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      workbook.close();

      out.close();
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
    }
  }

  private void writeExcelHeader(List<Node> assets) throws ServiceException {

    sheet = workbook.createSheet("Asset");

    Row row = sheet.createRow(0);

    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(12);
    style.setFont(font);

    HashMap<Integer, Integer> atrributeMap = new HashMap<>();
    HashMap<Integer, Integer> targetRelationMap = new HashMap<>();
    HashMap<Integer, Integer> sourceRelationMap = new HashMap<>();

    for (int i = 0; i < assets.size(); i++) {
      Node asset = assets.get(i);
      atrributeMap.put(i, asset.getAttributes().size());
      targetRelationMap.put(i, asset.getRelations().getTargets().size());
      sourceRelationMap.put(i, asset.getRelations().getSources().size());
    }

    maxAttributeValue = (Collections.max(atrributeMap.values()));
    maxTargetRelationValue = (Collections.max(targetRelationMap.values()));
    maxSourceRelationValue = (Collections.max(sourceRelationMap.values()));

    for (int i = 0; i < maxAttributeValue; i++) {
      attributeHeaders.add("attributes/" + i + "/name");
      attributeHeaders.add("attributes/" + i + "/value");
    }

    for (int i = 0; i < maxTargetRelationValue; i++) {
      relationTargetHeaders.add("relations/targets/" + i + "/coRole");
      // relationTargetHeaders.add("relations/targets/" + i + "/id");
      relationTargetHeaders.add("relations/targets/" + i + "/target/displayName");
      relationTargetHeaders.add("relations/targets/" + i + "/target/name");
      relationTargetHeaders.add("relations/targets/" + i + "/target/id");
      relationTargetHeaders.add("relations/targets/" + i + "/target/type");
      // relationTargetHeaders.add("relations/targets/" + i + "/typeId");
    }

    for (int i = 0; i < maxSourceRelationValue; i++) {
      relationSourceHeaders.add("relations/sources/" + i + "/role");
      // relationSourceHeaders.add("relations/sources/" + i + "/id");
      relationSourceHeaders.add("relations/sources/" + i + "/source/displayName");
      relationSourceHeaders.add("relations/sources/" + i + "/source/name");
      relationSourceHeaders.add("relations/sources/" + i + "/source/id");
      relationSourceHeaders.add("relations/sources/" + i + "/source/type");
      // relationSourceHeaders.add("relations/sources/" + i + "/typeId");
    }

    exportHeaders.addAll(attributeHeaders);
    exportHeaders.addAll(relationTargetHeaders);
    exportHeaders.addAll(relationSourceHeaders);

    for (int col = 0; col < exportHeaders.size(); col++) {
      createCell(row, col, exportHeaders.get(col), style);
    }

  }

  public void createCell(Row row, int columnCount, Object value, CellStyle style)
      throws ServiceException {
    sheet.autoSizeColumn(columnCount);
    Cell cell = row.createCell(columnCount);
    if (value instanceof Integer) {
      cell.setCellValue((Integer) value);
    } else if (value instanceof Long) {
      cell.setCellValue((Long) value);
    } else if (value instanceof Boolean) {
      cell.setCellValue((Boolean) value);
    } else {
      cell.setCellValue((String) value);
    }
    cell.setCellStyle(style);
  }

  public void writeExcel(List<Node> assets) {

    int rowCount = 1;

    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setFontHeight(12);
    style.setFont(font);

    int index = 1;
    for (Node asset : assets) {
      Row row = sheet.createRow(rowCount++);
      int columnCount = 0;
      String id = asset.getId();
      String identifier = asset.getIdentifier();
      String displayName = asset.getDisplayName();
      String metaCollectionName = asset.getType().getMetaCollectionName();
      String typeName = asset.getType().getName();
      String typeId = asset.getType().getId();
      String articulationScore = asset.getArticulationScore();
      String name = asset.getName();
      String ratingsCount = asset.getRatingsCount();
      String avgRating = asset.getAvgRating();
      String assetId = asset.getId();
      String sourceCatalog = asset.getSourceCatalog();

      createCell(row, columnCount++, id, style);
      createCell(row, columnCount++, identifier, style);
      createCell(row, columnCount++, displayName, style);
      createCell(row, columnCount++, metaCollectionName, style);
      createCell(row, columnCount++, typeName, style);
      createCell(row, columnCount++, typeId, style);
      createCell(row, columnCount++, articulationScore, style);
      createCell(row, columnCount++, name, style);
      createCell(row, columnCount++, ratingsCount, style);
      createCell(row, columnCount++, avgRating, style);
      createCell(row, columnCount++, assetId, style);
      createCell(row, columnCount++, sourceCatalog, style);
      for (int i = 0; i < asset.getAttributes().size(); i++) {
        createCell(row, columnCount++, asset.getAttributes().get(i).getName(), style);
        createCell(row, columnCount++, asset.getAttributes().get(i).getValue(), style);
      }
      if (asset.getAttributes().size() != maxAttributeValue) {
        int remainingSize = maxAttributeValue - asset.getAttributes().size();
        if (remainingSize > 0) {
          for (int i = 0; i < remainingSize; i++) {
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
          }
        }
      }

      List<TargetRelation> targetRelations = asset.getRelations().getTargets();
      List<SourceRelation> sourceRelations = asset.getRelations().getSources();

      for (int i = 0; i < targetRelations.size(); i++) {
        createCell(row, columnCount++, targetRelations.get(i).getCoRole(), style);
        //createCell(row, columnCount++, targetRelations.get(i).getId(), style);
        createCell(row, columnCount++, targetRelations.get(i).getTarget().getDisplayName(), style);
        createCell(row, columnCount++, targetRelations.get(i).getTarget().getName(), style);
        createCell(row, columnCount++, targetRelations.get(i).getTarget().getId(), style);
        createCell(row, columnCount++, targetRelations.get(i).getTarget().getType(), style);
        //createCell(row, columnCount++, targetRelations.get(i).getTypeId(), style);
      }
      if (targetRelations.size() != maxTargetRelationValue) {
        int remainingSize = maxTargetRelationValue - targetRelations.size();
        if (remainingSize > 0) {
          for (int i = 0; i < remainingSize; i++) {
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
          }
        }

      }
      for (int i = 0; i < sourceRelations.size(); i++) {
        createCell(row, columnCount++, sourceRelations.get(i).getRole(), style);
        //  createCell(row, columnCount++, sourceRelations.get(i).getId(), style);
        createCell(row, columnCount++, sourceRelations.get(i).getSource().getDisplayName(), style);
        createCell(row, columnCount++, sourceRelations.get(i).getSource().getName(), style);
        createCell(row, columnCount++, sourceRelations.get(i).getSource().getId(), style);
        createCell(row, columnCount++, sourceRelations.get(i).getSource().getType(), style);
        // createCell(row, columnCount++, sourceRelations.get(i).getTypeId(), style);
      }
      if (sourceRelations.size() != maxSourceRelationValue) {
        int remainingSize = maxSourceRelationValue - sourceRelations.size();
        if (remainingSize > 0) {
          for (int i = 0; i < remainingSize; i++) {
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
            createCell(row, columnCount++, "", style);
          }
        }

      }
      index++;

    }
  }

  @Override
  public ResponseEntity<InputStreamResource> exportJsonAsExcel(
      ByteArrayInputStream excelFilePath1) {
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<Node>> typeReference = new TypeReference<List<Node>>() {
    };

    try {
      List<Node> assets = mapper.readValue(excelFilePath1, typeReference);

      ByteArrayInputStream in = generateExcel(assets);

      InputStreamResource fileRes = new InputStreamResource(in);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excel-sheet")
          .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(fileRes);

    } catch (IOException e) {
      throw new RuntimeException("fail to store excel data: " + e.getMessage());
    }
  }
}
