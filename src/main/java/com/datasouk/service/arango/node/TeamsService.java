package com.datasouk.service.arango.node;

import com.datasouk.core.exception.ServiceException;

import java.util.List;

public interface TeamsService {
   List<Object> allTeams() throws ServiceException;
}
