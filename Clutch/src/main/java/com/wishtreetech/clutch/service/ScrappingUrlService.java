package com.wishtreetech.clutch.service;

import com.wishtreetech.clutch.entity.ScrappingUrl;

import java.util.List;

public interface ScrappingUrlService {
    public void addUrl(ScrappingUrl scrappingUrl);
    public List<ScrappingUrl> getAllUrls();
    public void deleteUrl(long id);
}
