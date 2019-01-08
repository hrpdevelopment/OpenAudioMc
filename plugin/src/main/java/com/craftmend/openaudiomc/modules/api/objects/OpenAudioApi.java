package com.craftmend.openaudiomc.modules.api.objects;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.modules.players.interfaces.ClientConnection;
import com.craftmend.openaudiomc.modules.regions.objects.IRegion;
import com.craftmend.openaudiomc.modules.speakers.objects.SimpleLocation;
import com.craftmend.openaudiomc.modules.speakers.objects.Speaker;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OpenAudioApi {

    public ClientConnection getClient(UUID uuid) {
        return OpenAudioMc.getInstance().getPlayerModule().getClient(uuid);
    }

    public ClientConnection getClient(Player player) {
        return OpenAudioMc.getInstance().getPlayerModule().getClient(player.getUniqueId());
    }

    public List<IRegion> getRegion(Location location) {
        return OpenAudioMc.getInstance().getRegionModule().getRegions(location);
    }

    public Speaker getSpeaker(Location location) {
        return OpenAudioMc.getInstance().getSpeakerModule().getSpeaker(new SimpleLocation(location));
    }

}