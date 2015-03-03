package lan.dk.podcastserver.manager.worker.updater;

import lan.dk.podcastserver.entity.Item;
import lan.dk.podcastserver.entity.Podcast;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.function.Predicate;


public interface Updater {

    public Pair<Podcast, Set<Item>> update(Podcast podcast);

    public Set<Item> getItems(Podcast podcast);

    public Podcast updateAndAddItems(Podcast podcast);

    public Podcast findPodcast(String url);

    public String generateSignature(Podcast podcast);

    public default Predicate<Item> notIn(Podcast podcast) {
        return item -> !podcast.contains(item);
    }
}
