package it.pgp.instar.adapters;

import java.io.File;

public class GalleryItem {
    public String filepath;
    public boolean selected;

    public GalleryItem(String filepath, boolean selected) {
        this.filepath = filepath;
        this.selected = selected;
    }

    public GalleryItem(String filepath) {
        this.filepath = filepath;
        this.selected = false;
    }

    public boolean toggleSelection() {
        selected = !selected;
        return selected;
    }

    public File getFile() {
        return new File(filepath);
    }
}
