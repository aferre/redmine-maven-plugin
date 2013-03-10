package org.aferre.maven.redmine.plugin.versions;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.NotFoundException;
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
	 *            expression="${redmine.versionToBeCreated}"
	 */
	public String version;

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		if (version == null || version.isEmpty()) {
			if (getLog().isInfoEnabled()) {
				getLog().info("No version provided, aborting.");
			}

			if (abortOnError) {
				return;
			}
		}

		String v = Utils.getVersion(version);
		if (projectIds != null && projectIds.length != 0) {
			if (getLog().isInfoEnabled()) {
				getLog().info("Creating version for projects " + projectIds);
			}
			for (String projectName : projectIds) {
				try {
					createVersion(mgr, projectName, v);
				} catch (Exception e) {
					if (getLog().isErrorEnabled()) {
						getLog().error(e.getMessage());
					}
				}
			}

		} else if (projectId != null) {
			try {
				createVersion(mgr, projectId, v);
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

	private void createVersion(RedmineManager mgr, String projectName,
			String versionToCreate) throws RedmineException {

		if (getLog().isInfoEnabled()) {
			getLog().info(
					"Trying to create version " + versionToCreate
							+ " for project " + projectName);
		}

		try {
			Project project = mgr.getProjectByKey(projectName);
			if (getLog().isInfoEnabled()) {
				getLog().info("Retrieved project " + Utils.toString(project));
			}
			if (interactive) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Continue?(y/n)");
				}
				String readLine = System.console().readLine();
				while (readLine.isEmpty()) {
					if (getLog().isInfoEnabled()) {
						getLog().info("Continue?(y/n)");
					}
					readLine = System.console().readLine();
				}
				if (!readLine.equalsIgnoreCase("y")) {
					return;
				}
			}
			List<Version> versions = getVersions(mgr, project);
			Version known = null;
			for (Version version : versions) {
				if (version.getProject().getName().equals(project.getName())) {
					String name = version.getName();

					if (name.equals(versionToCreate)) {
						if (getLog().isInfoEnabled()) {
							getLog().info(
									"Version  " + name + " already exists:");
							getLog().info(Utils.toString(version));
						}
						known = version;
						break;
					}
				}
			}
			if (known == null) {
				if (getLog().isInfoEnabled()) {
					getLog().info("Version does not exist.");
				}
				Version v = new Version(project, versionToCreate);
				Version createVersion = mgr.createVersion(v);

				if (getLog().isInfoEnabled()) {
					getLog().info("Created version: ");
					getLog().info(Utils.toString(createVersion));
				}
			}
		} catch (NotFoundException e) {
			if (getLog().isErrorEnabled()) {
				getLog().error("Project " + projectName + " not found.");
			}
			throw e;
		}

	}
}
