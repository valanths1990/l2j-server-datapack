package com.l2jserver.datapack.autobots.behaviors.preferences;

public interface PetOwnerPreferences {
    boolean getPetAssists();

    void setPetAssists(boolean petAssists);

    boolean getSummonPet();

    void setSummonPet(boolean summonPet);

    boolean getPetUsesShots();

    void setPetUsesShots(boolean petUsesShots);

    int getPetShotId();

    void setPetShotId(int petShotId);

    boolean getPetHasBuffs();

    void setPetHasBuffs(boolean petHasBuffs);
}
