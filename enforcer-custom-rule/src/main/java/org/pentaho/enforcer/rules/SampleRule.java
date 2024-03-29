package org.pentaho.enforcer.rules;

/**
 * Created by hfernandes on 13/12/2016.
 */

import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 */
public class SampleRule implements EnforcerRule {
    /**
     * Simple param. This rule will fail if the value is true.
     */
    private boolean shouldIfail = false;

    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        Log log = helper.getLog();

        try {
            // get the various expressions out of the helper.
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            MavenSession session = (MavenSession) helper.evaluate("${session}");
            String target = (String) helper.evaluate("${project.build.directory}");
            String artifactId = (String) helper.evaluate("${project.artifactId}");

            // retrieve any component out of the session directly
            ArtifactResolver resolver = (ArtifactResolver) helper.getComponent(ArtifactResolver.class);
            RuntimeInformation rti = (RuntimeInformation) helper.getComponent(RuntimeInformation.class);

            log.info("Retrieved Target Folder: " + target);
            log.info("Retrieved ArtifactId: " + artifactId);
            log.info("Retrieved Project: " + project);
            log.info("Retrieved RuntimeInfo: " + rti);
            log.info("Retrieved Session: " + session);
            log.info("Retrieved Resolver: " + resolver);

            if (this.shouldIfail) {
                throw new EnforcerRuleException("Failing because my param said so.");
            }
        } catch (ComponentLookupException e) {
            throw new EnforcerRuleException("Unable to lookup a component " + e.getLocalizedMessage(), e);
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or conditions
     * change that would cause the result to be different. Multiple cached results are stored
     * based on their id.
     * <p>
     * The easiest way to do this is to return a hash computed from the values of your parameters.
     * <p>
     * If your rule is not cacheable, then the result here is not important, you may return anything.
     */
    public String getCacheId() {
        //no hash on boolean...only parameter so no hash is needed.
        return "" + this.shouldIfail;
    }

    /**
     * This tells the system if the results are cacheable at all. Keep in mind that during
     * forked builds and other things, a given rule may be executed more than once for the same
     * project. This means that even things that change from project to project may still
     * be cacheable in certain instances.
     */
    public boolean isCacheable() {
        return false;
    }

    /**
     * If the rule is cacheable and the same id is found in the cache, the stored results
     * are passed to this method to allow double checking of the results. Most of the time
     * this can be done by generating unique ids, but sometimes the results of objects returned
     * by the helper need to be queried. You may for example, store certain objects in your rule
     * and then query them later.
     */
    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }
}
