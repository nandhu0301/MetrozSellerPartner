package com.smiligenceUAT1.metrozsellerpartner.bean;

public class CategoryDetailsNew {
    String categoryid;
    String categoryName;
    String categoryImage;
    String categoryCreatedDate;
    String categoryPriority;
    private Boolean value;

    public String getCategoryPriority()
    {
        return categoryPriority;
    }

    public void setCategoryPriority(String categoryPriority) {
        this.categoryPriority = categoryPriority;
    }

    public CategoryDetailsNew() {
        super ();
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getCategoryCreatedDate() {
        return categoryCreatedDate;
    }

    public void setCategoryCreatedDate(String categoryCreatedDate) {
        this.categoryCreatedDate = categoryCreatedDate;
    }


    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }


}
