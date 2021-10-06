package org.openmrs.module.ugandaemrreports.reports2019;


import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ugandaemrreports.data.converter.CalculationResultDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.NationalityPersonalAttributeDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.PersonAttributeDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.ObsDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.EmctCodesDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.ARVsDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.WHODataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.IYCFDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.FpcDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.TetanusDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.IptCtxDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.FreeLlinDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.MebendazoleDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.MUACDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.STKDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.STKResultDataConverter;
import org.openmrs.module.ugandaemrreports.data.converter.TFVDataConverter;
import org.openmrs.module.ugandaemrreports.library.BasePatientDataLibrary;
import org.openmrs.module.ugandaemrreports.library.HIVPatientDataLibrary;
import org.openmrs.module.ugandaemrreports.library.Cohorts;
import org.openmrs.module.ugandaemrreports.library.DataFactory;
import org.openmrs.module.ugandaemrreports.reporting.calculation.ANCEncounterDateCalculation;
import org.openmrs.module.ugandaemrreports.reporting.dataset.definition.SharedDataDefintion;
import org.openmrs.module.ugandaemrreports.reports.UgandaEMRDataExportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 */
@Component
public class SetupCHCRegister2019 extends UgandaEMRDataExportManager {

    @Autowired
    private DataFactory df;

    @Autowired
    SharedDataDefintion sdd;

    @Autowired
    BasePatientDataLibrary basePatientDataLibrary;
    
    @Autowired
    HIVPatientDataLibrary hivPatientData;


    /**
     * @return the uuid for the report design for exporting to Excel
     */
    @Override
    public String getExcelDesignUuid() {
        return "f728c941-2411-11ec-9e14-00ffcd58c42d";
    }

