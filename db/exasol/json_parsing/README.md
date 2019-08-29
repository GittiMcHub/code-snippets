# JSON Parsing in EXASOL // SOL-570

## Source & Author
EXASOL AG <br>
EXASOL Solution Center - SOL-570 <br>
"Querying and Converting JSON Data with the JSON_TABLE UDF" <br>
by Johannes Schildgen (as of information out of the Attachment - 08.10.2017)

All information and complete version can be found here:
https://www.exasol.com/support/browse/SOL-570


## Usage 
JSON data that is stored in EXASOL tables can be accessed through UDFs. This solution presents a generic Python UDF json_table to access field values in JSON documents through path expressions.

The json_table function has the following form:
```
select json_table(
 <json string or column>,
 <path expression>,
 <path expression>,
 ...
) emits (<column_name> <data_type>, <column_name> <data_type>, ...)
```

To understand what path expressions are, let us have a look at the following JSON document:
```
{ "name": "Bob", "age": 37, "address":{"street":"Example Street 5","city":"Berlin"}, "phone":[{"type":"home","number":"030555555"},{"type":"mobile","number":"017777777"}], "email":["bob@example.com","bobberlin@example.com"] }
```

The JSON document contains five fields: “name” (a string), “age” (an integer), “address” (a document), “phone” (an array containing documents) and “email” (an array containing strings).

Path expressions start with a dollar sign ($) representing the root document. The dot operator (.) is used to select a field of a document, the star in box brackets ([*]) selects and unnests all elements of an array. The following path expression finds all phone numbers:
```
$.phone[*].number
```

 This expression is evaluated as follows:
| path step | result |
|---|---|
| ```$``` | ```{ "name": "Bob", "age": 37, "address":{"street":"Example Street 5","city":"Berlin"},"phone":[{"type":"home","number":"030555555"},{"type":"mobile","number":"017777777"}],"email":["bob@example.com","bobberlin@example.com"] }``` |
| ``` $.phone ``` | ```[{"type":"home","number":"030555555"},{"type":"mobile","number":"017777777"}]``` |
| ``` $.phone[*] ``` | ``` {"type":"home","number":"030555555"} ``` <br> ``` {"type":"mobile","number":"017777777"} ``` |
| ```$.phone[*].number``` | ```"030555555"``` <br> ```"017777777"``` |
