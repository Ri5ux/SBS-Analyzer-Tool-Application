package com.arisux.sbsat;

public class AnalyzerDevice
{
    private String model;
    private int revision;
    
    public AnalyzerDevice()
    {
        this.model = "Generic";
        this.revision = 1;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public void setRevision(int revision)
    {
        this.revision = revision;
    }
    
    public String getModel()
    {
        return model;
    }
    
    public int getRevision()
    {
        return revision;
    }
}