package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.ArticleForm;
import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    SessionRepository sessionRepository;

    // GET - ziskanie udajov o clanku
    @RequestMapping(value = "/articles/full/{id}", method = RequestMethod.GET)
    public ResponseEntity getArticle(
            @PathVariable long id) {

        Optional<Article> article;

        article = articleRepository.findById(id);

        if (article != null)
            return ResponseEntity.status(200).body(article);
        else
            return ResponseEntity.status(400).body("Invalid ID");
    }

    //TODO pridat tagy k clanku

    // POST - vytvorenie noveho clanku
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    public ResponseEntity createArticle(@RequestHeader String token,
                                        @RequestBody ArticleForm articleForm) {

        if (articleForm == null)
            return ResponseEntity.status(400).body("Missing attributes.");

        long bloggerId = articleForm.getBlogger_id();

        Optional<Session> session;
        //TODO tu je chyba...session nenajde
        session = sessionRepository.findByBloggerId(bloggerId);

        System.out.println("\n\ntttooookkkeeeennnnn>>>>>>>>>>>>>>>>>>>> " + session + "\n\n");

        if (session.isPresent()) {
            String sessionToken = session.get().getToken();
            if (token.compareTo(sessionToken) == 0) {

                String title = articleForm.getTitle();
                String articleText = articleForm.getArticle_text();
                Date published = Calendar.getInstance().getTime();
                int likes = 0;

                Article article;
                article = new Article(bloggerId, title, articleText, published, likes);
                articleRepository.save(article);

                return ResponseEntity.status(201).body("");
            } else
                return ResponseEntity.status(401).body("");
        } else
            return ResponseEntity.status(400).body("session is not present...");
    }

    // PUT - uprava udajov clanku


    // DELETE - vymazanie clanku
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteArticle(@RequestHeader String token,
                                        @PathVariable long id) {      // id clanku !!!

        Optional<Article> articleToDelete;
        articleToDelete = articleRepository.findById(id);

        if (!articleToDelete.isPresent()) {
            return ResponseEntity.status(400).body("Article does not exist.");
        } else {
            Optional<Session> session;
            long blogger_id = articleToDelete.get().getBloggerId();
            session = sessionRepository.findByBloggerId(blogger_id);
            //TODO treba pridat moznost Forbidden to delete this article

            System.out.println("\n\ntttooookkkeeeennnnn>>>>>>>>>>>>>>>>>>>> " + session + "\n\n");

            if (session.isPresent()) {
                String sessionToken = session.get().getToken();

                if (token.compareTo(sessionToken) == 0) {
                    articleRepository.delete(articleToDelete.get());
                    return ResponseEntity.status(200).body("Article was successfully deleted.");
                } else
                    return ResponseEntity.status(401).body("");
            }
            return ResponseEntity.status(400).body("session is not present");
        }
    }

        // GET - ziskanie dlazdic urcitej kategorie clankov

        // GET - ziskanie obrazku z clanku

        // POST - pridanie komentarov

        // POST - pridanie reakcii na komentar

        // PUT - zmena obrazku v clanku

}