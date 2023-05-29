package com.datasouk.service.importExcel;


import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Attribute;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.Relation;
import com.datasouk.core.models.arango.RelationAttribute;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.models.arango.TargetRelation;
import com.datasouk.core.models.arango.Type;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ImportServiceImpl implements ImportService {

  private static Logger logger = LoggerFactory.getLogger(ImportServiceImpl.class);

  public Workbook workbook;
  private Map<String, Integer> columns = new LinkedHashMap<>();

  public ImportServiceImpl() {
    this.workbook = new XSSFWorkbook();
  }

  @Override
  public List<Node> importExcel(MultipartFile file) throws ServiceException {
    List<Node> assets = new ArrayList<>();
    try {
      assets = readExcel(file.getInputStream(), "Asset");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return assets;
  }

  public List<Node> readExcel(InputStream is, String sheetName) throws ServiceException {
    try {
      workbook = new XSSFWorkbook(is);
      List<Node> assets = new ArrayList<>();
      Sheet sheet = workbook.getSheet(sheetName);

      /*
       * Iterator<Row> rows = sheet.iterator();
       *
       * Row currentRow = rows.next(); currentRow.forEach(cell -> {
       * columns.put(cell.getStringCellValue(), cell.getColumnIndex()); });
       * logger.info("======" + sheet.getPhysicalNumberOfRows()); int rowNumber = 0; while
       * (rows.hasNext()) { Row currentRoww = rows.next(); // skip header if (rowNumber == 0) {
       * rowNumber++; continue; } for (Map.Entry<String, Integer> entry : columns.entrySet()) { Cell
       * cell = currentRoww.getCell(entry.getValue()); Node asset = new Node();
       * asset.setIdentifier(getCellData(cell)); asset.setIdentifier(getCellData(cell)); }
       *
       * }
       */
      Iterator<Row> rows = sheet.iterator();
      Row headerRow = rows.next();
      headerRow.forEach(cell -> {
        columns.put(cell.getStringCellValue(), cell.getColumnIndex());
      });
      int totalRows = sheet.getPhysicalNumberOfRows();
      for (int i = 0; i < totalRows; i++) {
        if (i == 0) {
          continue;
        }
        Node asset = new Node();
        List<Attribute> attributes = new ArrayList<>();
        Relation relations = new Relation();
        Type type = new Type();
        for (Map.Entry<String, Integer> entry : columns.entrySet()) {

          Cell currentRow = sheet.getRow(i).getCell(entry.getValue());
          String cellValue = getCellData(currentRow);
          List<String> keyList = Arrays.asList(entry.getKey().split("/"));
          String key = keyList.get(0);
          switch (key) {
            case "id":
              asset.setId(cellValue);
              break;
//            case "_key":
//              asset.set_key(cellValue);
//              break;
            case "attributes":
              buildAttributes(asset, attributes, keyList, cellValue);
              break;
            case "identifier":
              asset.setIdentifier(cellValue);
              break;
            case "displayName":
              asset.setDisplayName(cellValue);
              break;
            case "articulationScore":
              asset.setArticulationScore(cellValue);
              break;
            case "name":
              asset.setName(cellValue);
              break;
            case "ratingsCount":
              asset.setRatingsCount(cellValue);
              break;
            case "avgRating":
              asset.setAvgRating(cellValue);
              break;
            case "sourceCatalog":
              asset.setSourceCatalog(cellValue);
              break;
            case "type":
              buildAssetType(asset, type, keyList, cellValue);
              break;
            case "relations":
              buildRelations(asset, relations, keyList, cellValue);
              break;
            default:
              break;
          }

        }
        assets.add(asset);
      }
      workbook.close();
      is.close();
      return assets;
    } catch (IOException e) {
      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
    }


  }


  public String getCellData(Cell cell) throws ServiceException {

    String CellData = null;
    if (cell == null) {
      return "";
    }
    switch (cell.getCellType()) {
      case STRING:
        CellData = cell.getStringCellValue();
        break;
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          CellData = String.valueOf(cell.getDateCellValue());
        } else {
          CellData = String.valueOf((long) cell.getNumericCellValue());
        }
        break;
      case BOOLEAN:
        CellData = Boolean.toString(cell.getBooleanCellValue());
        break;
      case BLANK:
        CellData = "";
        break;
      default:
        break;
    }
    return CellData;
  }

  public void buildAttributes(Node asset, List<Attribute> attributes, List<String> keyList,
      String cellValue) throws ServiceException {
    int attributeIndex = Integer.parseInt(keyList.get(1));
    try {
      Attribute existingAttribute = attributes.get(attributeIndex);
      existingAttribute.setValue(cellValue);
    } catch (IndexOutOfBoundsException e) {
      if (!cellValue.isEmpty()) {
        Attribute att = new Attribute();
        att.setName(cellValue);
        attributes.add(att);
      }
    }
    asset.setAttributes(attributes);
  }

  public void buildAssetType(Node asset, Type type, List<String> keyList, String cellValue)
      throws ServiceException {
    String typeKey = keyList.get(1);
    switch (typeKey) {
      case "metaCollectionName":
        type.setMetaCollectionName(cellValue);
        break;
      case "name":
        type.setName(cellValue);
        break;
      case "id":
        type.setId(cellValue);
        break;
      default:
        break;
    }
    asset.setType(type);
  }

  public void buildRelations(Node asset, Relation relations, List<String> keyList,
      String cellValue) throws ServiceException {
    String relationType = keyList.get(1);

    if (relationType.equals("targets")) {
      try {
        int targetIndex = Integer.parseInt(keyList.get(2));
        if (relations.getTargets() != null) {
          TargetRelation existingTargetRelation = relations.getTargets().get(targetIndex);
          RelationAttribute targetAttribute = existingTargetRelation.getTarget();
          buildTargetRelation(existingTargetRelation, targetAttribute, keyList, cellValue);
          existingTargetRelation.setTarget(targetAttribute);
          asset.getRelations().getTargets().set(targetIndex, existingTargetRelation);
        } else {
          if (!cellValue.isEmpty()) {
            TargetRelation targetRelation = new TargetRelation();
            RelationAttribute targetAttribute = new RelationAttribute();
            buildTargetRelation(targetRelation, targetAttribute, keyList, cellValue);
            targetRelation.setTarget(targetAttribute);
            List<TargetRelation> targets = new ArrayList<>();
            targets.add(targetRelation);
            relations.setTargets(targets);
            asset.setRelations(relations);
          }
        }

      } catch (IndexOutOfBoundsException e) {
        if (!cellValue.isEmpty()) {

          if (relations.getTargets() != null) {
            TargetRelation targetRelation = new TargetRelation();
            RelationAttribute targetAttribute = new RelationAttribute();
            buildTargetRelation(targetRelation, targetAttribute, keyList, cellValue);
            targetRelation.setTarget(targetAttribute);
            relations.getTargets().add(targetRelation);
            asset.setRelations(relations);
          } else {
            TargetRelation targetRelation = new TargetRelation();
            RelationAttribute targetAttribute = new RelationAttribute();
            buildTargetRelation(targetRelation, targetAttribute, keyList, cellValue);
            targetRelation.setTarget(targetAttribute);
            List<TargetRelation> targets = new ArrayList<>();
            targets.add(targetRelation);
            relations.setTargets(targets);
            asset.setRelations(relations);
          }

        }
      }
    } else if (relationType.equals("sources")) {
      try {
        int sourceIndex = Integer.parseInt(keyList.get(2));
        if (relations.getSources() != null) {
          SourceRelation existingSourceRelation = relations.getSources().get(sourceIndex);
          RelationAttribute sourceAttribute = existingSourceRelation.getSource();
          buildSourceRelation(existingSourceRelation, sourceAttribute, keyList, cellValue);
          existingSourceRelation.setSource(sourceAttribute);
          asset.getRelations().getSources().set(sourceIndex, existingSourceRelation);
        } else {
          if (!cellValue.isEmpty()) {
            SourceRelation sourceRelation = new SourceRelation();
            RelationAttribute sourceAttribute = new RelationAttribute();
            buildSourceRelation(sourceRelation, sourceAttribute, keyList, cellValue);
            sourceRelation.setSource(sourceAttribute);
            List<SourceRelation> sources = new ArrayList<>();
            sources.add(sourceRelation);
            relations.setSources(sources);
            asset.setRelations(relations);
          }
        }

      } catch (IndexOutOfBoundsException e) {
        if (!cellValue.isEmpty()) {

          if (relations.getSources() != null) {
            SourceRelation sourceRelation = new SourceRelation();
            RelationAttribute sourceAttribute = new RelationAttribute();
            buildSourceRelation(sourceRelation, sourceAttribute, keyList, cellValue);
            sourceRelation.setSource(sourceAttribute);
            relations.getSources().add(sourceRelation);
            asset.setRelations(relations);
          } else {
            SourceRelation sourceRelation = new SourceRelation();
            RelationAttribute sourceAttribute = new RelationAttribute();
            buildSourceRelation(sourceRelation, sourceAttribute, keyList, cellValue);
            sourceRelation.setSource(sourceAttribute);
            List<SourceRelation> sources = new ArrayList<>();
            sources.add(sourceRelation);
            relations.setSources(sources);
            asset.setRelations(relations);
          }

        }
      }
    }


  }

  public void buildTargetRelation(TargetRelation targetRelation, RelationAttribute targetAttribute,
      List<String> keyList, String cellValue) throws ServiceException {

    String targetOther = keyList.get(3);

    if (keyList.size() == 5) {

      String targetKey = keyList.get(4);
      switch (targetKey) {
        case "displayName":
          targetAttribute.setDisplayName(cellValue);
          break;
        case "name":
          targetAttribute.setName(cellValue);
          break;
        case "id":
          targetAttribute.setId(cellValue);
          break;
        case "type":
          targetAttribute.setType(cellValue);
          break;
        default:
          break;
      }
    } else {
      switch (targetOther) {
        case "coRole":
          targetRelation.setCoRole(cellValue);
          break;
        default:
          break;
      }
    }

  }

  public void buildSourceRelation(SourceRelation sourceRelation, RelationAttribute sourceAttribute,
      List<String> keyList, String cellValue) throws ServiceException {

    String sourceOther = keyList.get(3);

    if (keyList.size() == 5) {

      String targetKey = keyList.get(4);
      switch (targetKey) {
        case "displayName":
          sourceAttribute.setDisplayName(cellValue);
          break;
        case "name":
          sourceAttribute.setName(cellValue);
          break;
        case "id":
          sourceAttribute.setId(cellValue);
          break;
        case "type":
          sourceAttribute.setType(cellValue);
          break;
        default:
          break;
      }
    } else {
      switch (sourceOther) {
        case "role":
          sourceRelation.setRole(cellValue);
          break;
        default:
          break;
      }
    }

  }
}
