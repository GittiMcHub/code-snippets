# Tipps & Tricks



# ExaSol Cheats (Undocumented functions)

DISCLAIMER: The following code snippets will maybe not work in future as these are unoffical and undocumented functionalities

## Current Session - Transaction Locks
To see the effect make sure you set autocommit off (@set autocommit off;) and select any table or do any other operation.
```
select * from "$EXA_OBJECT_LOCKS";
```
