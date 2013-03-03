maven-redmine-plugin
====================

Maven plugin for redmine REST API, using https://github.com/taskadapter/redmine-java-api


Configuration:
<hostUrl> ... </hostUrl>"
<projectId>...</projectId>
<apiKey>...</apiKey>

Examples:

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
      
Prints projects:

Prints versions:

Create version:

Close version:

Remove version:

Create project:
