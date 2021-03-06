package com.craftmend.openaudiomc.spigot.modules.regions.interfaces;

import com.craftmend.openaudiomc.spigot.modules.regions.RegionModule;
import com.craftmend.openaudiomc.spigot.modules.regions.objects.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractRegionAdapter {

    private RegionModule regionModule;

    public AbstractRegionAdapter(RegionModule regionModule) {
        this.regionModule = regionModule;
    }

    public abstract Set<ProtectedRegion> getRegionsAtLocation(Location location);
    public abstract boolean doesRegionExist(String name);

    public List<IRegion> getAudioRegions(Location location) {
        List<IRegion> regions = new ArrayList<>();
        int prio = 0;
        for (ProtectedRegion r : getRegionsAtLocation(location)) {
            if (regionModule.getRegionPropertiesMap().get(r.getId()) == null) continue;
            if (r.getPriority() > prio) {
                prio = r.getPriority();
                regions.clear();
            }
            regions.add(new Region(r.getId(), regionModule.getRegionPropertiesMap().get(r.getId())));
        }
        return regions;
    }

}
