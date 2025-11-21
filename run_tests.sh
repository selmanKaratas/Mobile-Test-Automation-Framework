#!/bin/bash

# Clean previous test results
mvn clean

# Run tests and generate Allure results
echo "Running tests and generating Allure report..."
mvn test

# Generate Allure report
echo "Generating Allure report..."
mvn allure:report

# Open the report in the default browser
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open "target/site/allure-maven-plugin/index.html"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    xdg-open "target/site/allure-maven-plugin/index.html"
else
    # Windows (Git Bash)
    start "" "target/site/allure-maven-plugin/index.html"
fi

echo "Test execution completed. Report should open in your default browser."
