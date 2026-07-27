#!/bin/sh
# Gradle wrapper script - downloads Gradle if not present
# This is a simplified version; use the full Gradle wrapper for production

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use JAVA_HOME if set
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check if java is available
if ! command -v "$JAVACMD" > /dev/null 2>&1 ; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    exit 1
fi

# Try to find gradle-wrapper.jar
APP_HOME=$( cd "${0%/*}" > /dev/null && pwd -P )
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$CLASSPATH" ]; then
    echo "Gradle wrapper JAR not found. Please run 'gradle wrapper' to generate it."
    echo "Alternatively, install Gradle from https://gradle.org/install/"
    exit 1
fi

exec "$JAVACMD" \
    $DEFAULT_JVM_OPTS \
    $JAVA_OPTS \
    $GRADLE_OPTS \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"