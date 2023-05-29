package com.datasouk.utils.arango;

import com.datasouk.core.dto.request.Filters;
import com.datasouk.service.arango.connect.ConnectArango;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QueryBuilder {

  private static Logger logger = LoggerFactory.getLogger(QueryBuilder.class);
  @Autowired
  private ConnectArango connectarango;

  public String build(List<Filters> filters) {

    if (filters == null) {
      return null;
    }
    HashMap<String, String> filtersMap = new HashMap<>();

    for (int i = 0; i < filters.size(); i++) {
      Filters filter = filters.get(i);
      JSONObject searchString = new JSONObject();
      if (filter.getFieldName().equals("doc.name")) {
        searchString.put("Name", filter.getFieldValue());
        connectarango.myRecentSearchHistory(searchString);
      }
      if (filter.getSearchType().equals("SEARCH")) {
        if (filtersMap.containsKey(filter.getSearchType())) {

          if (filter.getCondition().equals("or")) {
            StringBuilder result = new StringBuilder();
            result.append(" OR ");
            if (filter.getContextFunction() != null) {
              result.append(filter.getContextFunction());
              result.append("(");
            }
            result.append(filter.getStringFunction()).append("(");
            result.append(filter.getFieldName()).append(",");
            if (filter.getIsToken()) {
              result.append("TOKENS(");
            }
            result.append("'");
            result.append(filter.getFieldValue());
            result.append("'");
            if (filter.getIsToken()) {
              result.append(",");
              result.append("'" + filter.getAnalyzer() + "'))");
            }
            result.append(",");
            if (filter.getThreshold() != null) {
              result.append(filter.getThreshold()).append(",");
            }
            if (filter.getAnalyzer() != null) {
              result.append("'" + filter.getAnalyzer() + "')");
            } else {
              result.append("'fuzzy_search_bigram')");
            }

            filtersMap.put("SEARCH", filtersMap.get(filter.getSearchType()) + result);
          } else if (filter.getCondition().equals("and")) {
            StringBuilder result = new StringBuilder();
            /*result.append(" AND ").append(filter.getStringFunction()).append("(")
                .append(filter.getFieldName()).append(",").append("'")
                .append(filter.getFieldValue()).append("'").append(",")
                .append(filter.getThreshold()).append(",").append("'fuzzy_search_bigram')");*/
            result.append(" AND ");
            if (filter.getContextFunction() != null) {
              result.append(filter.getContextFunction());
              result.append("(");
            }
            result.append(filter.getStringFunction()).append("(");
            result.append(filter.getFieldName()).append(",");
            if (filter.getIsToken()) {
              result.append("TOKENS(");
            }
            result.append("'");
            result.append(filter.getFieldValue());
            result.append("'");
            if (filter.getIsToken()) {
              result.append(",");
              result.append("'" + filter.getAnalyzer() + "'))");
            }
            result.append(",");
            if (filter.getThreshold() != null) {
              result.append(filter.getThreshold()).append(",");
            }
            if (filter.getAnalyzer() != null) {
              result.append("'" + filter.getAnalyzer() + "')");
            } else {
              result.append("'fuzzy_search_bigram')");
            }
            filtersMap.put("SEARCH", filtersMap.get(filter.getSearchType()) + result);
          }
        } else {
          if (filter.getCondition() != null && filter.getCondition().equals("or")) {
            StringBuilder result = new StringBuilder();
            result.append(" OR ").append(filter.getStringFunction()).append("(")
                .append(filter.getFieldName()).append(",").append("'")
                .append(filter.getFieldValue()).append("'").append(",")
                .append(filter.getThreshold()).append(",").append("'fuzzy_search_bigram')");
            filtersMap.put("SEARCH", filtersMap.get(filter.getSearchType()) + result);
          } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
            StringBuilder result = new StringBuilder();
            result.append(" AND ").append(filter.getStringFunction()).append("(")
                .append(filter.getFieldName()).append(",").append("'")
                .append(filter.getFieldValue()).append("'").append(",")
                .append(filter.getThreshold()).append(",").append("'fuzzy_search_bigram')");
            filtersMap.put("SEARCH", filtersMap.get(filter.getSearchType()) + result);
          } else {
            StringBuilder result = new StringBuilder();
            result.append("SEARCH ANALYZER(").append(filter.getStringFunction()).append("(")
                .append(filter.getFieldName()).append(",").append("TOKENS('")
                .append(filter.getFieldValue()).append("'").append(",")
                .append("\"text_en\")), \"text_en\")");
            filtersMap.put("SEARCH", result.toString());

          }
        }
        //Todo Need to change later after working search issue without space
        String formattedString = filter.getFieldValue().replaceAll("\\s", "")
            .replaceAll("[^a-zA-Z0-9]", "");
        String pharseValue = "{\"LEVENSHTEIN_MATCH\": [\n"
            + "        \"" + formattedString + "\",\n"
            + "        2,\n"
            + "        true\n"
            + "      ]}";

        String searchQuery =
            " SEARCH NGRAM_MATCH(doc.name, '" + formattedString
                + "', 0.2, 'ngram_upper') \n"
                + "         OR\n"
                + "         BOOST(PHRASE(doc.name, " + pharseValue
                + ", 'en_tokenizer'), 10) ";

        filtersMap.put("SEARCH", searchQuery);

      } else if (filter.getSearchType().equals("FILTER")) {
        if (filtersMap.containsKey(filter.getSearchType())) {
          if (filter.getCondition().equals("or")) {
            StringBuilder result = new StringBuilder();
            StringBuilder operations = buildOperation(filter, result);
            filtersMap.put("FILTER", filtersMap.get(filter.getSearchType()) + operations);
          } else if (filter.getCondition().equals("and")) {
            StringBuilder result = new StringBuilder();
            StringBuilder operations = buildOperation(filter, result);
            filtersMap.put("FILTER", filtersMap.get(filter.getSearchType()) + operations);
          }
        } else {
          if (filter.getCondition() != null && filter.getCondition().equals("or")) {
            StringBuilder result = new StringBuilder();
            StringBuilder operations = buildOperation(filter, result);
            filtersMap.put("FILTER", operations.toString());
          } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
            StringBuilder result = new StringBuilder();
            StringBuilder operations = buildOperation(filter, result);
            filtersMap.put("FILTER", operations.toString());
          } else {
            StringBuilder result = new StringBuilder();
            result.append(" FILTER ");
            StringBuilder operations = buildOperation(filter, result);
            filtersMap.put("FILTER", operations.toString());

          }
        }
      } else if (filter.getSearchType().equals("PHRASE_FILTER_EXACT")) {
        /*if (filtersMap.containsKey("FILTER")) {
          StringBuilder result = new StringBuilder();
          StringBuilder operations = buildOperation(filter, result);
          filtersMap.put("FILTER", filtersMap.get("FILTER") + " AND " + operations);
        } else {
          StringBuilder result = new StringBuilder();
          result.append(" FILTER ");
          StringBuilder operations = buildOperation(filter, result);
          filtersMap.put("FILTER", operations.toString());
        }*/
        //filtersMap
        //SEARCH doc.title == TOKENS("thé mäTRïX", "norm_en")[0]
        /*List<String> combinations = generateOrderPermutation(filter.getFieldValue());
        List<String> generateValues = new ArrayList<>();
        for (String value : combinations) {
          generateValues.add(" TOKENS('" + value + "'" + "," + "\"norm_en\")[0] ");
        }

        StringBuilder result = new StringBuilder();
        result.append("SEARCH ANALYZER( ").append(filter.getFieldName()).append(" in ")
            .append("[")
            .append(String.join(",", generateValues))
            .append("]")
            .append(",")
            .append("\"norm_en\")");*/

        String convertToLower = filter.getFieldValue().toLowerCase().trim();
        String[] convertToArray = convertToLower.split(" ");

        List<String> phraseStructure = new ArrayList<>();

        /*for (int j = 0; j < convertToArray.length; j++) {
          String pharseValue = "{\"LEVENSHTEIN_MATCH\": [\n"
              + "        \"" + convertToArray[j] + "\",\n"
              + "        2,\n"
              + "        true\n"
              + "      ]}";

          phraseStructure.add(pharseValue);
        }*/
        String formattedString = convertToLower.replaceAll("\\s", "")
            .replaceAll("[^a-zA-Z0-9]", "");
        String pharseValue = "{\"LEVENSHTEIN_MATCH\": [\n"
            + "        \"" + formattedString + "\",\n"
            + "        2,\n"
            + "        true\n"
            + "      ]}";

        String phraseStructureStr =
            "LET phraseStructure =[" + pharseValue + "]";

        String searchQuery =
            " SEARCH NGRAM_MATCH(doc.properName, '" + formattedString
                + "', 0.8, 'ngram_upper') \n"
                + "         OR\n"
                + "         BOOST(PHRASE(doc.properName, " + pharseValue
                + ", 'en_tokenizer'), 10) ";
        filtersMap.put("PHRASE_FILTER_EXACT", searchQuery);
        //Identify this is pharse filter
        filtersMap.put("PHRASE", "PHRASE_FILTER_EXACT");

      } else if (filter.getSearchType().equals("PHRASE_FILTER_AND")) {

        //Todo need to check this
        //filtersMap = new HashMap<>();
        /*StringBuilder result = new StringBuilder();
        result.append("SEARCH ANALYZER(").append("TOKENS('")
            .append(filter.getFieldValue()).append("'").append(",")
            .append("\"text_en\")").append(" ALL == " + filter.getFieldName() + ", ")
            .append("\"text_en\")");*/

        List<String> combinations = generateOrderPermutation(filter.getFieldValue());
        List<String> permutations = generatePermutation(combinations);
        List<String> generateValues = new ArrayList<>();
        for (String value : permutations) {
          generateValues.add(" TOKENS('" + value + "'" + "," + "\"norm_en\")[0] ");
        }
        String convertToLower = filter.getFieldValue().toLowerCase().trim();
        String formattedString = convertToLower.replaceAll("\\s", "")
            .replaceAll("[^a-zA-Z0-9]", "");
        String pharseValue = "{\"LEVENSHTEIN_MATCH\": [\n"
            + "        \"" + formattedString + "\",\n"
            + "        2,\n"
            + "        true\n"
            + "      ]}";

        String searchQuery =
            " OR NGRAM_MATCH(doc.properName, '" + formattedString
                + "', 0.8, 'ngram_upper') \n"
                + "         OR\n"
                + "         BOOST(PHRASE(doc.properName, " + pharseValue
                + ", 'en_tokenizer'), 10) ";

        StringBuilder result = new StringBuilder();
        result.append("SEARCH ANALYZER( ").append(filter.getFieldName()).append(" in ")
            .append("[")
            .append(String.join(",", generateValues))
            .append("]")
            .append(",")
            .append("\"norm_en\")").append(searchQuery);
        filtersMap.put("PHRASE_FILTER_AND", result.toString());
        //Identify this is pharse filter
        filtersMap.put("PHRASE", "PHRASE_FILTER_AND");

        logger.info("filtersMap test : " + filtersMap);

        //Todo need to check after this
        /*String queryParams = "";
        queryParams = " " + filtersMap.get("PHRASE_FILTER_AND");

        logger.info("queryParams test : " + queryParams);*/

        //return queryParams;
        /*filtersMap = new HashMap<>();
        StringBuilder result = new StringBuilder();
        result.append("SEARCH ANALYZER(").append("TOKENS('")
            .append(filter.getFieldValue()).append("'").append(",")
            .append("\"text_en\")").append(" ALL == " + filter.getFieldName() + ", ")
            .append("\"text_en\")");*/
        filtersMap.put("PHRASE_FILTER_AND", result.toString());
        filtersMap.put("PHRASE", "PHRASE_FILTER_AND");


      } else if (filter.getSearchType().equals("PHRASE_FILTER_OR")) {
        /*String[] names = filter.getFieldValue().split("\\s+");
        String searchCombinations = combinationsToString(names);
        logger.info("searchCombinations for OR Case : " + searchCombinations);

        if (filtersMap.containsKey("FILTER")) {
          StringBuilder result = new StringBuilder();
          filtersMap.put("SEARCH",
              filtersMap.get("SEARCH") + " AND " + filter.getFieldName() + " IN ["
                  + searchCombinations + "]");

        } else {
          StringBuilder result = new StringBuilder();
          result.append(" SEARCH ");
          filtersMap.put("SEARCH",
              filtersMap.get("SEARCH") + " AND " + filter.getFieldName() + " IN ["
                  + searchCombinations + "]");
        }*/
        String convertToLower = filter.getFieldValue().toLowerCase().trim();
        String formattedString = convertToLower.replaceAll("\\s", "")
            .replaceAll("[^a-zA-Z0-9]", "");
        String pharseValue = "{\"LEVENSHTEIN_MATCH\": [\n"
            + "        \"" + formattedString + "\",\n"
            + "        2,\n"
            + "        true\n"
            + "      ]}";

        String searchQuery =
            " SEARCH NGRAM_MATCH(doc.name, '" + formattedString
                + "', 0.3, 'ngram_upper') \n"
                + "         OR\n"
                + "         BOOST(PHRASE(doc.name, " + pharseValue
                + ", 'en_tokenizer'), 10) ";

        filtersMap.put("PHRASE_FILTER_OR", searchQuery);
        //Identify this is pharse filter
        filtersMap.put("PHRASE", "PHRASE_FILTER_OR");

      } else if (filter.getSearchType().equals("FILTER_ML")) {
        if (filtersMap.containsKey("FILTER")) {
          StringBuilder result = new StringBuilder();
          StringBuilder operations = buildOperation(filter, result);
          if (filtersMap.get("FILTER_ML") == null) {
            operations.replace(0, 3, "");
            filtersMap.put("FILTER_ML", "" + operations);
          } else {
            filtersMap.put("FILTER_ML", filtersMap.get("FILTER_ML") + " " + operations);
          }
        } else {
          StringBuilder result = new StringBuilder();
          result.append(" FILTER ");
          StringBuilder operations = buildOperation(filter, result);
          filtersMap.put("FILTER_ML", operations.toString());
        }

        //Todo Vani added this filter need to verify this
        /*String[] names = filter.getFieldValue().split("\\s+");
        List<String> combinations = getWordPermutations(Arrays.asList(names));
        String stringCombinations = combinationsToString(combinations.toArray(new String[0]));
        logger.info("combinations data for AND Case : " + stringCombinations);

        if (filtersMap.containsKey("FILTER")) {
          StringBuilder result = new StringBuilder();
          filtersMap.put("SEARCH",
              filtersMap.get("SEARCH") + " AND " + filter.getFieldName() + " IN ["
                  + stringCombinations + "]");

        } else {
          StringBuilder result = new StringBuilder();
          result.append(" SEARCH ");
          filtersMap.put("SEARCH",
              filtersMap.get("SEARCH") + " AND " + filter.getFieldName() + " IN ["
                  + stringCombinations + "]");
        }*/
      } else if (filter.getSearchType().equals("SUB_QUERY_ATTRIBUTE_FILTER")) {

        List<String> fieldList = Arrays.asList(filter.getFieldValue().split(","));
        String subQueryVariable = getSaltString();

        if (filtersMap.containsKey("SUB_QUERY_ATTRIBUTE_FILTER")) {
          String subQuery = attributeSubQuery(subQueryVariable, fieldList.get(0), fieldList.get(1));
          filtersMap.put("SUB_QUERY_ATTRIBUTE_FILTER",
              filtersMap.get("SUB_QUERY_ATTRIBUTE_FILTER") + subQuery);
          if (filter.getCondition() != null && filter.getCondition().equals("or")) {
            filtersMap.put("FILTER",
                filtersMap.get("FILTER") + " OR LENGTH(" + subQueryVariable + ") > 0");
          } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
            filtersMap.put("FILTER",
                filtersMap.get("FILTER") + " AND LENGTH(" + subQueryVariable + ") > 0");
          }
        } else {

          if (filtersMap.containsKey("FILTER")) {
            if (filter.getCondition() != null && filter.getCondition().equals("or")) {
              filtersMap.put("FILTER",
                  filtersMap.get("FILTER") + " OR LENGTH(" + subQueryVariable + ") > 0");
            } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
              filtersMap.put("FILTER",
                  filtersMap.get("FILTER") + " AND LENGTH(" + subQueryVariable + ") > 0");
            }
          } else {
            filtersMap.put("FILTER", "FILTER LENGTH(" + subQueryVariable + ") > 0");
          }
          String subQuery = attributeSubQuery(subQueryVariable, fieldList.get(0), fieldList.get(1));
          filtersMap.put("SUB_QUERY_ATTRIBUTE_FILTER", subQuery);
        }
      } else if (filter.getSearchType().equals("SUB_QUERY_RELATION_FILTER")) {
        String subQuery = "";
        if (filter.getFieldName().equals("dataDomain")) {
          subQuery = dataDomainSubQuery(filter.getFieldValue());
        } else if (filter.getFieldName().equals("lineOfBusiness")) {
          subQuery = lobSubQuery(filter.getFieldValue());
        }

        if (filtersMap.containsKey("SUB_QUERY_RELATION_FILTER")) {

          filtersMap.put("SUB_QUERY_RELATION_FILTER",
              filtersMap.get("SUB_QUERY_RELATION_FILTER") + subQuery);
          if (filter.getCondition() != null && filter.getCondition().equals("or")) {
            filtersMap.put("FILTER",
                filtersMap.get("FILTER") + " OR LENGTH(" + filter.getFieldName() + ") > 0");
          } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
            filtersMap.put("FILTER",
                filtersMap.get("FILTER") + " AND LENGTH(" + filter.getFieldName() + ") > 0");
          }
        } else {

          if (filtersMap.containsKey("FILTER")) {
            if (filter.getCondition() != null && filter.getCondition().equals("or")) {
              filtersMap.put("FILTER",
                  filtersMap.get("FILTER") + " OR LENGTH(" + filter.getFieldName() + ") > 0");
            } else if (filter.getCondition() != null && filter.getCondition().equals("and")) {
              filtersMap.put("FILTER",
                  filtersMap.get("FILTER") + " AND LENGTH(" + filter.getFieldName() + ") > 0");
            }
          } else {
            filtersMap.put("FILTER", "FILTER LENGTH(" + filter.getFieldName() + ") > 0");
          }
          filtersMap.put("SUB_QUERY_RELATION_FILTER", subQuery);
        }
      } else {

      }

    }
    String filterValues = "";
    if (filtersMap.containsKey("SEARCH")
        && !filtersMap.containsKey("PHRASE")) {

      filterValues = " " + filtersMap.get("SEARCH");
    }
    if (filtersMap.containsKey("PHRASE_FILTER_AND")) {
      filterValues = " " + filtersMap.get("PHRASE_FILTER_AND");
    }
    if (filtersMap.containsKey("PHRASE_FILTER_EXACT")) {
      filterValues = " " + filtersMap.get("PHRASE_FILTER_EXACT");
    }
    if (filtersMap.containsKey("PHRASE_FILTER_OR")) {
      filterValues = " " + filtersMap.get("PHRASE_FILTER_OR");
    }

    if (filtersMap.containsKey("SUB_QUERY_ATTRIBUTE_FILTER")) {
      filterValues += " " + filtersMap.get("SUB_QUERY_ATTRIBUTE_FILTER");
    }
    if (filtersMap.containsKey("SUB_QUERY_RELATION_FILTER")) {
      filterValues += " " + filtersMap.get("SUB_QUERY_RELATION_FILTER");
    }
    if (filtersMap.containsKey("FILTER")) {
      filterValues += " " + filtersMap.get("FILTER");
    }
    if (filtersMap.containsKey("FILTER_ML")) {
      filterValues += " " + filtersMap.get("FILTER_ML") + "";
    }
    return filterValues;
  }

  private StringBuilder buildOperation(Filters filter, StringBuilder result) {
    switch (filter.getOp()) {
      case "eq":
        if (filter.getCondition() != null) {
          result.append(" " + filter.getCondition() + " ");
        }
        if (filter.getTypeConverter() != null) {
          result.append(filter.getTypeConverter() + "(" + filter.getFieldName() + ")").append("==");

        } else {
          result.append(filter.getFieldName()).append("==");

        }
        result.append("\"");
        result.append(filter.getFieldValue());
        result.append("\"");
        break;
      case "in":
        if (filter.getCondition() != null) {
          result.append(" " + filter.getCondition() + " ");
        }
        result.append(filter.getFieldName());
        result.append(" in ");
        if (filter.getTypeConverter() != null) {
          result.append(filter.getTypeConverter() + "(");
          result.append(filter.getFieldName() + ")");
        } else {
          String[] tokens = filter.getFieldValue().split(",");
          result.append("[");
          for (int i = 0; i <= tokens.length - 1; i++) {
            if (i == tokens.length - 1) {
              result.append("\"" + tokens[i] + "\" ");
            } else {
              result.append(" \"" + tokens[i] + "\", ");
            }
          }
          result.append("]");
        }
        break;
      case "like":
        if (filter.getCondition() != null) {
          result.append(" " + filter.getCondition() + " ");
        }
        result.append(" LIKE( ");
        result.append(filter.getFieldName() + ",");
        result.append("\"");
        result.append(filter.getFieldValue());
        result.append("\", ");
        result.append("true)");
        break;
      case "not like":
        if (filter.getCondition() != null) {
          result.append(" " + filter.getCondition() + " ");
        }
        result.append(" NOT LIKE( ");
        result.append(filter.getFieldName() + ",");
        result.append("\"");
        result.append(filter.getFieldValue());
        result.append("\", ");
        result.append("true)");
        break;
      default:
        break;
    }
    return result;
  }

  private List<String> getWordPermutations(List<String> words) {

    List<String> result = new ArrayList<>(words);
    List<String> oldPermutations = new ArrayList<>(words);

    for (int i = 1; i < words.size(); i++) {
      List<String> newPermutations = new ArrayList<>();
      for (String previousList : oldPermutations) {
        for (String word : words) {
          if (previousList.contains(word)) {
            continue;
          }
          newPermutations.add(previousList + " " + word);
        }
      }
      oldPermutations = newPermutations;
      result.addAll(newPermutations);
      result.removeAll(words);
    }

    return result;
  }

  private String combinationsToString(String[] combinations) {
    String combinationToString = "";

    for (int i = 0; i < combinations.length; i++) {
      combinationToString += "\"" + combinations[i] + "\"";
      if (i != combinations.length - 1) {
        combinationToString += ", ";
      }
    }
    return combinationToString;
  }

  private String attributeSubQuery(String variableName, String name, String value) {

    String subQuery = "LET " + variableName + " = ( \r\n" +
        " FOR attribute IN doc.attributes \r\n" +
        " FILTER attribute.name == '" + name + "' and attribute.value == '" + value + "' \r\n" +
        " RETURN attribute \r\n" +
        ") \r\n";
    return subQuery;
  }

  protected String getSaltString() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 8) { // length of the random string.
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return saltStr;

  }

  private List<String> generateOrderPermutation(String fieldValue) {
    if (fieldValue == null || fieldValue == "") {
      return null;
    }
    String convertToLower = fieldValue.toLowerCase().trim();
    String[] convertToArray = convertToLower.split(" ");
    List<String> combinations = new ArrayList<>();
    int arraySize = convertToArray.length;
    combinations.add(convertToLower);
    String removedValues = "";
    for (int i = 0; i < arraySize; i++) {
      removedValues += convertToArray[0];
      convertToArray = ArrayUtils.remove(convertToArray, 0);
      combinations.add(removedValues + " " + String.join("", convertToArray));
    }
    return combinations;
  }

  public List<String> generatePermutation(List<String> combinations) {
    List<String> allPermutations = new ArrayList<>();

    for (String value : combinations) {
      List<String> convertValue = List.of(value.split(" "));
      Collection<List<String>> permutations = Collections2.permutations(
          Lists.newArrayList(convertValue));
      for (List<String> permutation : permutations) {
        String permutationString = Joiner.on(" ").join(permutation);
        allPermutations.add(permutationString);
      }
    }
    return allPermutations;
  }

  private String dataDomainSubQuery(String value) {
    String subQuery = "LET dataDomain =(\n"
        + "        FOR x IN doc.relations.targets\n"
        + "            FILTER x.coRole == \"contains\" \n"
        + "            LET represents = (\n"
        + "                FOR y IN nodesView\n"
        + "                FILTER x.target.id == y.id and \"represents\" in y.relations.sources[*].role\n"
        + "                return y.relations.sources\n"
        + "            )\n"
        + "            LET classifyData = ( \n"
        + "            FOR source IN represents[0] \n"
        + "                LET classifies = ( \n"
        + "                    FOR z IN nodesView \n"
        + "                        FILTER  source.source.id == z.id and 'classifies' in z.relations.sources[*].role \n"
        + "                        and '" + value + "' in z.relations.sources[*].source.name \n"
        + "                    return z \n"
        + "                    ) \n"
        + "            return classifies \n"
        + "            )       \n"
        + "            \n"
        + "            \n"
        + "            RETURN classifyData\n"
        + "    )\r\n";
    return subQuery;
  }

  private String lobSubQuery(String value) {
    String subQuery = "LET lineOfBusiness =(\n"
        + "        FOR x IN doc.relations.targets\n"
        + "            FILTER x.coRole == \"contains\" \n"
        + "            LET represents = (\n"
        + "                FOR y IN nodesView\n"
        + "                FILTER x.target.id == y.id and \"represents\" in y.relations.sources[*].role\n"
        + "                return y.relations.sources\n"
        + "            )\n"
        + "            LET classifyData = ( \n"
        + "            FOR source IN represents[0] \n"
        + "                LET classifies = ( \n"
        + "                    FOR z IN nodesView \n"
        + "                        FILTER  source.source.id == z.id and 'associates' in z.relations.sources[*].role \n"
        + "                        and '" + value + "' in z.relations.sources[*].source.name \n"
        + "                    return z \n"
        + "                    ) \n"
        + "            return classifies \n"
        + "            )       \n"
        + "            \n"
        + "            \n"
        + "            RETURN classifyData\n"
        + "    )\r\n";
    return subQuery;
  }
}
