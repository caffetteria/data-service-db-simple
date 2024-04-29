# data-service-db-simple

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](CHANGELOG.md)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.caffetteria/data-service-db-simple.svg)](https://central.sonatype.com/artifact/io.github.caffetteria/data-service-db-simple)
[![license](https://img.shields.io/badge/License-MIT%20License-teal.svg)](https://opensource.org/license/mit)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=caffetteria_data-service-db-simple&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=caffetteria_data-service-db-simple)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=caffetteria_data-service-db-simple&metric=coverage)](https://sonarcloud.io/summary/new_code?id=caffetteria_data-service-db-simple)

[![Java runtime version](https://img.shields.io/badge/run%20on-java%208+-%23113366.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Java build version](https://img.shields.io/badge/build%20on-java%2011+-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-3.9.0+-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://universe.fugerit.org/src/docs/versions/maven3_9.html)

Semplice implementazione di un 
[Data Service Client](https://github.com/fugerit-org/fj-service-helper-bom/tree/main/data-service-base)
che si basa su una tabella di un RDBMS

## Quickstart

1. Aggiungere dipendenza : 

```xml
<dependency>
    <groupId>io.github.caffetteria</groupId>
    <artifactId>data-service-db-simple</artifactId>
    <version>${data-service-db-simple-version}</version>
</dependency>
```

2. Create database schema

```xml
CREATE TABLE data_service_db_simple (
id BIGINT,
content BLOB,
primary key (ID)
);

CREATE SEQUENCE  data_service_db_simple_seq START WITH 1 INCREMENT BY 1;
```

3. Configuration reference


| parametero    | obbligatorio | default                    | note                                                                                             |
|---------------|--------------|----------------------------|--------------------------------------------------------------------------------------------------|
| db_mode       | false        |                            | Possible values are : postgres, mysql, oracle, if not set will try to get from database metadata |
| table_id      | true         | data_service_db_simple     | Table id, with schema, catalog and so (ex. *my_catalog.my_schema.my_table*)                      |
| field_id      | true         | id                         | Field to be used as *id* in the chosen table. Should be BIGINT                                   |
| field_content | true         | content                    | Field to be used as *content* in the chosen table. Should be BLOB                                |
| sequence_id   | true         | data_service_db_simple_seq | Name of the sequence to be used for *id* generation.                                             |


Sample configuration : 

```
db_mode=oracle
table_id=data_service_db_simple
field_id=id
field_content=content
```

4. Setup data service

```java
ConnectionFactory cf = ...
Properties dsProps = ...
ConfigParams config = new ConfigParamsDefault( dsProps );
DbSimpleDataServiceFacade facade = new DbSimpleDataServiceFacade( cf, config );
DataService dataService = DbSimpleDataService.newDataService( facade );
String testData = "testData";
try (InputStream input = new ByteArrayInputStream(testData.getBytes())) {
    String id = dataService.save( input );
    String readData = StreamIO.readString( dataService.load( id ) );
}
```

