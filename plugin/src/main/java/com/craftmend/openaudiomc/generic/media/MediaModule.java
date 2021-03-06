package com.craftmend.openaudiomc.generic.media;

import com.craftmend.openaudiomc.generic.media.interfaces.ForcedUrlMutation;
import com.craftmend.openaudiomc.generic.media.interfaces.UrlMutation;
import com.craftmend.openaudiomc.generic.media.middleware.DropBoxMiddleware;
import com.craftmend.openaudiomc.generic.media.middleware.GoogleDriveMiddleware;
import com.craftmend.openaudiomc.generic.media.middleware.SoundCloudMiddleware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaModule {

    private Map<String, List<UrlMutation>> urlMutations = new HashMap<>();

    public MediaModule() {
        // register default mutations
        registerMutation("https://drive.google.com", new GoogleDriveMiddleware());
        registerMutation("https://www.dropbox.com", new DropBoxMiddleware());
        registerMutation("https://soundcloud.com", new SoundCloudMiddleware());
    }

    public void registerMutation(String host, UrlMutation urlMutation) {
        List<UrlMutation> list = urlMutations.getOrDefault(host, new ArrayList<>());
        list.add(urlMutation);
        urlMutations.put(host, list);
    }

    /**
     * Process the url trough the mutation api
     *
     * @param original the original url
     * @return the altered url
     */
    public String process(String original) {
        for (String selector : urlMutations.keySet()) {
            if (original.startsWith(selector)) {
                for (UrlMutation urlMutation : urlMutations.get(selector)) {
                    if (urlMutation instanceof ForcedUrlMutation) {
                        original = urlMutation.onRequest(original);
                    }
                }
            }
        }

        for (String selector : urlMutations.keySet()) {
            if (original.startsWith(selector)) {
                for (UrlMutation urlMutation : urlMutations.get(selector)) {
                    if (!(urlMutation instanceof ForcedUrlMutation))
                    original = urlMutation.onRequest(original);
                }
            }
        }

        return original;
    }

}
