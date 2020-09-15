#!/bin/bash

echo Cassandra Altering table USER_FEED started

echo IP : $1
echo Port : $2
echo Username : $3
echo Password : $4
echo ***Executing $5 started***
​
bin/cqlsh $1 $2 -u $3 -p $4 -f $5

echo Cassandra Altering table USER_FEED ended