    public String getCSVDesignUuid()
    {
        return "04c7d585-2412-11ec-9e14-00ffcd58c42d";
    }


    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        List<ReportDesign> l = new ArrayList<ReportDesign>();
        l.add(buildReportDesign(reportDefinition));
        l.add(buildExcelDesign(reportDefinition));
        return l;
    }

    /**
     * Build the report design for the specified report, this allows a user to override the report design by adding properties and other metadata to the report design
     *
     * @param reportDefinition
     * @return The report design
     */

    @Override
    public ReportDesign buildReportDesign(ReportDefinition reportDefinition) {
        ReportDesign rd = createCSVDesign(getCSVDesignUuid(), reportDefinition);
        return rd;
    }
    public ReportDesign buildExcelDesign(ReportDefinition reportDefinition) {
        ReportDesign rd = createExcelTemplateDesign(getExcelDesignUuid(), reportDefinition, "CHCRegister2019.xls");
        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:11-14,dataset:CHC");
        props.put("sortWeight", "5000");
        rd.setProperties(props);
        return rd;
    }

    @Override
    public String getUuid() {
        return "8dd5f582-2412-11ec-9e14-00ffcd58c42d";
    }

    @Override
    public String getName() {
        return "Intergrated Child Health Card Register 2019";
    }

    @Override
    public String getDescription() {
        return "It contains Child Health Card Details captured upto 18 months";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        ReportDefinition rd = new ReportDefinition();

        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.addParameters(getParameters());
        rd.addDataSetDefinition("CHC", Mapped.mapStraightThrough(dataSetDefinition()));
        return rd;
    }

    @Override
    public String getVersion() {
        return "3.0.6";
    }

    @Override
    public List<Parameter> getParameters() {
        List<Parameter> l = new ArrayList<Parameter>();
        l.add(df.getStartDateParameter());
        l.add(df.getEndDateParameter());
        return l;
    }




    private DataSetDefinition dataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.setName("CHC");
        dsd.addParameters(getParameters());
        
        //start adding columns here       
        dsd.addColumn("Visit Date", sdd.definition("Visit Date",  sdd.getConcept("dce153a7-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Child Number", sdd.definition("Child Number",  sdd.getConcept("2da812db-02fa-451b-88b2-816829d1ab86")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("NIN", sdd.getNationalIDNumber(), "");        
        dsd.addColumn("Child Name", new PreferredNameDataDefinition(), (String) null);
        dsd.addColumn("Age", sdd.getAgeDataDefinition(10,200), "onDate=${endDate}", new CalculationResultDataConverter());
        dsd.addColumn("DOB", sdd.definition("DOB",  sdd.getConcept("dce1116f-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Village", basePatientDataLibrary.getVillage(),(String)null);
        dsd.addColumn("Parish", basePatientDataLibrary.getParish(),(String)null);
        dsd.addColumn("County", basePatientDataLibrary.getCounty(),(String)null);
        dsd.addColumn("District", basePatientDataLibrary.getDistrict(),(String)null);
        dsd.addColumn("Nationality", new PersonAttributeDataDefinition("Nationality", sdd.getPatientNationality()), "", new NationalityPersonalAttributeDataConverter());
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition("Phone Number", sdd.getPhoneNumber()), "", new PersonAttributeDataConverter());
        dsd.addColumn("Mothers Name", sdd.definition("Mothers Name",  sdd.getConcept("1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Birth Place", sdd.definition("Birth Place",  sdd.getConcept("15b646cc-90b9-4946-ba92-e901a498c639")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Low Birth Weight", sdd.definition("Low Birth Weight",  sdd.getConcept("4fcf0f3b-6e82-4ea9-a66a-8e7795903579")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Birth Defect", sdd.definition("Birth Defect",  sdd.getConcept("f10be4c0-2ee9-4597-b52a-7f18838fd820")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Weight", sdd.definition("Weight",  sdd.getConcept("94e4aeea-84d0-4207-aacb-ce38fe8e109c")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("BCG", sdd.definition("BCG",  sdd.getConcept("165012AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Polio 0", sdd.definition("Polio 0",  sdd.getConcept("165006AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Polio 1", sdd.definition("Polio 1",  sdd.getConcept("165007AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Polio 2", sdd.definition("Polio 2",  sdd.getConcept("165008AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Polio 3", sdd.definition("Polio 3",  sdd.getConcept("165009AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Rota 1", sdd.definition("Rota 1",  sdd.getConcept("165022AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Rota 2", sdd.definition("Rota 2",  sdd.getConcept("165023AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("PCV 1", sdd.definition("PCV 1",  sdd.getConcept("bf57b8aa-a014-4605-9196-0124c3633e53")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("PCV 2", sdd.definition("PCV 2",  sdd.getConcept("446d1986-af13-4354-86c4-00b2474b8e62")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("PCV 3", sdd.definition("PCV 3",  sdd.getConcept("7afa366e-fb2f-46e3-b58c-df4a1eb3a182")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DPT-HepB Hib 1", sdd.definition("DPT-HepB Hib 1",  sdd.getConcept("165014AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DPT-HepB Hib 2", sdd.definition("DPT-HepB Hib 2",  sdd.getConcept("165015AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DPT-HepB Hib 3", sdd.definition("DPT-HepB Hib 3",  sdd.getConcept("165016AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("IPV", sdd.definition("IPV",  sdd.getConcept("165011AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("MR1", sdd.definition("MR1",  sdd.getConcept("0cf39ac1-526d-459a-a120-aa5fda6110d5")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("MR2", sdd.definition("MR2",  sdd.getConcept("17079665-cf7b-46d8-9bf5-e8763f68ebaf")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Vitamin A Date", sdd.definition("Vitamin A Date",  sdd.getConcept("8aaf57d7-7967-43b6-9ae8-ef4bf0a15bd8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Deworming Date", sdd.definition("Deworming Date",  sdd.getConcept("165035AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Received LLIN", sdd.definition("Received LLIN",  sdd.getConcept("165025AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("IYCF", sdd.definition("IYCF",  sdd.getConcept("dc9a00a2-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("PMTCT Code", sdd.definition("PMTCT Code",  sdd.getConcept("d5b0394c-424f-41db-bc2f-37180dcdbe74")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Treatment Initiated", sdd.definition("Treatment Initiated",  sdd.getConcept("e77b5448-129f-4b1a-8464-c684fb7dbde8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Date Initiated", sdd.definition("Date Initiated",  sdd.getConcept("836a70ea-4fae-4a22-83e3-9e866f8c617c")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("6w_Reactive", sdd.definition("6w_Reactive",  sdd.getConcept("16091701-69b8-4bc7-82b3-b1726cf5a5df")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("9m_Reactive", sdd.definition("9m_Reactive",  sdd.getConcept("16091701-69b8-4bc7-82b3-b1726cf5a5df")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("18m_Reactive", sdd.definition("18m_Reactive",  sdd.getConcept("16091701-69b8-4bc7-82b3-b1726cf5a5df")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Next Appointment", sdd.definition("Next Appointment",  sdd.getConcept("21c5d9e3-c00f-442f-9cd7-1580d8c86fa0")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Child Information", sdd.definition("Child Information",  sdd.getConcept("7b49143d-f53f-4aee-b2e4-31cf49a634f9")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        
        return dsd;
    }

}
