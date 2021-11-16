package org.openmrs.module.ugandaemrreports.definition.dataset.definition;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

public class HTSDatasetDefinition extends BaseDataSetDefinition {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @ConfigurationProperty
    private Date startDate;
    @ConfigurationProperty
    private Date endDate;

    public HTSDatasetDefinition() {
        super();
    }

    public HTSDatasetDefinition(String name, String description) {
        super(name, description);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
