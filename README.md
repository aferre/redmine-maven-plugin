maven-redmine-plugin
====================

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
Prints projects:

Prints versions:

Create version:

Close version:

Remove version:

Create project:
