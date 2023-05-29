package com.datasouk.service.arango.node;

import com.datasouk.core.dto.request.PayLoad;
import com.datasouk.core.exception.ServiceException;
import com.datasouk.core.models.arango.Node;
import com.datasouk.core.repository.NodePinCollectionRepository;
import com.datasouk.core.repository.TeamsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamsServiceImpl implements TeamsService{
    private final TeamsRepository teamsRepository;
    public List<Object> allTeams() throws ServiceException {
        List<Object>  nodeinfo = teamsRepository.getTeams();
        return nodeinfo;
    }
}
