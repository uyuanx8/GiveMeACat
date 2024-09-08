package com.example.givemeacat;

import java.util.ArrayList;

public class ImageData {
    ArrayList< String > tags = new ArrayList < String > ();
    private String createdAt;
    private String updatedAt;
    private String mimetype;
    private float size;
    private String _id;


    // Getter Methods

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getMimetype() {
        return mimetype;
    }

    public float getSize() {
        return size;
    }

    public String get_id() {
        return _id;
    }

    public ArrayList getTags(){
        return tags;
    }

// Setter Methods

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }
}
