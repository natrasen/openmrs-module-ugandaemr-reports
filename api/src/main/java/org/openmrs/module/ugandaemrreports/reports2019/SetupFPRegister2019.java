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
public class SetupFPRegister2019 extends UgandaEMRDataExportManager {

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
        return "82269afd-2438-11ec-9e14-00ffcd58c42d";
    }

    public String getCSVDesignUuid()
    {
        return "8b7f2cf6-2438-11ec-9e14-00ffcd58c42d";
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
        ReportDesign rd = createExcelTemplateDesign(getExcelDesignUuid(), reportDefinition, "FPRegister2019.xls");
        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:11-14,dataset:FP");
        props.put("sortWeight", "5000");
        rd.setProperties(props);
        return rd;
    }

    @Override
    public String getUuid() {
        return "97581b09-2438-11ec-9e14-00ffcd58c42d";
    }

    @Override
    public String getName() {
        return "Intergrated FP Register 2019";
    }

    @Override
    public String getDescription() {
        return "It contains Family Planning Information";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        ReportDefinition rd = new ReportDefinition();

        rd.setUuid(getUuid());
        rd.setName(getName());
        rd.setDescription(getDescription());
        rd.addParameters(getParameters());
        rd.addDataSetDefinition("FP", Mapped.mapStraightThrough(dataSetDefinition()));
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
        dsd.setName("FP");
        dsd.addParameters(getParameters());
        
        //start adding columns here
      
        dsd.addColumn("Clinic number", sdd.definition("Clinic number", sdd.getConcept("a6217c17-5012-4514-b9f2-5d02d3d04c44")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Serial Number", sdd.definition("Serial Number", sdd.getConcept("1646AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("NIN", sdd.getNationalIDNumber(), "");
        dsd.addColumn("Name of Client", new PreferredNameDataDefinition(), (String) null);
        dsd.addColumn("Village", basePatientDataLibrary.getVillage(),(String)null);
        dsd.addColumn("Parish", basePatientDataLibrary.getParish(),(String)null);
        dsd.addColumn("County", basePatientDataLibrary.getCounty(),(String)null);
        dsd.addColumn("District", basePatientDataLibrary.getDistrict(),(String)null);
        dsd.addColumn("Nationality", new PersonAttributeDataDefinition("Nationality", sdd.getPatientNationality()), "", new NationalityPersonalAttributeDataConverter());
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition("Phone Number", sdd.getPhoneNumber()), "", new PersonAttributeDataConverter());
        dsd.addColumn("Age", sdd.getAgeDataDefinition(10,200), "onDate=${endDate}", new CalculationResultDataConverter());        
        dsd.addColumn("Sex", sdd.definition("Sex",  sdd.getConcept("ab505422-26d9-41f1-a079-c3d222000440")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("New User", sdd.definition("New User",  sdd.getConcept("51ad6e56-cdea-4065-866e-96fbf1db16d6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Revisit", sdd.definition("Revisit",  sdd.getConcept("51ad6e56-cdea-4065-866e-96fbf1db16d6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Previous Method", sdd.definition("Previous Method",  sdd.getConcept("dc7620b3-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("FP Counselling", sdd.definition("FP Counselling",  sdd.getConcept("b92b1777-4356-49b2-9c83-a799680dc7d4")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("No Method Given", sdd.definition("No Method Given",  sdd.getConcept("aaf150a5-92d2-416f-8254-95d34ed9c4ab")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Swithing Method", sdd.definition("Swithing Method",  sdd.getConcept("4ee29b85-7160-437a-b609-20fa4099dd12")), "onOrAfter=${startDate},onOrBefore=${endDate}", new MUACDataConverter());
        dsd.addColumn("Switching Reason", sdd.definition("Switching Reason",  sdd.getConcept("c5099d8b-5d13-4ae8-a1ef-71a39184e6e6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Lofeminal", sdd.definition("Lofeminal",  sdd.getConcept("165004AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Microgynon", sdd.definition("Microgynon", sdd.getConcept("104625AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Ovrette", sdd.definition("Ovrette", sdd.getConcept("0b434cfa-b11c-4d14-aaa2-9aed6ca2da88")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Levonorgestrel", sdd.definition("Levonorgestrel", sdd.getConcept("410c4c1a-37ff-4c2a-a3ad-6e8860c91455")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Other Oral", sdd.definition("Other Oral",  sdd.getConcept("97199c54-74aa-43d4-b216-e9774133d4fe")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Emergency Contraceptives", sdd.definition("Emergency Contraceptives",  sdd.getConcept("160570AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Male condom", sdd.definition("Male condom",  sdd.getConcept("164813AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Female condom", sdd.definition("Female condom",  sdd.getConcept("164814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DMPA IM", sdd.definition("DMPA IM", sdd.getConcept("dcb30ba3-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("DMPA SC", sdd.definition("DMPA SC",  sdd.getConcept("dcb30ba3-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Implants", sdd.definition("Implants", sdd.getConcept("27cfe3c6-b69e-4c8d-9e6d-5ddb964ae236")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Implant removal", sdd.definition("Implant removal", sdd.getConcept("f0fa81a9-1a59-406c-9efc-f7de2a8f7c64")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Implant Removal Reason", sdd.definition("Implant Removal Reason", sdd.getConcept("43a8727f-1466-4517-af51-c108c02674a8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Vasectomy", sdd.definition("Vasectomy", sdd.getConcept("263809c2-c9d5-40b3-ace3-3e41c2065f21")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Copper-T", sdd.definition("Copper-T", sdd.getConcept("6a6bf16a-0cb7-4c9b-bce7-fe822e25a7fa")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Hormonal", sdd.definition("Hormonal", sdd.getConcept("a407a3ca-c5f2-4e80-bc7a-e261b49efeb3")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("IUD REMOVAL", sdd.definition("IUD REMOVAL", sdd.getConcept("d7822c3f-d196-4a9d-809d-4b6e307b7b8d")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("IUD Removal Reason", sdd.definition("IUD Removal Reason", sdd.getConcept("340cc067-c59c-4187-859a-170ca31e0fe8")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Post partum FP", sdd.definition("Post partum FP", sdd.getConcept("9b692d61-7815-4bba-a919-246455f9aa62")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Standard days", sdd.definition("Standard days", sdd.getConcept("3797e4ad-1bef-4df8-beb0-af5c2bd46257")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("LAM", sdd.definition("LAM", sdd.getConcept("5a51b880-700d-4c90-bf5e-5c0b6f3cee6b")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ARVsDataConverter());
        dsd.addColumn("Two day method", sdd.definition("Two day method", sdd.getConcept("d0d68bd3-0845-4d04-a189-2e76c001f200")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Side effect codes", sdd.definition("Side effect codes", sdd.getConcept("ad3177b6-7ef9-4147-a1d5-9b895c1d7dc4")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Cancer Cervix Screening", sdd.definition("Cancer Cervix Screening", sdd.getConcept("5029d903-51ba-4c44-8745-e97f320739b6")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Breast Cancer Screening", sdd.definition("Breast Cancer Screening", sdd.getConcept("b2493ffb-0ce9-429c-b756-9a2842f440b5")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("HCT code", sdd.definition("HCT code", sdd.getConcept("efff658e-1db1-4c18-b113-435272ba462d")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("HIV Positive First FP", sdd.definition("HIV Positive First FP", sdd.getConcept("165003AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("ARVS", sdd.definition("ARVS", sdd.getConcept("dd2b0b4d-30ab-102d-86b0-7a5022ba4115")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("STIs Screened", sdd.definition("STIs Screened", sdd.getConcept("2c48beab-1fc0-4000-91e0-523f8d22f0b5")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("SGBV Screened", sdd.definition("SGBV Screened", sdd.getConcept("c205d5a8-8c2a-4405-9ea1-0da9b8362071")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Referrals", sdd.definition("Referrals", sdd.getConcept("48d526ec-5bba-4475-ac51-79b37ceb79ef")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());
        dsd.addColumn("Remarks", sdd.definition("Remarks", sdd.getConcept("bd86c483-2548-4d74-8322-0d2093787959")), "onOrAfter=${startDate},onOrBefore=${endDate}", new ObsDataConverter());        
        
        
        return dsd;
    }

}
