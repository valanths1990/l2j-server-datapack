package com.l2jserver.datapack.custom.bounty.model;

public class BountyHolder {
    private int charId;
    private double pvpBounty;
    private double pkBounty;
    private double assistBounty;

    public BountyHolder(int charId, double pvpBounty, double pkBounty, double assistBounty) {
        this.charId = charId;
        this.pvpBounty = pvpBounty;
        this.pkBounty = pkBounty;
        this.assistBounty = assistBounty;
    }
    public BountyHolder(){
        
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public double getPvpBounty() {
        return pvpBounty;
    }

    public void setPvpBounty(double pvpBounty) {
        this.pvpBounty = pvpBounty;
    }

    public double getPkBounty() {
        return pkBounty;
    }

    public void setPkBounty(double pkBounty) {
        this.pkBounty = pkBounty;
    }

    public double getAssistBounty() {
        return assistBounty;
    }

    public void setAssistBounty(double assistBounty) {
        this.assistBounty = assistBounty;
    }

}
