package com.typinggame.util;

import javafx.scene.image.Image;
import java.net.URL;

public class RankLoader {
    private RankLoader() {}

    public static Image loadIcon(Rank rankBadge) {
        String resourcePath = rankBadge.resourcePath;
        URL url = Rank.class.getResource(resourcePath); // leading "/" required
        if (url == null) {
            throw new IllegalStateException("Missing resource: " + resourcePath);
        }
        return new Image(url.toExternalForm());
    }
}