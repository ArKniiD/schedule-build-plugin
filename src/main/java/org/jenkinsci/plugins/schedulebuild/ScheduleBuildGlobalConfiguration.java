package org.jenkinsci.plugins.schedulebuild;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.Descriptor.FormException;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import net.sf.json.JSONObject;

@Extension
public class ScheduleBuildGlobalConfiguration extends GlobalConfiguration {
    private Date defaultScheduleTime;
    private String timeZone;

    public ScheduleBuildGlobalConfiguration() {
        this.defaultScheduleTime = new Date(0, 0, 0, 22, 0);
        this.timeZone = TimeZone.getDefault().getID();
        load();
    }

    @Override
    public GlobalConfigurationCategory getCategory() {
        return new GlobalConfigurationCategory.Unclassified();
    }

    public String getDefaultScheduleTime() {
        return getTimeFormat().format(this.defaultScheduleTime);
    }

    public String getTimeZone() {
        return timeZone;
    }

    public TimeZone getTimeZoneObject() {
        return TimeZone.getTimeZone(timeZone);
    }

    private DateFormat getTimeFormat() {
        return DateFormat.getTimeInstance(DateFormat.SHORT, Stapler.getCurrentRequest().getLocale());
    }

    public Date getDefaultScheduleTimeObject() {
        return this.defaultScheduleTime;
    }

    public FormValidation doCheckDefaultScheduleTime(@QueryParameter String value) {
        try {
            getTimeFormat().parse(value);
        } catch (ParseException ex) {
            return FormValidation.error(Messages.ScheduleBuildGlobalConfiguration_ParsingError());
        }
        return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        if (json.containsKey("defaultScheduleTime") && json.containsKey("timeZone")) {
            try {
                this.defaultScheduleTime = getTimeFormat().parse(json.getString("defaultScheduleTime"));
                this.timeZone = json.getString("timeZone");
                save();
                return true;
            } catch (ParseException ex) {
            }
        }
        return false;
    }
}
