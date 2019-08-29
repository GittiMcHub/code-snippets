# Java UDF to make an HTTP Request to ElasticSearch

## Todos before deploy:
- Adjust parameters and datatypes for your query
- ElasticSearch URL settings
- Adjust Regex to extract ID out of Elastic Search HTTPResponse
- Adjust ElasticSearch json search query

## Three examples
- e.g.: SELECT ELASTIC_SEARCH_QUERY_SPECIFIC_FIELD_SAMPLE('MySearchFieldInES', 'AnySearchString') FROM DUAL;
- e.g.: SELECT ELASTIC_SEARCH_SINGLE_FIELD_QUERY_SAMPLE('AnySearchString') FROM DUAL;
- e.g.: SELECT ELASTIC_SEARCH_QUERY_SAMPLE('AnyCountry', 'AnyPostcode', 'AnyCity') FROM DUAL;