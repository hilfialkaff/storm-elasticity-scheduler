# !/bin/bash
#
# Copy library into the main Storm directory.

STORM_DIR="../storm/"

mvn clean
mvn package
cp ./target/storm_elasticity_scheduler-1.0.0-SNAPSHOT.jar $STORM_DIR/lib/
