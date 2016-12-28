package com.seventhmoon.tennisumpire.Data;


public class FileChooseItem implements Comparable<FileChooseItem> {
    private String fileName;
    private String date;


    public FileChooseItem(String n,String date)
    {
        super();
        this.fileName = n;
        this.date = date;
    }

    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(FileChooseItem o) {
        if(this.fileName != null)
            return this.fileName.toLowerCase().compareTo(o.getFileName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
