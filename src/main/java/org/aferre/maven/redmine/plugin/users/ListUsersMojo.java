package org.aferre.maven.redmine.plugin.users;

import java.util.List;

import org.aferre.maven.redmine.plugin.core.AbstractRedmineMojo;
import org.aferre.maven.redmine.plugin.core.Utils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.User;

/**
 * 
 * @goal list-users
 * 
 * @phase install
 */
@Mojo(name = "list-users")
public class ListUsersMojo extends AbstractRedmineMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		RedmineManager mgr = new RedmineManager(hostUrl.toString(), apiKey);

		try {
			List<User> users = mgr.getUsers();
			for (User user : users) {
				Utils.printuser(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
