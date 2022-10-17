package com.leih.url.link.manager;

import com.leih.url.link.entity.Link;

public interface ShortLinkManager {
    boolean addShortLink(Link shortLink);
    Link findShortLinkByCode(String linkCode);
    boolean deleteShortLink(Link shortLink);

    boolean updateShortLink(Link updatedLink);

}
