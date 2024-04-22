package com.khanhpham.managerclassroom.models;

public class DeviceClassroom {
    private boolean light;
    private boolean fan;
    private boolean projector;
    private boolean air_conditioner;

    public DeviceClassroom(boolean light, boolean fan, boolean projector, boolean air_conditioner) {
        this.light = light;
        this.fan = fan;
        this.projector = projector;
        this.air_conditioner = air_conditioner;
    }

    public DeviceClassroom(){

    }
    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public boolean isFan() {
        return fan;
    }

    public void setFan(boolean fan) {
        this.fan = fan;
    }

    public boolean isProjector() {
        return projector;
    }

    public void setProjector(boolean projector) {
        this.projector = projector;
    }

    public boolean isAir_conditioner() {
        return air_conditioner;
    }

    public void setAir_conditioner(boolean air_conditioner) {
        this.air_conditioner = air_conditioner;
    }
}
