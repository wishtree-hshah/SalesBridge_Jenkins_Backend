package com.wishtreetech.clutch.service;

import com.wishtreetech.clutch.entity.ScrappingUrl;
import com.wishtreetech.clutch.repository.ScrappingUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrappingUrlServiceImpl implements ScrappingUrlService {
    @Autowired
    private ScrappingUrlRepository scrappingUrlRepository;

    @Override
    public void addUrl(ScrappingUrl scrappingUrl) {
        scrappingUrlRepository.save(scrappingUrl);
    }

    @Override
    public List<ScrappingUrl> getAllUrls() {
        return scrappingUrlRepository.findAll();
    }

    @Override
    public void deleteUrl(long id) {
        scrappingUrlRepository.deleteById(id);
    }
}
