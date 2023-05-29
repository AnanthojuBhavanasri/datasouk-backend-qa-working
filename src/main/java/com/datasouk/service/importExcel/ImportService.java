package com.datasouk.service.importExcel;

import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface ImportService {

  public List<Node> importExcel(MultipartFile file) throws ServiceException;

}
