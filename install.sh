# !/bin/bash
#
# Copy library into the main Storm directory.

STORM_DIR="/var/storm/storm-0.9.1/"

# mvn clean
#mvn -f pom_thrift.xml package
mvn -f pom.xml package
sudo cp ./target/storm_elasticity_scheduler-1.0.0-SNAPSHOT.jar $STORM_DIR/lib/
