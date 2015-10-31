/**
 * Created by kevin on 02/11@/14 for Podcast Server
 */

import RestangularConfig from '../../../config/restangular.config';

class PodcastService  {

    constructor(Restangular) {
        "ngInject";
        this.Restangular = Restangular;
        this.route = 'podcast';
    }

    findById(podcastId) {
        return this.Restangular.one(this.route, podcastId).get();
    }

    findAll() {
        return this.Restangular.all(this.route).getList();
    }

    save(podcast) {
        return podcast.save();
    }

    getNewPodcast() {
        return this.Restangular.one(this.route);
    }

    patch(item) {
        return item.patch();
    }

    deletePodcast(item) {
        return item.remove();
    }

    findInfo(url) {
        return this.Restangular.one(this.route).findInfo(url);
    }

    statsByPubdate(id, numberOfMonth = 6) {
        return this.Restangular.one(this.route, id).one('stats').all('byPubdate').post(numberOfMonth);
    }

    statsByByDownloaddate(id, numberOfMonth = 6) {
        return this.Restangular.one(this.route, id).one('stats').all('byDownloaddate').post(numberOfMonth);
    }

    static config(RestangularProvider) {
        "ngInject";
        RestangularProvider.addElementTransformer('podcast', false, (podcast) => {
            podcast.addRestangularMethod('findInfo', 'post', 'fetch', undefined, {'Content-Type': 'text/plain'});
            return podcast;
        });
    }
}

export default angular.module('ps.common.service.data.podcastService', [
    RestangularConfig.name
])
    .config(PodcastService.config)
    .service('podcastService', PodcastService);