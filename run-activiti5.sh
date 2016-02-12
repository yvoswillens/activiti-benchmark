#!/usr/bin/env bash
echo "Building Activiti 5 benchmark"
./build-activiti5.sh

echo "Running Activiti 5 benchmark"
cd target

mysql -u alfresco -palfresco -e "DROP SCHEMA benchmark"
mysql -u alfresco -palfresco -e "CREATE SCHEMA benchmark DEFAULT CHARACTER SET utf8 COLLATE utf8_bin"

java -Xmx8G -XX:MaxPermSize=256M -Xms512M -DjdbcUrl=jdbc:mysql://localhost:3306/benchmark?characterEncoding=UTF-8 -DjdbcUsername=alfresco -DjdbcPassword=alfresco -DjdbcDriver=com.mysql.jdbc.Driver -Dhistory=audit -Dconfig=spring -jar activiti-basic-benchmark.jar 2500 16 activiti5