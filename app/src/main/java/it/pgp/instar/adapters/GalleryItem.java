package it.pgp.instar.adapters;

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
}
