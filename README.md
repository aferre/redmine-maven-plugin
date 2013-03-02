maven-redmine-plugin
====================

Maven plugin for redmine REST API, using https://github.com/taskadapter/redmine-java-api


Configuration:
              <hostUrl>http://redmine.saic.int</hostUrl>
							<projectId>g2-station</projectId>
							<apiKey>dac83154a75360d8fced5ce29f3941ec281cc17f</apiKey>
              
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
