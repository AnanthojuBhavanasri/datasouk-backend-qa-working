package com.datasouk.utils.arango;

import com.datasouk.core.models.arango.Node;
import com.datasouk.core.models.arango.SourceRelation;
import com.datasouk.core.models.arango.TargetRelation;
import com.datasouk.core.repository.NodeRepository;
import com.datasouk.core.utils.Common;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BuildRelation {

  private final NodeRepository nodeRepository;
  private final Common common;


  public void buildTargetNames(Node node, List<String> targetResults, String relationType) {
    List<TargetRelation> targetRelations = node.getRelations().getTargets();
    targetRelations.forEach(targetRelation -> {
      String targetname = targetRelation.getCoRole();
      if (targetname != null && targetname.contains(relationType)) {
        targetResults.add(targetRelation.getTarget().getName());
      }
    });

  }

  public void buildTargetNodes(Node node, List<String> targetResults, String relationType) {
    List<TargetRelation> targetRelations = node.getRelations().getTargets();
    targetRelations.forEach(targetRelation -> {
      String targetname = targetRelation.getTarget().getType();
      if (targetname != null && targetname.contains(relationType)) {
        targetResults.add(targetRelation.getTarget().getName());
      }
    });

  }

  public void buildTargetIds(Node node, List<String> targetResults, String relationType) {
    List<TargetRelation> targetRelations = node.getRelations().getTargets();
    targetRelations.forEach(targetRelation -> {
      String targetname = targetRelation.getCoRole();
      if (targetname != null && targetname.contains(relationType)) {
        targetResults.add(targetRelation.getTarget().getId());
      }
    });

  }

  public void buildSourceNames(Node node, List<String> sourceResults, String relationType) {
    List<SourceRelation> sourceRelations = node.getRelations().getSources();
    sourceRelations.forEach(sourceRelation -> {
      String sourceName = sourceRelation.getRole();
      if (sourceName.equals(relationType)) {
        sourceResults.add(sourceRelation.getSource().getName());
      }
    });

  }

  public List<String> getTargetNames(List<Node> nodes, String relationType) {
    List<String> targetResults = new ArrayList<>();
    nodes.forEach(
        nodeTargetelation -> buildTargetNames(nodeTargetelation, targetResults, relationType));

    return targetResults;
  }

  public List<String> getSourceNames(List<String> targetNames, String relationType) {
    List<String> sourceResults = new ArrayList<>();
    if (targetNames.isEmpty()) {
      return sourceResults;
    }
    String searchType = " FILTER node.name in [" + common.arrayToStringWithComma(targetNames) + "]";
    List<Node> nodeSourceRelations = nodeRepository.getNodes(searchType);

    nodeSourceRelations.forEach(
        nodeSourceRelation -> buildSourceNames(nodeSourceRelation, sourceResults, relationType));
    return sourceResults;
  }

  public List<String> buildMetaQualityScore(List<Node> nodes) {
    List<String> targetResults = new ArrayList<>();
    nodes.forEach(node -> {
      buildTargetIds(node, targetResults, "is part of");
    });

    return targetResults;
  }

  public List<String> getTargetNodes(List<Node> nodes, String type) {
    List<String> targetResults = new ArrayList<>();
    nodes.forEach(
        nodeTargetelation -> buildTargetNodes(nodeTargetelation, targetResults, type));

    return targetResults;
  }
}
