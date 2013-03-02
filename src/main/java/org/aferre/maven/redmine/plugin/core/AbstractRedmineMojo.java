package org.aferre.maven.redmine.plugin.core;

import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;

/**
 * 
 */
public abstract class AbstractRedmineMojo extends AbstractMojo {

	/**
	 * @parameter expression="${list-issues.hostUrl}"
	 */
	protected URL hostUrl;
	/**
	 * @parameter expression="${list-issues.apiKey}"
	 */
	protected String apiKey;

}
