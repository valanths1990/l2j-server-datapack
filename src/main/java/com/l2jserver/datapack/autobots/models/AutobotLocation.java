package com.l2jserver.datapack.autobots.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.Location;

import java.util.Objects;

public class AutobotLocation {
    private int x;
    private int y;
    private int z;

    public AutobotLocation(@JsonProperty("x") int x, @JsonProperty("y")int y, @JsonProperty("z")int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public final Location getLocation(){
        return new Location(x,y,z);
    }
    public final AutobotLocation copy(int x, int y, int z) {
        return new AutobotLocation(x, y, z);
    }

    public final int getX() {
        return this.x;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final int getY() {
        return this.y;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public final int getZ() {
        return this.z;
    }

    public final void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutobotLocation that = (AutobotLocation) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "AutobotLocation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
