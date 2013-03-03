package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

/**
 * 
 * @goal create-versions
 * 
 */
@Mojo(name = "create-versions")
public class CreateVersionsMojo extends AbstractRedmineVersionsMojo {
	/**
	 * @parameter default-value="${project.version}"
	 *            expression="${versionToBeCreated}"
	 */
	public String version;

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);
		
		if (projectIds != null && projectIds.length != 0) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for projects " + projectIds);
			}
			for (String projectName : projectIds) {
				try {
					createVersion(mgr, projectName);
				} catch (Exception e) {
					if (getLog().isErrorEnabled()) {
						getLog().error(e.getMessage());
					}
				}
			}

		} else if (projectId != null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieving versions for project " + projectId);
			}
			try {
				createVersion(mgr, projectId);
			} catch (Exception e) {
				if (getLog().isErrorEnabled()) {
					getLog().error(e.getMessage());
				}
			}

		} else {
			if (getLog().isErrorEnabled()) {
				getLog().error("No projectId(s) provided.");
			}
		}

	}

	private void createVersion(RedmineManager mgr, String projectName)
			throws RedmineException {
		Project project = mgr.getProjectByKey(projectName);
		List<Version> versions = getVersions(mgr, project);
		Version known = null;
		for (Version version : versions) {
			String name = version.getName();
			if (name.equals(Utils.getVersion(this.version))) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version  " + name + " already exists:");
					getLog().info(Utils.toString(version));
				}
				known = version;
				break;
			}
		}
		if (known == null) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Version does not exist.");
			}
			Version v = new Version(project, Utils.getVersion(version));
			Version createVersion = mgr.createVersion(v);

			if (getLog().isInfoEnabled()) {
				getLog().info("Created version: ");
				getLog().info(Utils.toString(createVersion));
			}
		}
	}

}
