# Tipps & Tricks

## Raw Object Sizes and Schema Limits
```
SELECT  root_name,OBJECT_NAME,OWNER,
        trunc(MEM_OBJECT_SIZE/power(1024,3),2) as MOS_in_GB,
        trunc(RAW_OBJECT_SIZE/power(1024,3),2) as ROS_in_GB,
        trunc(RAW_OBJECT_SIZE_LIMIT/power(1024,3),2) as ROW_LIMIT_in_GB,
        ROOT_TYPE
FROM SYS_VIEWS.EXA_ALL_OBJECT_SIZES
WHERE OBJECT_TYPE='SCHEMA' AND OBJECT_NAME IN ('>ANY_SCHEMA_NAME<')
and MEM_OBJECT_SIZE>0
order by MEM_OBJECT_SIZE desc;
```


# ExaSol Cheats (Undocumented functions)

DISCLAIMER: The following code snippets will maybe not work in future as these are unoffical and undocumented functionalities

## Current Session - Transaction Locks
To see the effect make sure you set autocommit off (@set autocommit off;) and select any table or do any other operation.
```
select * from "$EXA_OBJECT_LOCKS";
```

## Hidden Profile Table
```
select * from "$EXA_PROFILE_LAST_DAY"
```

=> "NODE SYNC" describes that at least one node is done and is wating for at least one other nodes

# Optimizing

## Multiple UNION ALL optimization 
1. Materialization causes long runtine
Optimazion: Change to JOINs 


## When a manual Distribution Key makes sense
Using a column as JOIN-Condition, in a Parition By and in a group by? Probably makes sense to set as Distribution Key (check existing first!)


## NL JOIN with filter (e.g. BETWEEN JOIN)
Exasol internaly joins everything (CROSS-JOIN) and filters last, when the joins doesn't have an equal condition.
```
...
JOIN date_dim ON date_field between valid_from between valid_until
```

Optimizing: 
Filter the DATE_DIMENSION to a minium first and then JOIN BETWEEN

Possible other solutions:
1. In Data Vault you can use Point in Time tables 
2. using an emitting UDF