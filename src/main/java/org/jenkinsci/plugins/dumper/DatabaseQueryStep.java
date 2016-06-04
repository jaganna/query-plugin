package org.jenkinsci.plugins.dumper;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link DatabaseQueryStep} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #sqlQuery})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class DatabaseQueryStep extends Builder implements SimpleBuildStep {

    private static final String PLUGIN_DISPLAY_NAME = "Database Query";

    private final String sqlQuery;
    private final String jdbcDriver;
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public DatabaseQueryStep(String sqlQuery, String jdbcDriver, String jdbcUrl, String jdbcUser, String jdbcPassword) {
        this.sqlQuery = sqlQuery;
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPassword = jdbcPassword;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        executeQueryViaJdbc(listener.getLogger());
    }

    private void executeQueryViaJdbc(PrintStream logger) {
        JdbcConnectionDetails jdbcConnectionDetails = new JdbcConnectionDetails(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
        logger.println("JDBC details: " + jdbcConnectionDetails.toString());

        new Database(jdbcConnectionDetails).queryFor(sqlQuery, logger);
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link DatabaseQueryStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/DatabaseQueryStep/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckSqlQuery(@QueryParameter("sqlQuery") String sqlQuery) {
            if (isEmpty(sqlQuery)) {
                return FormValidation.error("Please set a sqlQuery");
            }
            else return FormValidation.ok();
        }

        public FormValidation doTestJdbcConnection(@QueryParameter("sqlQuery") String sqlQuery,
                                                   @QueryParameter("jdbcDriver") String jdbcDriver,
                                                   @QueryParameter("jdbcUrl") String jdbcUrl,
                                                   @QueryParameter("jdbcUser") String jdbcUser,
                                                   @QueryParameter("jdbcPassword") String jdbcPassword)
                throws IOException, ServletException {

            JdbcConnectionDetails jdbcConnectionDetails = new JdbcConnectionDetails(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
            try {
                new Database(jdbcConnectionDetails);
                return FormValidation.ok("JDBC connection: OK");
            } catch (Exception e) {
                return FormValidation.error(e, "Please check JDBC connection");
            }
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return PLUGIN_DISPLAY_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

    }
}
