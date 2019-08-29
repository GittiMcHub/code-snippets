# Prerequesites
- Hadoop 
- ExaSol > 6.0
- Deployed ExaSol hadoop-etl-udfs (https://github.com/exasol/hadoop-etl-udfs/blob/master/doc/deployment-guide.md)
- Deployed ExaSol Connection for Kerberos Auth (https://github.com/exasol/hadoop-etl-udfs/blob/master/doc/kerberos.md)
## Hadoop Hive
Create Database 
```
CREATE DATABASE IF NOT EXISTS datalake_abc LOCATION '/datalake/abc';
```

Create external table
```
	CREATE EXTERNAL TABLE `datalake_abc.any_hive_table_data`(
	  `field1` string COMMENT '', 
	  `field2` string COMMENT '', 
	  `field3` int COMMENT '')
	ROW FORMAT SERDE 'parquet.hive.serde.ParquetHiveSerDe' 
	STORED AS INPUTFORMAT 'parquet.hive.DeprecatedParquetInputFormat' 
	OUTPUTFORMAT 'parquet.hive.DeprecatedParquetOutputFormat'
	LOCATION 'hdfs://nameservice1/datalake/abc/any_hive_table_data'
	;
```

Set Permissions
```
CREATE ROLE rconsumer_any_table_data;
GRANT ROLE rconsumer_any_table_data TO GROUP consumer_any_table_data;

GRANT ALL ON DATABASE datalake_abc TO ROLE rconsumer_any_table_data;
GRANT ALL ON URI 'hdfs://nameservice1/datalake/abc/any_hive_table_data' TO ROLE rconsumer_any_table_data;
```

## On ExaSol

1. Deploy HCAT_TABLE_FILES 

```
CREATE JAVA SCALAR SCRIPT ANY_SCHEMA.HCAT_TABLE_FILES (...) EMITS ("HDFS_SERVER_PORT" VARCHAR(200) UTF8, "HDFS_PATH" VARCHAR(200) UTF8, "HDFS_USER" VARCHAR(100) UTF8, "INPUT_FORMAT" VARCHAR(200) UTF8, "SERDE" VARCHAR(200) UTF8, "COLUMN_INFO" VARCHAR(100000) UTF8, "PARTITION_INFO" VARCHAR(10000) UTF8, "SERDE_PROPS" VARCHAR(10000) UTF8, "IMPORT_PARTITION" DECIMAL(18,0), "AUTH_TYPE" VARCHAR(1000) UTF8, "CONN_NAME" VARCHAR(1000) UTF8, "OUTPUT_COLUMNS" VARCHAR(100000) UTF8, "DEBUG_ADDRESS" VARCHAR(200) UTF8) AS
%scriptclass com.exasol.hadoop.scriptclasses.HCatTableFiles;
%jar /buckets/bfsdefault/default/exa-hadoop-etl-udfs-0.0.1-SNAPSHOT-all-dependencies.jar;
/
```

2. Create the Script that Emits the data, in this case, the raw data on hdfs is a JSON Sequence, so each line contains one field with the complete JSON.
NOTE: with this udf version 0.0.1 we had to explicitly define the output (EMITS "LINE").
Mabye it's different in Version 1.0.0 (https://github.com/exasol/hadoop-etl-udfs) that you can use one Script for all whith "Emits(...)"..

```
CREATE JAVA SET SCRIPT ANY_SCHEMA.IMPORT_HIVE_TABLE_FILES_WITH_ONE_COLUMN (...) EMITS ("LINE" VARCHAR(2000000) UTF8) AS
%env LD_LIBRARY_PATH=/tmp/;
%scriptclass com.exasol.hadoop.scriptclasses.ImportHiveTableFiles;
%jar /buckets/bfsdefault/default/exa-hadoop-etl-udfs-0.0.1-SNAPSHOT-all-dependencies.jar;
/
```

3. Grant Access to KRB Connection to Script and ROLE
```
GRANT ACCESS ON CONNECTION YOUR_KRB_CONNECTION FOR ANY_SCHEMA."HCAT_TABLE_FILES" TO YOUR_EXASOL_ROLE;
GRANT ACCESS ON CONNECTION YOUR_KRB_CONNECTION FOR ANY_SCHEMA."IMPORT_HIVE_TABLE_FILES_WITH_ONE_COLUMN" TO YOUR_EXASOL_ROLE;
```

4. Use the deployed Scripts to get the Data 

```
SELECT ANY_SCHEMA.IMPORT_HIVE_TABLE_FILES_WITH_ONE_COLUMN(
        HDFS_PATH
        ,INPUT_FORMAT
        ,SERDE
        ,COLUMN_INFO
        ,PARTITION_INFO
        ,SERDE_PROPS
        ,HDFS_SERVER_PORT
        ,HDFS_USER
        ,AUTH_TYPE
        ,CONN_NAME
        ,OUTPUT_COLUMNS
        ,DEBUG_ADDRESS
        )
    FROM (
        SELECT ANY_SCHEMA.HCAT_TABLE_FILES(
             '{InsertHiveDatabaseNameHere}'
            ,'{InsertHiveTableNameHere}'
            ,'{InsertThriftNameNodesHere e.g. thrift://hadoopNN01.any.domain:9083,thrift://hadoopNN02.any.domain:9083}'
            ,'{InsertHadoopPrincipalHere e.g. hdfs/_HOST@KRBPRINCIPAL.KRBSERVER.any.domain}'
            ,nproc()
            ,''
            ,''
            ,'{InsertHadoopNameNodesHere e.g. hdfs://hadoopNN01.any.domain:8020,hdfs://hadoopNN02.any.domain:8020}'
            ,'kerberos'
            ,'{InsertExaSolKerberosConnectionHere}'
        )
    ));
```