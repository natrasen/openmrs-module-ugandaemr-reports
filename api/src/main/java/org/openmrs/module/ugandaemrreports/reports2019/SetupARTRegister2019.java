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
public class SetupARTRegister2019 extends UgandaEMRDataExportManager {

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
        return "750f5a31-9338-4e99-bfda-e8418862991d";
    }

    public String getCSVDesignUuid()
    {
        return "c7fd16df-9d5e-46bc-918a-550d5c4404b6";
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
        ReportDesign rd = createExcelTemplateDesign(getExcelDesignUuid(), reportDefinition, "ARTRegister2019.xls");
        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:11-14,dataset:ART");
        props.put("sortWeight", "5000");
        rd.setProperties(props);
        return rd;
    }

    @Override
    public String getUuid() {
        return "196fe601-e0e2-4f20-8f67-267b0c6ae1d0";
    }

    @Override
    public String getName() {
        return "Intergrated ART Register 2019";
    }

    @Override
    public String getDescription() {
        return "It contains ART information";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        ReportDefinition rd = new ReportDefinition();

        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.addParameters(getParameters());
        rd.addDataSetDefinition("ART", Mapped.mapStraightThrough(dataSetDefinition()));
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
        dsd.setName("ART");
        dsd.addParameters(getParameters());
        
        //start adding columns here
        dsd.addColumn("ARTStartDate",hivPatientData.getArtStartDate(),(String)null);
        dsd.addColumn("Clinic number", hivPatientData.getClinicNumber(), (String) null);
        dsd.addColumn("NIN", sdd.getNationalIDNumber(), (String)null);
        dsd.addColumn("returnVisitDate",hivPatientData.getLastReturnDateByEndDate(),(String) null);
        dsd.addColumn("Name of Client", new PreferredNameDataDefinition(), (String) null);
        dsd.addColumn("Village", basePatientDataLibrary.getVillage(),(String)null);
        dsd.addColumn("Parish", basePatientDataLibrary.getParish(),(String)null);
        dsd.addColumn("County", basePatientDataLibrary.getCounty(),(String)null);
        dsd.addColumn("District", basePatientDataLibrary.getDistrict(),(String)null);
        dsd.addColumn("Nationality", new PersonAttributeDataDefinition("Nationality", sdd.getPatientNationality()), "", new NationalityPersonalAttributeDataConverter());
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition("Phone Number", sdd.getPhoneNumber()), "", new PersonAttributeDataConverter());
        dsd.addColumn("Age", sdd.getAgeDataDefinition(10,200), "onDate=${endDate}", new CalculationResultDataConverter());
        dsd.addColumn("Age ART", hivPatientData.getAgeDuringPeriod(),(String)null);
        dsd.addColumn("ART start Date", sdd.definition("ART Start Date",  sdd.getConcept("ab505422-26d9-41f1-a079-c3d222000440")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Adv Disease Status", sdd.definition("Adv Disease Status",  sdd.getConcept("17def5f6-d6b4-444b-99ed-40eb05d2c4f8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("WHO Clinical Stage", sdd.definition("WHO Clinical Stage",  sdd.getConcept("dcd034ed-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Weight", sdd.definition("Weight",  sdd.getConcept("5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Height", sdd.definition("Height",  sdd.getConcept("5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("MUAC", sdd.definition("MUAC",  sdd.getConcept("5f86d19d-9546-4466-89c0-6f80c101191b")), "onOrAfter=${startDate},onOrBefore=${endDate}", new MUACDataConverter());
        dsd.addColumn("CD4", sdd.getWHOCD4ViralLoadCalculation("dcbcba2c-30ab-102d-86b0-7a5022ba4115", "159376AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), "onDate=${endDate}", new CalculationResultDataConverter());
        dsd.addColumn("ViralLoad", sdd.definition("ViralLoad",  sdd.getConcept("dc8d83e3-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("VL Date", hivPatientData.getLastViralLoadDateByEndDate(),(String)null);
        dsd.addColumn("CrAg", sdd.definition("CrAg",  sdd.getConcept("43c33e93-90ff-406b-b7b2-9c655b2a561a")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TB LAM", sdd.definition("TB LAM",  sdd.getConcept("066b84a0-e18f-4cdd-a0d7-189454f4c7a4")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Hep B Status", sdd.definition("Hep B Status",  sdd.getConcept("dca16e53-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Syphilis Status", sdd.definition("Syphilis Status",  sdd.getConcept("275a6f72-b8a4-4038-977a-727552f69cb8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("CPT Dapsone", sdd.definition("CPT Dapsone", sdd.getConcept("c3d744f6-00ef-4774-b9a7-d33c58f5b014")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("CPT Start", sdd.definition("CPT Start",  sdd.getConcept("1190AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("CPT Stop", sdd.definition("CPT Stop", sdd.getConcept("1191AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TPT B6 plus Status", sdd.definition("TPT B6 plus Status", sdd.getConcept("37d4ac43-b3b4-4445-b63b-e3acf47c8910")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Fluconazole", sdd.definition("Fluconazole", sdd.getConcept("25a839f2-ab34-4a22-aa4d-558cdbcedc43")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TB Status", sdd.definition("TB Status", sdd.getConcept("dce02aa1-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("District TB Reg", sdd.definition("District TB Reg", sdd.getConcept("67e9ec2f-4c72-408b-8122-3706909d77ec")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TB Start Date", sdd.definition("TB Start Date", sdd.getConcept("dce02eca-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TB Stop Date", sdd.definition("TB Stop Date", sdd.getConcept("dd2adde2-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("EDD", sdd.definition("EDD", sdd.getConcept("5b110c7d-8031-4526-9724-f262d6e2733e")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("ANC Number", sdd.definition("ANC Number", sdd.getConcept("c7231d96-34d8-4bf7-a509-c810f75e3329")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("EID", sdd.definition("EID", sdd.getConcept("2c5b695d-4bf3-452f-8a7c-fe3ee3432ffe")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Art Adherence", sdd.definition("Art Adherence", sdd.getConcept("dce03b2f-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Adherence Miss Reason", sdd.definition("Adherence Miss Reason", sdd.getConcept("dce045a4-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("First Line Regimen", sdd.definition("First Line Regimen", sdd.getConcept("dd2b0b4d-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Second Line Regimen", sdd.definition("Second Line Regimen", sdd.getConcept("dd277db4-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Third Line Regimen", sdd.definition("Third Line Regimen", sdd.getConcept("c2742c79-8c24-4fd6-9750-ecfdc7501080")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Current ARV Days", sdd.definition("Current ARV Days", sdd.getConcept("171de3f4-a500-46f6-8098-8097561dfffb")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DSDM", sdd.definition("DSDM", sdd.getConcept("73312fee-c321-11e8-a355-529269fb1459")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("GBV Risk", sdd.definition("GBV Risk", sdd.getConcept("6b433917-16af-498d-8fda-0e7919f59c5b")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("CaCx Status", sdd.definition("CaCx Status", sdd.getConcept("5029d903-51ba-4c44-8745-e97f320739b6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Blood Group", sdd.definition("Blood Group", sdd.getConcept("dc747e86-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Hb", sdd.definition("Hb", sdd.getConcept("dc548e89-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());        
        dsd.addColumn("Rh", sdd.definition("Rh", sdd.getConcept("1429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Sickle Cells", sdd.definition("Sickle Cells", sdd.getConcept("907e11ae-5d80-4ee5-8167-42417b8bc4c9")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("CaCx Date", sdd.definition("CaCx date", sdd.getConcept("6c5f527d-c254-4414-83f2-8e119c0607df")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("EMTCT codesP", sdd.definition("EMTCT codesP", sdd.getConcept("62a37075-fc2a-4729-8950-b9fae9b22cfb")), "onOrAfter=${startDate},onOrBefore=${endDate}", new EmctCodesDataConverter());
        dsd.addColumn("WHO", sdd.definition("WHO", sdd.getConcept("dcdff274-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new WHODataConverter());
        dsd.addColumn("Pre-ART No", sdd.getPreARTNumber(), "");        
        dsd.addColumn("Syphilis testW", sdd.definition("Syphilis testW", sdd.getConcept("3c5aa2a6-ca7a-478c-b559-d11040691b8d")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Syphilis testP", sdd.definition("Syphilis testP", sdd.getConcept("3c5aa2a6-ca7a-478c-b559-d11040691b8d")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("TT", sdd.definition("TT", sdd.getConcept("39217e3d-6a39-4679-bf56-f0954a7ffdb8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new TetanusDataConverter());
        dsd.addColumn("IPT/CTX", sdd.definition("IPT/CTX", sdd.getConcept("1da3cb98-59d8-4bfd-b0bb-c9c1bcd058c6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new IptCtxDataConverter());
        dsd.addColumn("Referal In/Out", sdd.referredToOrFrom(), "onDate=${endDate}", new CalculationResultDataConverter());
        dsd.addColumn("ART Code", sdd.definition("ART Code", sdd.getConcept("a615f932-26ee-449c-8e20-e50a15232763")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ARVsDataConverter());
        dsd.addColumn("Linkage ART No", sdd.definition("Linkage ART No", sdd.getConcept("9db2900d-2b44-4629-bdf8-bf25de650577")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("FPC", sdd.definition("FPC", sdd.getConcept("0815c786-5994-49e4-aa07-28b662b0e428")), "onOrAfter=${startDate},onOrBefore=${endDate}", new FpcDataConverter());
        dsd.addColumn("Follow Up Date", sdd.definition("Follow Up Date", sdd.getConcept("6eb65cbd-4eab-4343-810b-b831144954e4")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Follow Up Attempts", sdd.definition("Follow Up Attempts", sdd.getConcept("b8b3433e-3e9d-42c2-bc1b-1e60421d09d7")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Follow Up Action", sdd.definition("Follow Up Action", sdd.getConcept("928c4617-436e-44b3-91c3-725cb1c910c0")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Follow Up Outcome", sdd.definition("Follow Up Outcome", sdd.getConcept("8f889d84-8e5c-4a66-970d-458d6d01e8a4")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("End Quarter Outcome", sdd.definition("End Quarter Outcome", sdd.getConcept("cd14792c-03e3-47ac-9e08-846ab1d93c87")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Next Quarter Outcome", sdd.definition("Next Quarter Outcome", sdd.getConcept("04afa790-1600-4078-818d-4a71a014f073")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Care Return Date", sdd.definition("Care Return Date", sdd.getConcept("d2d437fb-ff88-44c2-bf17-1d35bde6f586")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Self Transfer Date", sdd.definition("Self Transfer Date", sdd.getConcept("fc1b1e96-4afb-423b-87e5-bb80d451c967")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Death Cause", sdd.definition("Death Cause", sdd.getConcept("dca2c3f2-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        dsd.addColumn("Followup Comment", sdd.definition("Followup Comment", sdd.getConcept("159395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}",new ObsDataConverter());
        
        
        return dsd;
    }

}
