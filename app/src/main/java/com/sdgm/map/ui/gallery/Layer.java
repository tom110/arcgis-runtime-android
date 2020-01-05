package com.sdgm.map.ui.gallery;

public class Layer {
    private String code;
    private String name;

    public Layer(String code, String name, boolean selected) {
        this.code = code;
        this.name = name;
        this.selected = selected;
    }

    private boolean selected;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
