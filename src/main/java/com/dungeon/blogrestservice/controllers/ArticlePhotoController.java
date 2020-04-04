package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.ArticlePhoto;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.ArticlePhotoRepository;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ArticlePhotoController {

    @Autowired
    ArticlePhotoRepository articlePhotoRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ArticleRepository articleRepository;

    @RequestMapping(value = "/articles/{article_id}/photos/{photo_position}", method = RequestMethod.GET)
    public ResponseEntity getPhotos(@PathVariable long article_id, @PathVariable int photo_position) {
        HttpHeaders headers = new HttpHeaders();
        Optional<Article> optionalArticle = articleRepository.findById(article_id);
        List<ArticlePhoto> articlePhotos = articlePhotoRepository.findAllByArticleId(article_id);
        int numberOfPhotos = articlePhotos.size();
        byte[] photo;

        if(!optionalArticle.isPresent())
            return ResponseEntity.status(400).body("Non-existing id");

//        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        if (numberOfPhotos != 0 && numberOfPhotos > photo_position) {
            photo = articlePhotos.get(photo_position).getPhoto();
            return ResponseEntity.status(200).contentType(MediaType.parseMediaType("image/jpeg")).cacheControl(CacheControl.noCache()).body(photo);
        }
        else
            return ResponseEntity.status(400).body("Invalid position");
    }

    @RequestMapping(value = "/articles/{article_id}/photos/{blogger_id}", method = RequestMethod.POST)
    public ResponseEntity createPhoto(
            @PathVariable Long article_id,
            @PathVariable Long blogger_id,
            @RequestHeader(value = "token") String token,
            @RequestBody byte[] photo
    ) {
//        long article_id = 3;
//        long blogger_id = 5;
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(blogger_id);
        Session session;
        SessionHandler sessionHandler = new SessionHandler(blogger_id, token, optionalSession);
        ArticlePhoto articlePhoto = new ArticlePhoto();

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to upload this photo");

        articlePhoto.setArticleId(article_id);
        articlePhoto.setPhoto(photo);

        articlePhotoRepository.save(articlePhoto);

//        if (optionalBlogger.isPresent())
//            blogger = optionalBlogger.get();
//        else
//            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");


        return ResponseEntity.status(201).body("");
    }
}
