maven-redmine-plugin
====================

[![Build Status](https://travis-ci.org/aferre/redmine-maven-plugin.png?branch=master)](https://travis-ci.org/aferre/redmine-maven-plugin)

Maven plugin for redmine REST API, using https://github.com/taskadapter/redmine-java-api


Necessary configuration for all plugins:
```
<redmine.hostUrl>...</redmine.hostUrl>
<redmine.apiKey>...</redmine.apiKey>
```

It's a good thing to have the following property defined for a project tied to redmine (redmine project identifier).
```
<redmine.projectId>...</redmine.projectId>
```

Personnaly I define them as properties in a super pom with a bunch of other different properties to make it handier.

Examples:
```
<plugin>
  			<groupId>org.aferre</groupId>
				<artifactId>maven-redmine-plugin</artifactId>
				<version>0.0.1-SNAPSHOT</version>
				<executions>
					<execution>
						<configuration>
							<hostUrl>...</hostUrl>
							<projectId>...</projectId>
							<apiKey>...</apiKey>
						</configuration>
						<id>install1</id>
						<phase>install</phase>
						<goals>
							<goal>list-issues</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```

*	List all projects:

```
mvn redmine:list-projects -Dredmine.apiKey=API_KEY -Dredmine.hostUrl=REDMINE_URL -Dredmine.all=true
```

*	Prints versions:


*	Create version:


*	Close version:


*	Remove version:


*	Create project:


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/aferre/redmine-maven-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

