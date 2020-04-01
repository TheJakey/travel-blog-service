package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.ArticleForm;
import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.jvm.hotspot.ui.EditableAtEndDocument;

import java.text.SimpleDateFormat;
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
            return ResponseEntity.status(200).body("Article was found.");
        else
            return ResponseEntity.status(400).body("Invalid ID");
    }

    // POST - vytvorenie noveho clanku
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    public ResponseEntity createArticle(@RequestHeader String token,
                                        @RequestBody ArticleForm articleForm){

        if (articleForm == null)
            return ResponseEntity.status(400).body("Missing attributes.");

        long bloggerId = articleForm.getBloggerId();

        Optional<Session> session;
        session = sessionRepository.findByBloggerId(bloggerId);

        if (token.compareTo(session.get().getToken()) == 0){

            String title = articleForm.getTitle();
            String articleText = articleForm.getArticleText();
            Date published = Calendar.getInstance().getTime();
            int likes = 0;

            Article article;
            article = new Article(bloggerId, title, articleText, published, likes);
            articleRepository.save(article);

            return ResponseEntity.status(201).body("New article was created.");
        }
        else
            //TODO: moze sa stat, ze z nejaakeho dovodu nevieme vytvorit novy clanok?
            return ResponseEntity.status(400).body("Failed to create article.");
    }

    // PUT - uprava udajov clanku


    // DELETE - vymazanie clanku



    // GET - ziskanie dlazdic urcitej kategorie clankov

    // GET - ziskanie obrazku z clanku

    // POST - pridanie komentarov

    // POST - pridanie reakcii na komentar

    // PUT - zmena obrazku v clanku




}