#!/bin/sh

echo "\n================= STARTING BUILD EACH MODULE ================";
echo "...";
echo "...";
echo "...";



echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Base-service";
echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
cd base-service/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Eureka-server";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../eureka-server/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Auth-service-share";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../auth-service/auth-service-share/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Api-gateway";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../api-gateway/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Product-service-share";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../product-service/product-service-share/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Order-service-share";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../order-service/order-service-share/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Notification-service-share";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../notification-service/notification-service-share/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Auth-service-core";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../auth-service/auth-service-core/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Product-service-core";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../product-service/product-service-core/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Order-service-core";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../order-service/order-service-core/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Notification-service-core";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../notification-service/notification-service-core/
mvn clean install

echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... Building the Mail-service";
echo"";
echo"";
echo"";
echo"";
echo"";
echo""
cd ../../mail-service/
mvn clean install
echo"";
echo"";
echo"";
echo"";
echo"";
echo"";
echo "..................... DONE!";
