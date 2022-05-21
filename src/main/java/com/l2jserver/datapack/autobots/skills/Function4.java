package com.l2jserver.datapack.autobots.skills;

@FunctionalInterface
public interface Function4<One, Two, Three, Four> {
    Four apply(One one, Two two, Three three);
}
